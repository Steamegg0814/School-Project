package com.doghealth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class DogHealthApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DogHealthApplication.class, args);
    }
}
