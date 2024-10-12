package com.example.kussiya;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.Kussiya.R;

public class RecipeViewActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeNameTextView, recipeDescriptionTextView;
    private VideoView recipeVideoView;
    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_view);

        recipeImageView = findViewById(R.id.recipeImageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView);
        recipeVideoView = findViewById(R.id.VideoView);
        playButton = findViewById(R.id.videoButtonView);

        // Get data from the Intent
        String recipeName = getIntent().getStringExtra("RECIPE_NAME");
        String recipeDescription = getIntent().getStringExtra("RECIPE_DESCRIPTION");
        String recipeImageUrl = getIntent().getStringExtra("RECIPE_IMAGE_URL");
        String recipeVideoUrl = getIntent().getStringExtra("RECIPE_VIDEO_URL");


        // Set the data to the views
        recipeNameTextView.setText(recipeName);
        recipeDescriptionTextView.setText(recipeDescription);

        // Load and display the image using Glide
        Glide.with(this).load(recipeImageUrl).into(recipeImageView);

        // Check if there is a video URL
        if (recipeVideoUrl != null && !recipeVideoUrl.isEmpty()) {
            // Set up the VideoView
            Uri videoUri = Uri.parse(recipeVideoUrl);
            recipeVideoView.setVideoURI(videoUri);

            // Add media controls (play, pause, etc.)
            MediaController mediaController = new MediaController(this);
            recipeVideoView.setMediaController(mediaController);
            mediaController.setAnchorView(recipeVideoView);

            // Play video when play button is clicked
            playButton.setOnClickListener(v -> {
                recipeVideoView.start();
            });
        } else {
            // If no video URL, hide the VideoView and play button
            recipeVideoView.setVisibility(VideoView.GONE);
            playButton.setVisibility(Button.GONE);
        }
    }
}
