package com.doghealth.repository;

import com.doghealth.model.BreedInfo;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BreedInfoRepository extends ReactiveMongoRepository<BreedInfo, String> {
    
    Mono<BreedInfo> findByBreedIgnoreCase(String breed);
    
    @Query("{ $or: [ { 'breed': { $regex: ?0, $options: 'i' } }, { 'aliases': { $regex: ?0, $options: 'i' } } ] }")
    Flux<BreedInfo> findByBreedOrAliasContaining(String searchTerm);
    
    @Query("{ 'aliases': ?0 }")
    Mono<BreedInfo> findByAlias(String alias);
    
    Flux<BreedInfo> findAllByOrderByBreedAsc();
}
