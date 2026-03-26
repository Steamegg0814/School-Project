package com.doghealth.model;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DogTest {
    
    @Test
    void shouldCreateValidDog() {
        // Given
        String userId = "user123";
        String breed = "Golden Retriever";
        Double weight = 30.0;
        Integer age = 5;
        
        // When
        Try<Dog> result = Dog.create(userId, breed, weight, age);
        
        // Then
        assertTrue(result.isSuccess());
        Dog dog = result.get();
        assertEquals(userId, dog.getUserId());
        assertEquals(breed, dog.getBreed());
        assertEquals(weight, dog.getWeight());
        assertEquals(age, dog.getAge());
        assertNotNull(dog.getCreatedAt());
        assertNotNull(dog.getUpdatedAt());
    }
    
    @Test
    void shouldFailWithEmptyUserId() {
        // When
        Try<Dog> result = Dog.create("", "Golden Retriever", 30.0, 5);
        
        // Then
        assertTrue(result.isFailure());
        assertInstanceOf(IllegalArgumentException.class, result.getCause());
    }
    
    @Test
    void shouldFailWithNegativeWeight() {
        // When
        Try<Dog> result = Dog.create("user123", "Golden Retriever", -5.0, 5);
        
        // Then
        assertTrue(result.isFailure());
        assertInstanceOf(IllegalArgumentException.class, result.getCause());
    }
    
    @Test
    void shouldFailWithInvalidAge() {
        // When
        Try<Dog> result = Dog.create("user123", "Golden Retriever", 30.0, -1);
        
        // Then
        assertTrue(result.isFailure());
        assertInstanceOf(IllegalArgumentException.class, result.getCause());
    }
    
    @Test
    void shouldCalculateRERCorrectly() {
        // Given
        Dog dog = Dog.builder()
                .userId("user123")
                .breed("Golden Retriever")
                .weight(30.0)
                .age(5)
                .build();
        
        // When
        Try<Double> result = dog.calculateRER();
        
        // Then
        assertTrue(result.isSuccess());
        Double rer = result.get();
        // RER = 70 * (30^0.75) = 70 * 15.59 ≈ 1091.3
        assertTrue(rer > 1000 && rer < 1200);
    }
    
    @Test
    void shouldCalculateDERCorrectly() {
        // Given
        Dog dog = Dog.builder()
                .userId("user123")
                .breed("Golden Retriever")
                .weight(30.0)
                .age(5)
                .build();
        Double activityFactor = 1.6;
        
        // When
        Try<Double> result = dog.calculateDER(activityFactor);
        
        // Then
        assertTrue(result.isSuccess());
        Double der = result.get();
        // DER = RER * 1.6 ≈ 1091.3 * 1.6 ≈ 1746
        assertTrue(der > 1600 && der < 1900);
    }
    
    @Test
    void shouldAddTagSuccessfully() {
        // Given
        Dog dog = Dog.builder()
                .userId("user123")
                .breed("Golden Retriever")
                .weight(30.0)
                .age(5)
                .build();
        
        // When
        Dog updatedDog = dog.addTag("vaccinated");
        
        // Then
        assertNotNull(updatedDog.getTags());
        assertTrue(updatedDog.getTags().contains("vaccinated"));
    }
    
    @Test
    void shouldNotAddDuplicateTag() {
        // Given
        Dog dog = Dog.builder()
                .userId("user123")
                .breed("Golden Retriever")
                .weight(30.0)
                .age(5)
                .build();
        
        // When
        Dog dog1 = dog.addTag("vaccinated");
        Dog dog2 = dog1.addTag("vaccinated");
        
        // Then
        assertEquals(1, dog2.getTags().size());
    }
}
