package com.doghealth.model;

import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BreedInfoTest {
    
    @Test
    void shouldCreateValidBreedInfo() {
        // Given
        String breed = "Golden Retriever";
        List<String> aliases = List.of("黃金獵犬");
        List<String> diseases = List.of("Hip Dysplasia", "Cancer");
        String temperament = "Friendly";
        Double weight = 30.0;
        Integer lifespan = 12;
        List<String> exercise = List.of("High");
        List<String> diet = List.of("High-quality protein");
        
        // When
        Try<BreedInfo> result = BreedInfo.create(
                breed, aliases, diseases, temperament, 
                weight, lifespan, exercise, diet);
        
        // Then
        assertTrue(result.isSuccess());
        BreedInfo info = result.get();
        assertEquals(breed, info.getBreed());
        assertEquals(aliases, info.getAliases());
        assertEquals(diseases, info.getCommonDiseases());
        assertNotNull(info.getCreatedAt());
    }
    
    @Test
    void shouldFailWithEmptyBreedName() {
        // When
        Try<BreedInfo> result = BreedInfo.create(
                "", List.of(), List.of(), "Friendly",
                30.0, 12, List.of(), List.of());
        
        // Then
        assertTrue(result.isFailure());
        assertInstanceOf(IllegalArgumentException.class, result.getCause());
    }
    
    @Test
    void shouldFailWithInvalidWeight() {
        // When
        Try<BreedInfo> result = BreedInfo.create(
                "Test Breed", List.of(), List.of(), "Friendly",
                -10.0, 12, List.of(), List.of());
        
        // Then
        assertTrue(result.isFailure());
        assertInstanceOf(IllegalArgumentException.class, result.getCause());
    }
    
    @Test
    void shouldMatchBreedByExactName() {
        // Given
        BreedInfo info = BreedInfo.builder()
                .breed("Golden Retriever")
                .aliases(List.of("黃金獵犬"))
                .build();
        
        // When & Then
        assertTrue(info.matchesBreedName("Golden Retriever"));
        assertTrue(info.matchesBreedName("golden retriever"));
    }
    
    @Test
    void shouldMatchBreedByAlias() {
        // Given
        BreedInfo info = BreedInfo.builder()
                .breed("Golden Retriever")
                .aliases(List.of("黃金獵犬", "金毛"))
                .build();
        
        // When & Then
        assertTrue(info.matchesBreedName("黃金獵犬"));
        assertTrue(info.matchesBreedName("金毛"));
    }
    
    @Test
    void shouldNotMatchDifferentBreed() {
        // Given
        BreedInfo info = BreedInfo.builder()
                .breed("Golden Retriever")
                .aliases(List.of("黃金獵犬"))
                .build();
        
        // When & Then
        assertFalse(info.matchesBreedName("Labrador"));
        assertFalse(info.matchesBreedName("Poodle"));
    }
}
