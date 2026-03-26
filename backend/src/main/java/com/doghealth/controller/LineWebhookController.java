package com.doghealth.controller;

import com.doghealth.model.BreedInfo;
import com.doghealth.model.Dog;
import com.doghealth.service.DogBreedService;
import com.doghealth.service.LLMService;
import com.doghealth.service.RERCalculatorService;
import com.linecorp.bot.messaging.client.MessagingApiClient;
import com.linecorp.bot.messaging.model.ReplyMessageRequest;
import com.linecorp.bot.messaging.model.TextMessage;
import com.linecorp.bot.webhook.model.Event;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.event.message.TextMessageContent;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/callback")
@RequiredArgsConstructor
public class LineWebhookController {
    
    private final DogBreedService dogBreedService;
    private final RERCalculatorService rerCalculatorService;
    private final LLMService llmService;
    private final messagingApiClient;
    
    private static final Pattern DOG_REGISTER_PATTERN = 
            Pattern.compile("註冊\\s+(\\S+)\\s+(\\d+\\.?\\d*)\\s+(\\d+)");
    
    @PostMapping
    public Mono<Void> handleWebhook(@RequestBody String body) {
        return Mono.fromRunnable(() -> 
                log.info("Received Line webhook: {}", body))
                .then();
    }
    
    @PostMapping("/events")
    public Mono<Void> handleEvents(@RequestBody List<Event> events) {
        return Mono.fromIterable(events)
                .flatMap(this::handleEvent)
                .then()
                .onErrorResume(e -> {
                    log.error("Error handling events", e);
                    return Mono.empty();
                });
    }
    
    private Mono<Void> handleEvent(Event event) {
        if (event instanceof MessageEvent messageEvent) {
            if (messageEvent.message() instanceof TextMessageContent textMessage) {
                return handleTextMessage(messageEvent, textMessage);
            }
        }
        return Mono.empty();
    }
    
    private Mono<Void> handleTextMessage(
            MessageEvent event, 
            TextMessageContent message) {
        
        String userId = event.source().userId();
        String text = message.text();
        String replyToken = event.replyToken();
        
        log.info("Received message from {}: {}", userId, text);
        
        return processCommand(userId, text)
                .flatMap(response -> replyMessage(replyToken, response))
                .onErrorResume(e -> {
                    log.error("Error processing message", e);
                    return replyMessage(replyToken, "處理訊息時發生錯誤：" + e.getMessage());
                });
    }
    
    private Mono<String> processCommand(String userId, String text) {
        // 註冊狗狗：註冊 黃金獵犬 30 5
        Matcher registerMatcher = DOG_REGISTER_PATTERN.matcher(text);
        if (registerMatcher.find()) {
            String breed = registerMatcher.group(1);
            Double weight = Double.parseDouble(registerMatcher.group(2));
            Integer age = Integer.parseInt(registerMatcher.group(3));
            
            return registerDog(userId, breed, weight, age);
        }
        
        // 查詢我的狗
        if (text.contains("我的狗") || text.contains("查詢")) {
            return getUserDogs(userId);
        }
        
        // 犬種資訊
        if (text.startsWith("犬種 ")) {
            String breed = text.substring(3).trim();
            return getBreedInfo(breed);
        }
        
        // 健康建議
        if (text.startsWith("建議 ")) {
            String dogId = text.substring(3).trim();
            return getHealthRecommendation(dogId);
        }
        
        // 預設幫助訊息
        return Mono.just("""
                歡迎使用犬隻健康管理系統！
                
                使用說明：
                • 註冊 [犬種] [體重kg] [年齡] - 註冊新狗狗
                  範例：註冊 黃金獵犬 30 5
                
                • 我的狗 - 查詢你的所有狗狗
                
                • 犬種 [名稱] - 查詢犬種資訊
                  範例：犬種 黃金獵犬
                
                • 建議 [狗狗ID] - 獲取健康建議
                
                試試看吧！
                """);
    }
    
    private Mono<String> registerDog(
            String userId, 
            String breed, 
            Double weight, 
            Integer age) {
        
        return dogBreedService.createDog(userId, breed, weight, age)
                .flatMap(dog -> rerCalculatorService.calculateRER(dog)
                        .map(rer -> String.format("""
                                ✅ 成功註冊狗狗！
                                
                                犬種：%s
                                體重：%.2f kg
                                年齡：%d 歲
                                RER：%.2f kcal/day
                                
                                狗狗ID：%s
                                
                                輸入「建議 %s」獲取健康建議
                                """,
                                dog.getBreed(),
                                dog.getWeight(),
                                dog.getAge(),
                                rer,
                                dog.getId(),
                                dog.getId()
                        )));
    }
    
    private Mono<String> getUserDogs(String userId) {
        return dogBreedService.getDogsByUserId(userId)
                .collectList()
                .map(dogs -> {
                    if (dogs.isEmpty()) {
                        return "你還沒有註冊任何狗狗。\n使用「註冊 [犬種] [體重] [年齡]」來註冊。";
                    }
                    
                    StringBuilder sb = new StringBuilder("你的狗狗：\n\n");
                    dogs.forEach(dog -> sb.append(String.format("""
                            🐕 %s
                            犬種：%s
                            體重：%.2f kg
                            年齡：%d 歲
                            ID：%s
                            
                            """,
                            dog.getBreed(),
                            dog.getBreed(),
                            dog.getWeight(),
                            dog.getAge(),
                            dog.getId()
                    )));
                    
                    return sb.toString();
                });
    }
    
    private Mono<String> getBreedInfo(String breed) {
        return dogBreedService.getBreedInfo(breed)
                .map(info -> String.format("""
                        🐕 %s 犬種資訊
                        
                        常見疾病：
                        %s
                        
                        性格：%s
                        平均體重：%.2f kg
                        平均壽命：%d 年
                        
                        運動需求：%s
                        飲食建議：%s
                        """,
                        info.getBreed(),
                        String.join("\n• ", info.getCommonDiseases()),
                        info.getTemperament(),
                        info.getAverageWeight(),
                        info.getAverageLifespan(),
                        String.join(", ", info.getExerciseNeeds()),
                        String.join(", ", info.getDietaryRecommendations())
                ));
    }
    
    private Mono<String> getHealthRecommendation(String dogId) {
        return dogBreedService.getDogById(dogId)
                .flatMap(dog -> 
                    Mono.zip(
                            dogBreedService.getBreedInfo(dog.getBreed()),
                            rerCalculatorService.calculateRER(dog)
                    )
                    .flatMap(tuple -> {
                        BreedInfo breedInfo = tuple.getT1();
                        Double rer = tuple.getT2();
                        
                        return llmService.getHealthRecommendation(dog, breedInfo, rer)
                                .map(recommendation -> String.format("""
                                        🐕 健康建議報告
                                        
                                        犬種：%s
                                        體重：%.2f kg
                                        年齡：%d 歲
                                        RER：%.2f kcal/day
                                        
                                        %s
                                        """,
                                        dog.getBreed(),
                                        dog.getWeight(),
                                        dog.getAge(),
                                        rer,
                                        recommendation
                                ));
                    })
                );
    }
    
    private Mono<Void> replyMessage(String replyToken, String messageText) {
        return Try.of(() -> {
            ReplyMessageRequest request = ReplyMessageRequest.builder()
                    .replyToken(replyToken)
                    .messages(List.of(new TextMessage(messageText)))
                    .build();
            
            return messagingApiClient.replyMessage(request);
        })
        .map(Mono::fromFuture)
        .getOrElseGet(e -> {
            log.error("Failed to reply message", e);
            return Mono.empty();
        })
        .then();
    }
}
