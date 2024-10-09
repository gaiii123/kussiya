package com.example.kussiya;

public class Recipe {
    private String recipeId;
    private String userId;
    private String recipeName;
    private String description;
    private String imageUrl;
    private String category;

    public Recipe() {
        // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    }

    public Recipe(String recipeId, String userId, String recipeName, String description, String imageUrl, String category) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.recipeName = recipeName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Getters and Setters

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
