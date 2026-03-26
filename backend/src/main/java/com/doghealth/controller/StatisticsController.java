package com.doghealth.controller;

import com.doghealth.service.DogBreedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatisticsController {
    
    private final DogBreedService dogBreedService;
    
    @GetMapping("/breeds")
    public Mono<Map<String, Long>> getBreedStatistics() {
        log.info("Fetching breed statistics");
        return dogBreedService.getBreedStatistics()
                .doOnSuccess(stats -> log.info("Breed statistics retrieved: {}", stats));
    }
    
    @GetMapping("/health-concerns/{breed}")
    public Mono<Map<String, Object>> getHealthConcerns(@PathVariable String breed) {
        log.info("Fetching health concerns for breed: {}", breed);
        return dogBreedService.getCommonHealthConcerns(breed)
                .map(concerns -> Map.of(
                        "breed", breed,
                        "healthConcerns", concerns
                ))
                .doOnSuccess(result -> log.info("Health concerns retrieved for: {}", breed));
    }
}
