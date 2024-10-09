package com.example.kussiya;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Kussiya.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        // Only show edit/delete buttons if the recipe belongs to the current user
        if (recipe.getUserId().equals(currentUserId)) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.editButton.setOnClickListener(v -> {
                // Handle edit recipe - navigate to an edit screen (not implemented here)
                Intent editIntent = new Intent(context, EditRecipeActivity.class);
                editIntent.putExtra("RECIPE_ID", recipe.getRecipeId());
                context.startActivity(editIntent);
            });

            holder.deleteButton.setOnClickListener(v -> {
                // Handle delete recipe
                DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipe.getRecipeId());
                recipeRef.removeValue();
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
        Button editButton, deleteButton;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeDescription = itemView.findViewById(R.id.recipeDescription);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
