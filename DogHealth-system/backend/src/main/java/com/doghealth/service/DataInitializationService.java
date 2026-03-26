package com.doghealth.service;

import com.doghealth.model.BreedInfo;
import com.doghealth.repository.BreedInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitializationService implements CommandLineRunner {
    
    private final BreedInfoRepository breedInfoRepository;
    
    @Override
    public void run(String... args) {
        initializeBreedData()
                .subscribe(
                        count -> log.info("Breed data initialization completed. Total breeds: {}", count),
                        error -> log.error("Failed to initialize breed data", error)
                );
    }
    
    public Flux<Long> initializeBreedData() {
        return breedInfoRepository.count()
                .flatMapMany(count -> {
                    if (count > 0) {
                        log.info("Breed data already exists. Skipping initialization.");
                        return Flux.just(count);
                    }
                    
                    log.info("Initializing breed data...");
                    return createDefaultBreeds()
                            .flatMap(breedInfoRepository::save)
                            .count()
                            .flux();
                });
    }
    
    private Flux<BreedInfo> createDefaultBreeds() {
        return Flux.just(
                // Golden Retriever
                BreedInfo.create(
                        "Golden Retriever",
                        List.of("黃金獵犬", "金毛尋回犬"),
                        List.of("Hip Dysplasia", "Cancer", "Heart Disease", "Eye Problems"),
                        "Friendly, Intelligent, Devoted",
                        30.0,
                        12,
                        List.of("High energy", "Daily walks 60+ minutes", "Swimming", "Fetch games"),
                        List.of("High-quality protein", "Omega-3 fatty acids", "Joint supplements", "Portion control")
                ).get(),
                
                // Labrador Retriever
                BreedInfo.create(
                        "Labrador Retriever",
                        List.of("拉布拉多", "拉不拉多"),
                        List.of("Obesity", "Hip Dysplasia", "Eye Problems", "Exercise-Induced Collapse"),
                        "Outgoing, Even Tempered, Gentle",
                        32.0,
                        12,
                        List.of("Very high energy", "Running", "Fetch", "Swimming"),
                        List.of("Portion control essential", "Low-fat diet", "Multiple small meals", "Avoid free-feeding")
                ).get(),
                
                // Poodle
                BreedInfo.create(
                        "Poodle",
                        List.of("貴賓犬", "貴婦犬"),
                        List.of("Addison's Disease", "Bloat", "Epilepsy", "Hip Dysplasia"),
                        "Intelligent, Active, Alert, Trainable",
                        25.0,
                        14,
                        List.of("Medium to high energy", "Mental stimulation", "Daily walks", "Interactive play"),
                        List.of("High-quality kibble", "Regular meal schedule", "Avoid table scraps", "Dental care treats")
                ).get(),
                
                // Chihuahua
                BreedInfo.create(
                        "Chihuahua",
                        List.of("吉娃娃", "奇娃娃"),
                        List.of("Dental Issues", "Heart Problems", "Patellar Luxation", "Hypoglycemia"),
                        "Devoted, Alert, Quick, Courageous",
                        2.5,
                        16,
                        List.of("Low to medium energy", "Short walks", "Indoor play", "Avoid overexertion"),
                        List.of("Small breed formula", "Frequent small meals", "Dental care critical", "Avoid obesity")
                ).get(),
                
                // German Shepherd
                BreedInfo.create(
                        "German Shepherd",
                        List.of("德國牧羊犬", "狼犬"),
                        List.of("Hip Dysplasia", "Elbow Dysplasia", "Bloat", "Degenerative Myelopathy"),
                        "Confident, Courageous, Intelligent",
                        35.0,
                        11,
                        List.of("High energy", "Daily exercise 90+ minutes", "Mental challenges", "Working activities"),
                        List.of("Large breed formula", "Glucosamine for joints", "Multiple meals to prevent bloat", "High protein")
                ).get(),
                
                // Bulldog
                BreedInfo.create(
                        "Bulldog",
                        List.of("鬥牛犬", "英國鬥牛犬"),
                        List.of("Breathing Problems", "Hip Dysplasia", "Cherry Eye", "Skin Infections"),
                        "Calm, Courageous, Friendly, Dignified",
                        23.0,
                        8,
                        List.of("Low energy", "Short walks", "Avoid heat and humidity", "Gentle exercise"),
                        List.of("Weight management critical", "Moderate protein", "Skin health supplements", "Small frequent meals")
                ).get(),
                
                // Beagle
                BreedInfo.create(
                        "Beagle",
                        List.of("米格魯", "小獵犬"),
                        List.of("Obesity", "Epilepsy", "Hip Dysplasia", "Ear Infections"),
                        "Friendly, Curious, Merry",
                        12.0,
                        13,
                        List.of("Medium to high energy", "Scent work", "Daily walks", "Secure yard"),
                        List.of("Portion control", "Food-motivated training treats", "Regular meals", "Avoid overfeeding")
                ).get(),
                
                // Shiba Inu
                BreedInfo.create(
                        "Shiba Inu",
                        List.of("柴犬", "柴狗"),
                        List.of("Allergies", "Hip Dysplasia", "Patellar Luxation", "Eye Problems"),
                        "Alert, Active, Attentive, Faithful",
                        10.0,
                        14,
                        List.of("Medium energy", "Daily walks", "Mental stimulation", "Secure area (escape artists)"),
                        List.of("High-quality protein", "Fish-based for allergies", "Grain-free options", "Fresh vegetables")
                ).get(),
                
                // Corgi (Pembroke Welsh Corgi)
                BreedInfo.create(
                        "Pembroke Welsh Corgi",
                        List.of("柯基", "威爾斯柯基犬"),
                        List.of("Hip Dysplasia", "Degenerative Myelopathy", "Obesity", "Eye Problems"),
                        "Affectionate, Smart, Alert, Playful",
                        12.0,
                        13,
                        List.of("Medium energy", "Herding activities", "Daily walks", "Weight-bearing exercise"),
                        List.of("Weight management essential", "Back health support", "Portion control", "Joint supplements")
                ).get(),
                
                // Pomeranian
                BreedInfo.create(
                        "Pomeranian",
                        List.of("博美犬", "松鼠犬"),
                        List.of("Dental Issues", "Patellar Luxation", "Tracheal Collapse", "Alopecia"),
                        "Inquisitive, Bold, Lively",
                        3.0,
                        14,
                        List.of("Medium energy", "Short walks", "Indoor play", "Socialization"),
                        List.of("Small breed formula", "Dental care treats", "Frequent small meals", "High-quality ingredients")
                ).get()
        );
    }
}
