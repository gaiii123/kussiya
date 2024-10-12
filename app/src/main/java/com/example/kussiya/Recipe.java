package com.example.kussiya;

public class Recipe {
    private String recipeId;
    private String userId;
    private String recipeName;
    private String description;
    private String imageUrl;
    private String category;
    private String videoUrl;  // Path for video (optional)

    // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    public Recipe() {
    }

    // Constructor with image and video URLs
    public Recipe(String recipeId, String userId, String recipeName, String description, String imageUrl, String category, String videoUrl) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.recipeName = recipeName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.videoUrl = videoUrl;
    }

    // Getters and Setters
    public String getRecipeId() {
        return recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
