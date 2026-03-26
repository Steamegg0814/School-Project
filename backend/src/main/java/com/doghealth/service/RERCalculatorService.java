package com.doghealth.service;

import com.doghealth.exception.DogHealthException;
import com.doghealth.exception.ErrorCode;
import com.doghealth.model.Dog;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class RERCalculatorService {
    
    private static final Map<String, Double> ACTIVITY_FACTORS = Map.of(
            "neutered_adult", 1.6,
            "intact_adult", 1.8,
            "light_work", 2.0,
            "moderate_work", 3.0,
            "heavy_work", 4.0,
            "puppy", 2.5,
            "senior", 1.4,
            "weight_loss", 1.0,
            "weight_gain", 1.8
    );
    
    public Mono<Double> calculateRER(Dog dog) {
        return Mono.fromCallable(() ->
            dog.calculateRER()
                    .getOrElseThrow(e -> new DogHealthException(
                            ErrorCode.CALCULATION_ERROR,
                            "Failed to calculate RER: " + e.getMessage(),
                            e))
        )
        .doOnSuccess(rer -> log.info("RER calculated for dog {}: {} kcal/day", 
                dog.getId(), rer));
    }
    
    public Mono<Double> calculateDER(Dog dog, String activityLevel) {
        return Mono.fromCallable(() -> {
            Double activityFactor = ACTIVITY_FACTORS.get(activityLevel);
            if (activityFactor == null) {
                throw new DogHealthException(
                        ErrorCode.INVALID_INPUT,
                        "Invalid activity level: " + activityLevel);
            }
            
            return dog.calculateDER(activityFactor)
                    .getOrElseThrow(e -> new DogHealthException(
                            ErrorCode.CALCULATION_ERROR,
                            "Failed to calculate DER: " + e.getMessage(),
                            e));
        })
        .doOnSuccess(der -> log.info("DER calculated for dog {}: {} kcal/day", 
                dog.getId(), der));
    }
    
    public Mono<Map<String, Double>> calculateAllMetrics(Dog dog) {
        return calculateRER(dog)
                .flatMap(rer -> {
                    // Calculate DER for multiple activity levels
                    return Mono.just(Map.of(
                            "rer", rer,
                            "der_neutered", rer * ACTIVITY_FACTORS.get("neutered_adult"),
                            "der_intact", rer * ACTIVITY_FACTORS.get("intact_adult"),
                            "der_puppy", rer * ACTIVITY_FACTORS.get("puppy"),
                            "der_senior", rer * ACTIVITY_FACTORS.get("senior"),
                            "der_weight_loss", rer * ACTIVITY_FACTORS.get("weight_loss")
                    ));
                })
                .doOnSuccess(metrics -> log.info("All metrics calculated for dog: {}", 
                        dog.getId()))
                .onErrorMap(e -> !(e instanceof DogHealthException),
                        e -> new DogHealthException(
                                ErrorCode.CALCULATION_ERROR,
                                "Failed to calculate metrics",
                                e));
    }
    
    public Mono<String> getRecommendedActivityLevel(Dog dog) {
        return Mono.fromCallable(() -> {
            if (dog.getAge() == null) {
                return "neutered_adult";
            }
            
            if (dog.getAge() < 1) {
                return "puppy";
            } else if (dog.getAge() > 7) {
                return "senior";
            } else {
                return "neutered_adult";
            }
        })
        .doOnSuccess(level -> log.debug("Recommended activity level for dog {}: {}", 
                dog.getId(), level));
    }
}
