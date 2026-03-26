package com.doghealth.controller;

import com.doghealth.model.BreedInfo;
import com.doghealth.service.DogBreedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/breeds")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BreedInfoController {
    
    private final DogBreedService dogBreedService;
    
    @GetMapping
    public Flux<BreedInfo> getAllBreeds() {
        log.info("Fetching all breeds");
        return dogBreedService.getAllBreeds()
                .doOnNext(breed -> log.debug("Retrieved breed: {}", breed.getBreed()));
    }
    
    @GetMapping("/{breedName}")
    public Mono<BreedInfo> getBreedInfo(@PathVariable String breedName) {
        log.info("Fetching breed info for: {}", breedName);
        return dogBreedService.getBreedInfo(breedName)
                .doOnSuccess(info -> log.info("Found breed info: {}", info.getBreed()));
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BreedInfo> createBreedInfo(@RequestBody BreedInfo breedInfo) {
        log.info("Creating breed info for: {}", breedInfo.getBreed());
        return dogBreedService.createBreedInfo(breedInfo)
                .doOnSuccess(created -> log.info("Created breed info: {}", created.getId()));
    }
    
    @GetMapping("/search")
    public Flux<BreedInfo> searchBreeds(@RequestParam String query) {
        log.info("Searching breeds with query: {}", query);
        return dogBreedService.getAllBreeds()
                .filter(breed -> breed.matchesBreedName(query))
                .doOnNext(breed -> log.debug("Match found: {}", breed.getBreed()));
    }
}
