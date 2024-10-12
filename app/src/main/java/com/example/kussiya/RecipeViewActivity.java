package com.example.kussiya;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.Kussiya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecipeViewActivity extends AppCompatActivity {

    private TextView recipeNameTextView, recipeDescriptionTextView;
    private ImageView recipeImageView;
    private Button editButton, deleteButton, favouriteButton;
    private String recipeId, currentUserId;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_view);

        // Initialize views
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView);
        recipeImageView = findViewById(R.id.recipeImageView);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        favouriteButton = findViewById(R.id.favouriteButton);

        // Get the recipe data from the intent
        recipeId = getIntent().getStringExtra("RECIPE_ID");
        String recipeName = getIntent().getStringExtra("RECIPE_NAME");
        String recipeDescription = getIntent().getStringExtra("RECIPE_DESCRIPTION");
        String recipeImageUrl = getIntent().getStringExtra("RECIPE_IMAGE_URL");
        String recipeVideoUrl = getIntent().getStringExtra("RECIPE_VIDEO_URL");

        // Set the recipe data to the views
        recipeNameTextView.setText(recipeName);
        recipeDescriptionTextView.setText(recipeDescription);
        Glide.with(this).load(recipeImageUrl).into(recipeImageView);

        // Get current user ID
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Handle edit button click
        editButton.setOnClickListener(v -> {
            Intent editIntent = new Intent(RecipeViewActivity.this, EditRecipeActivity.class);
            editIntent.putExtra("RECIPE_ID", recipeId);
            startActivity(editIntent);
        });

        // Handle delete button click with confirmation
        deleteButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mDatabase.child("recipes").child(recipeId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(RecipeViewActivity.this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Close activity
                            })
                            .addOnFailureListener(e -> Toast.makeText(RecipeViewActivity.this, "Failed to delete recipe", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", null)
                .show());

        // Handle favorite button click
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(currentUserId)
                .child("favorites")
                .child(recipeId);

        // Check if the recipe is already a favorite
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Recipe is already a favorite
                    favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_24);  // Filled heart icon
                    favouriteButton.setTag("favorited");
                } else {
                    // Recipe is not a favorite
                    favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_border_24);  // Outline heart icon
                    favouriteButton.setTag("not_favorited");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors
            }
        });

        // Toggle favorite status on button click
        favouriteButton.setOnClickListener(v -> {
            if (favouriteButton.getTag().equals("not_favorited")) {
                favRef.setValue(recipeId)
                        .addOnSuccessListener(aVoid -> {
                            favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_24);
                            favouriteButton.setTag("favorited");
                            Toast.makeText(RecipeViewActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(RecipeViewActivity.this, "Failed to add to favorites", Toast.LENGTH_SHORT).show());
            } else {
                favRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            favouriteButton.setBackgroundResource(R.drawable.baseline_favorite_border_24);
                            favouriteButton.setTag("not_favorited");
                            Toast.makeText(RecipeViewActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(RecipeViewActivity.this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
