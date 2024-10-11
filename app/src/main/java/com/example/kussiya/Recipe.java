package com.example.kussiya;

public class Recipe {
    private String recipeId;
    private String userId;
    private String recipeName;
    private String description;
    private String imageUrl;
    private String category;
    private String videoUrl; // <-- Add category field

    // Default constructor
    public Recipe() {
    }


    // Parameterized constructor
    public Recipe(String recipeId, String userId, String recipeName, String description, String imageUrl, String category, String videoUrl) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.recipeName = recipeName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.videoUrl = videoUrl;
    }

    // Getter and Setter for category
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Other getters and setters
    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
