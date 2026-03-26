package com.doghealth.service;

import com.doghealth.model.BreedInfo;
import com.doghealth.exception.DogHealthException;
import com.doghealth.exception.ErrorCode;
import com.doghealth.model.Dog;
import com.doghealth.repository.DogRepository;
import com.doghealth.repository.BreedInfoRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DogBreedService {
    
    private final DogRepository dogRepository;
    private final BreedInfoRepository breedInfoRepository;
    
    public Mono<Dog> createDog(String userId, String breed, Double weight, Integer age) {
        return Mono.fromCallable(() -> 
            Dog.create(userId, breed, weight, age)
                    .getOrElseThrow(e -> new DogHealthException(
                            ErrorCode.INVALID_INPUT, 
                            "Failed to create dog: " + e.getMessage(), 
                            e))
        )
        .flatMap(dog -> 
            dogRepository.save(dog)
                    .doOnSuccess(savedDog -> log.info("Dog created: {}", savedDog.getId()))
                    .onErrorMap(e -> new DogHealthException(
                            ErrorCode.DATABASE_ERROR,
                            "Failed to save dog",
                            e))
        );
    }
    
    public Mono<Dog> getDogById(String dogId) {
        return dogRepository.findById(dogId)
                .switchIfEmpty(Mono.error(new DogHealthException(
                        ErrorCode.DOG_NOT_FOUND,
                        "Dog not found with id: " + dogId)))
                .doOnSuccess(dog -> log.debug("Retrieved dog: {}", dogId))
                .onErrorMap(e -> !(e instanceof DogHealthException), 
                        e -> new DogHealthException(
                                ErrorCode.DATABASE_ERROR,
                                "Failed to retrieve dog",
                                e));
    }
    
    public Flux<Dog> getDogsByUserId(String userId) {
        return dogRepository.findByUserId(userId)
                .doOnNext(dog -> log.debug("Found dog for user {}: {}", userId, dog.getId()))
                .onErrorMap(e -> new DogHealthException(
                        ErrorCode.DATABASE_ERROR,
                        "Failed to retrieve dogs for user",
                        e));
    }
    
    public Mono<BreedInfo> getBreedInfo(String breedName) {
        return breedInfoRepository.findByBreedIgnoreCase(breedName)
                .switchIfEmpty(
                    // 嘗試用別名搜尋
                    breedInfoRepository.findByBreedOrAliasContaining(breedName)
                            .next()
                )
                .switchIfEmpty(
                    // 如果找不到，返回錯誤
                    Mono.error(new DogHealthException(
                            ErrorCode.BREED_NOT_FOUND,
                            "Breed information not found for: " + breedName))
                )
                .doOnSuccess(info -> log.info("Retrieved breed info for: {} (matched: {})", 
                        breedName, info.getBreed()))
                .onErrorMap(e -> !(e instanceof DogHealthException),
                        e -> new DogHealthException(
                                ErrorCode.DATABASE_ERROR,
                                "Failed to get breed info from database",
                                e));
    }
    
    public Flux<BreedInfo> getAllBreeds() {
        return breedInfoRepository.findAllByOrderByBreedAsc()
                .doOnNext(breed -> log.debug("Found breed: {}", breed.getBreed()))
                .onErrorMap(e -> new DogHealthException(
                        ErrorCode.DATABASE_ERROR,
                        "Failed to retrieve all breeds",
                        e));
    }
    
    public Mono<BreedInfo> createBreedInfo(BreedInfo breedInfo) {
        return breedInfoRepository.save(breedInfo)
                .doOnSuccess(saved -> log.info("Breed info created: {}", saved.getBreed()))
                .onErrorMap(e -> new DogHealthException(
                        ErrorCode.DATABASE_ERROR,
                        "Failed to save breed info",
                        e));
    }
    
    public Mono<Dog> addTagToDog(String dogId, String tag) {
        return getDogById(dogId)
                .map(dog -> Try.of(() -> dog.addTag(tag))
                        .getOrElseThrow(e -> new DogHealthException(
                                ErrorCode.INVALID_INPUT,
                                "Failed to add tag",
                                e)))
                .flatMap(dog -> dogRepository.save(dog)
                        .doOnSuccess(savedDog -> log.info("Tag added to dog {}: {}", dogId, tag))
                        .onErrorMap(e -> new DogHealthException(
                                ErrorCode.DATABASE_ERROR,
                                "Failed to save tag",
                                e)));
    }
    
    public Mono<Map<String, Long>> getBreedStatistics() {
        return dogRepository.findAll()
                .collect(Collectors.groupingBy(
                        Dog::getBreed,
                        Collectors.counting()))
                .map(stats -> {
                    log.info("Breed statistics calculated: {} breeds", stats.size());
                    return stats;
                })
                .onErrorMap(e -> new DogHealthException(
                        ErrorCode.DATABASE_ERROR,
                        "Failed to calculate breed statistics",
                        e));
    }
    
    public Mono<List<String>> getCommonHealthConcerns(String breed) {
        return getBreedInfo(breed)
                .map(BreedInfo::getCommonDiseases)
                .doOnSuccess(concerns -> log.info("Retrieved {} health concerns for {}", 
                        concerns.size(), breed));
    }
}
