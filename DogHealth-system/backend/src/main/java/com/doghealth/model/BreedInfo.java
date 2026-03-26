package com.doghealth.model;

import io.vavr.control.Try;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@With
@Document(collection = "breed_info")
public class BreedInfo {
    
    @Id
    String id;
    
    @Indexed(unique = true)
    String breed;
    
    List<String> aliases;  // 別名，例如 ["黃金獵犬", "Golden Retriever"]
    List<String> commonDiseases;
    String temperament;
    Double averageWeight;
    Integer averageLifespan;
    List<String> exerciseNeeds;
    List<String> dietaryRecommendations;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    
    public static Try<BreedInfo> create(
            String breed,
            List<String> aliases,
            List<String> commonDiseases,
            String temperament,
            Double averageWeight,
            Integer averageLifespan,
            List<String> exerciseNeeds,
            List<String> dietaryRecommendations) {
        
        return Try.of(() -> {
            validateBreed(breed);
            validateWeight(averageWeight);
            validateLifespan(averageLifespan);
            
            return BreedInfo.builder()
                    .breed(breed)
                    .aliases(aliases)
                    .commonDiseases(commonDiseases)
                    .temperament(temperament)
                    .averageWeight(averageWeight)
                    .averageLifespan(averageLifespan)
                    .exerciseNeeds(exerciseNeeds)
                    .dietaryRecommendations(dietaryRecommendations)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        });
    }
    
    public boolean matchesBreedName(String searchBreed) {
        if (breed.equalsIgnoreCase(searchBreed)) {
            return true;
        }
        
        if (aliases != null) {
            return aliases.stream()
                    .anyMatch(alias -> alias.equalsIgnoreCase(searchBreed));
        }
        
        return false;
    }
    
    private static void validateBreed(String breed) {
        if (breed == null || breed.isBlank()) {
            throw new IllegalArgumentException("Breed name cannot be empty");
        }
    }
    
    private static void validateWeight(Double weight) {
        if (weight != null && (weight <= 0 || weight > 200)) {
            throw new IllegalArgumentException("Average weight must be between 0 and 200 kg");
        }
    }
    
    private static void validateLifespan(Integer lifespan) {
        if (lifespan != null && (lifespan <= 0 || lifespan > 30)) {
            throw new IllegalArgumentException("Average lifespan must be between 0 and 30 years");
        }
    }
}
