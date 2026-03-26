package com.doghealth.repository;

import com.doghealth.model.HealthRecord;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface HealthRecordRepository extends ReactiveMongoRepository<HealthRecord, String> {
    
    Flux<HealthRecord> findByDogId(String dogId);
    
    Flux<HealthRecord> findByUserId(String userId);
}
