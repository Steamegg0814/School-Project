// MongoDB Initialization Script for Dog Health System
// Run this in MongoDB Shell or Compass

use doghealth;

// Create indexes for better performance
db.breed_info.createIndex({ "breed": 1 }, { unique: true });
db.breed_info.createIndex({ "aliases": 1 });
db.dogs.createIndex({ "userId": 1 });
db.dogs.createIndex({ "breed": 1 });
db.dogs.createIndex({ "tags": 1 });
db.health_records.createIndex({ "dogId": 1 });
db.health_records.createIndex({ "userId": 1 });
db.health_records.createIndex({ "recordedAt": -1 });

print("Indexes created successfully!");

// Sample breed data (optional - the app will auto-initialize)
db.breed_info.insertMany([
  {
    breed: "Golden Retriever",
    aliases: ["黃金獵犬", "金毛尋回犬"],
    commonDiseases: ["Hip Dysplasia", "Cancer", "Heart Disease", "Eye Problems"],
    temperament: "Friendly, Intelligent, Devoted",
    averageWeight: 30.0,
    averageLifespan: 12,
    exerciseNeeds: ["High energy", "Daily walks 60+ minutes", "Swimming", "Fetch games"],
    dietaryRecommendations: ["High-quality protein", "Omega-3 fatty acids", "Joint supplements", "Portion control"],
    description: "Golden Retrievers are friendly and devoted dogs, great for families.",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    breed: "Labrador Retriever",
    aliases: ["拉布拉多", "拉不拉多"],
    commonDiseases: ["Obesity", "Hip Dysplasia", "Eye Problems", "Exercise-Induced Collapse"],
    temperament: "Outgoing, Even Tempered, Gentle",
    averageWeight: 32.0,
    averageLifespan: 12,
    exerciseNeeds: ["Very high energy", "Running", "Fetch", "Swimming"],
    dietaryRecommendations: ["Portion control essential", "Low-fat diet", "Multiple small meals", "Avoid free-feeding"],
    description: "Labradors are outgoing and gentle, perfect for active families.",
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

print("Sample data inserted successfully!");

// Verify data
print("Total breeds:", db.breed_info.countDocuments());
print("Sample breed:", db.breed_info.findOne());
