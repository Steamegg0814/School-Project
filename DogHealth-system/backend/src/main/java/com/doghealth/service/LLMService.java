package com.doghealth.service;

import com.doghealth.model.BreedInfo;
import com.doghealth.exception.DogHealthException;
import com.doghealth.exception.ErrorCode;
import com.doghealth.model.Dog;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LLMService {
    
    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    
    public LLMService(
            @Value("${anthropic.api.base-url}") String baseUrl,
            @Value("${anthropic.api.key}") String apiKey,
            @Value("${anthropic.api.model}") String model) {
        
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    
    @CircuitBreaker(name = "llmService", fallbackMethod = "getRecommendationFallback")
    public Mono<String> getHealthRecommendation(Dog dog, BreedInfo breedInfo, Double rer) {
        String prompt = buildPrompt(dog, breedInfo, rer);
        
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", 1024,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );
        
        return webClient.post()
                .uri("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> extractContentFromResponse(response))
                .doOnSuccess(recommendation -> 
                        log.info("LLM recommendation received for dog: {}", dog.getId()))
                .doOnError(e -> 
                        log.error("LLM API call failed for dog: {}", dog.getId(), e))
                .onErrorMap(e -> new DogHealthException(
                        ErrorCode.LLM_SERVICE_ERROR,
                        "Failed to get LLM recommendation",
                        e));
    }
    
    private String buildPrompt(Dog dog, BreedInfo breedInfo, Double rer) {
        return String.format("""
                作為犬隻健康顧問，請提供以下狗狗的健康建議：
                
                犬種：%s
                年齡：%d 歲
                體重：%.2f kg
                RER (靜息能量需求)：%.2f kcal/day
                
                已知該犬種常見疾病：%s
                
                請提供：
                1. 飲食建議（考慮 RER 和犬種特性）
                2. 運動建議
                3. 需要特別注意的健康問題
                4. 預防保健建議
                
                請用繁體中文回答，簡潔明瞭。
                """,
                breedInfo.getBreed(),
                dog.getAge(),
                dog.getWeight(),
                rer,
                String.join(", ", breedInfo.getCommonDiseases())
        );
    }
    
    @SuppressWarnings("unchecked")
    private String extractContentFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> content = 
                    (List<Map<String, Object>>) response.get("content");
            
            if (content != null && !content.isEmpty()) {
                return (String) content.get(0).get("text");
            }
            
            throw new DogHealthException(
                    ErrorCode.LLM_SERVICE_ERROR,
                    "Invalid response format from LLM");
        } catch (ClassCastException e) {
            throw new DogHealthException(
                    ErrorCode.LLM_SERVICE_ERROR,
                    "Failed to parse LLM response",
                    e);
        }
    }
    
    private Mono<String> getRecommendationFallback(
            Dog dog, 
            BreedInfo breedInfo, 
            Double rer, 
            Exception ex) {
        
        log.warn("Circuit breaker activated for LLM service, using fallback", ex);
        
        return Mono.just(String.format("""
                【系統建議 - LLM 服務暫時無法使用】
                
                基本飲食建議：
                - 每日建議熱量：%.2f kcal (RER)
                - 成犬一般建議：RER × 1.6 = %.2f kcal
                
                犬種資訊：%s
                常見健康問題：%s
                
                建議諮詢獸醫以獲得更詳細的個人化建議。
                """,
                rer,
                rer * 1.6,
                breedInfo.getBreed(),
                String.join(", ", breedInfo.getCommonDiseases())
        ));
    }
}
