package com.example.kussiya;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Kussiya.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RecipiesAdapter extends RecyclerView.Adapter<RecipiesAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private String currentUserId;
    private Context context;

    public RecipiesAdapter(List<Recipe> recipeList, String currentUserId) {
        this.recipeList = recipeList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        context = parent.getContext();
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeDescription.setText(recipe.getDescription());

        // Load image using Glide
        Glide.with(holder.recipeImage.getContext())
                .load(recipe.getImageUrl())
                .into(holder.recipeImage);

        // Handle item click to navigate to the recipe view screen
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeViewActivity.class);
            intent.putExtra("RECIPE_NAME", recipe.getRecipeName());
            intent.putExtra("RECIPE_DESCRIPTION", recipe.getDescription());
            intent.putExtra("RECIPE_IMAGE_URL", recipe.getImageUrl());
            intent.putExtra("RECIPE_VIDEO_URL", recipe.getVideoUrl());

            context.startActivity(intent);
        });


        // Reference to the user's favorite section in Firebase
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("favorites")
                .child(recipe.getRecipeId());

        // Check if the recipe is already a favorite
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Recipe is already a favorite
                    holder.favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_24);  // Filled heart icon
                    holder.favouriteButton.setTag("favorited");  // Set a tag to track the state
                } else {
                    // Recipe is not a favorite
                    holder.favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_border_24);  // Outline heart icon
                    holder.favouriteButton.setTag("not_favorited");  // Set a tag to track the state
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors.
            }
        });

        // Toggle favorite status on button click
        holder.favouriteButton.setOnClickListener(v -> {
            if (holder.favouriteButton.getTag().equals("not_favorited")) {
                // Add to favorites
                favRef.setValue(recipe)
                        .addOnSuccessListener(aVoid -> {
                            holder.favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_24);  // Filled heart icon
                            holder.favouriteButton.setTag("favorited");
                            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Remove from favorites
                favRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            holder.favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_border_24);  // Outline heart icon
                            holder.favouriteButton.setTag("not_favorited");
                            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Only show edit/delete buttons if the recipe belongs to the current user
        if (recipe.getUserId().equals(currentUserId)) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.editButton.setOnClickListener(v -> {
                // Handle edit recipe - navigate to an edit screen
                Intent editIntent = new Intent(context, EditRecipeActivity.class);
                editIntent.putExtra("RECIPE_ID", recipe.getRecipeId());
                context.startActivity(editIntent);
            });

            // Add confirmation dialog for deletion
            holder.deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Recipe")
                        .setMessage("Are you sure you want to delete this recipe?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove the recipe from Firebase
                            DatabaseReference recipeRef = FirebaseDatabase.getInstance()
                                    .getReference("recipes")
                                    .child(recipe.getRecipeId());
                            recipeRef.removeValue();
                            Toast.makeText(context, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        ImageView recipeImage;
        TextView recipeName, recipeDescription;
        Button editButton, deleteButton, favouriteButton;  // Add favouriteButton here

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeDescription = itemView.findViewById(R.id.recipeDescription);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            favouriteButton = itemView.findViewById(R.id.favouriteButton);  // Initialize favouriteButton
        }
    }
}