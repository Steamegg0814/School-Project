package com.doghealth.model;

import io.vavr.control.Try;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Value
@Builder
@With
@Document(collection = "health_records")
public class HealthRecord {
    
    @Id
    String id;
    String dogId;
    String userId;
    Double calculatedRER;
    Double calculatedDER;
    String recommendation;
    LocalDateTime recordedAt;
    
    public static Try<HealthRecord> create(
            String dogId, 
            String userId, 
            Double rer, 
            Double der,
            String recommendation) {
        
        return Try.of(() -> {
            if (dogId == null || dogId.isBlank()) {
                throw new IllegalArgumentException("Dog ID cannot be empty");
            }
            if (userId == null || userId.isBlank()) {
                throw new IllegalArgumentException("User ID cannot be empty");
            }
            
            return HealthRecord.builder()
                    .dogId(dogId)
                    .userId(userId)
                    .calculatedRER(rer)
                    .calculatedDER(der)
                    .recommendation(recommendation)
                    .recordedAt(LocalDateTime.now())
                    .build();
        });
    }
}
