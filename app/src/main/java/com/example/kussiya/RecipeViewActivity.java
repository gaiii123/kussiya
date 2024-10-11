package com.example.kussiya;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.Kussiya.R;

public class RecipeViewActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeNameTextView, recipeDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_view);

        recipeImageView = findViewById(R.id.recipeImageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView);

        // Get data from the Intent
        String recipeName = getIntent().getStringExtra("RECIPE_NAME");
        String recipeDescription = getIntent().getStringExtra("RECIPE_DESCRIPTION");
        String recipeImageUrl = getIntent().getStringExtra("RECIPE_IMAGE_URL");

        // Set the data to the views
        recipeNameTextView.setText(recipeName);
        recipeDescriptionTextView.setText(recipeDescription);
        Glide.with(this).load(recipeImageUrl).into(recipeImageView);
    }
}
