package com.doghealth.repository;

import com.doghealth.model.Dog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DogRepository extends ReactiveMongoRepository<Dog, String> {
    
    Flux<Dog> findByUserId(String userId);
    
    Flux<Dog> findByBreed(String breed);
    
    Flux<Dog> findByTagsContaining(String tag);
    
    Mono<Long> countByBreed(String breed);
}
