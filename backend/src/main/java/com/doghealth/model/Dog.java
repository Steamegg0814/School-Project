package com.doghealth.model;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Value
@Builder
@With
@Document(collection = "dogs")
public class Dog {
    
    @Id
    String id;
    String userId;
    String breed;
    Double weight;
    Integer age;
    List<String> tags;
    List<String> healthConcerns;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    
    public static Try<Dog> create(String userId, String breed, Double weight, Integer age) {
        return Try.of(() -> {
            validateUserId(userId);
            validateBreed(breed);
            validateWeight(weight);
            validateAge(age);
            
            return Dog.builder()
                    .userId(userId)
                    .breed(breed)
                    .weight(weight)
                    .age(age)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        });
    }
    
    public Try<Double> calculateRER() {
        return Try.of(() -> {
            if (weight == null || weight <= 0) {
                throw new IllegalStateException("Invalid weight for RER calculation");
            }
            return 70 * Math.pow(weight, 0.75);
        });
    }
    
    public Try<Double> calculateDER(Double activityFactor) {
        return calculateRER()
                .flatMap(rer -> Try.of(() -> {
                    if (activityFactor == null || activityFactor <= 0) {
                        throw new IllegalArgumentException("Activity factor must be positive");
                    }
                    return rer * activityFactor;
                }));
    }
    
    public Option<List<String>> getHealthConcerns() {
        return Option.of(healthConcerns);
    }
    
    public Dog addTag(String tag) {
        List<String> updatedTags = new ArrayList<>(Option.of(tags)
                .getOrElse(List.of()));
        
        if (!updatedTags.contains(tag)) {
            updatedTags.add(tag);
        }
        
        return this.withTags(updatedTags)
                .withUpdatedAt(LocalDateTime.now());
    }
    
    private static void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
    }
    
    private static void validateBreed(String breed) {
        if (breed == null || breed.isBlank()) {
            throw new IllegalArgumentException("Breed cannot be empty");
        }
    }
    
    private static void validateWeight(Double weight) {
        if (weight == null || weight <= 0 || weight > 200) {
            throw new IllegalArgumentException("Weight must be between 0 and 200 kg");
        }
    }
    
    private static void validateAge(Integer age) {
        if (age == null || age < 0 || age > 30) {
            throw new IllegalArgumentException("Age must be between 0 and 30 years");
        }
    }
}
