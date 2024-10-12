package com.example.kussiya;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.Kussiya.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RecipeViewActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeNameTextView, recipeDescriptionTextView;
    private VideoView recipeVideoView;
    private Button playButton;
    private Button ReviewButton;
    private RatingBar ratingBar;
    private LinearLayout reviewView;
    private List<String> reviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_view);

        recipeImageView = findViewById(R.id.recipeImageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView);
        recipeVideoView = findViewById(R.id.VideoView);
        playButton = findViewById(R.id.videoButtonView);
        ReviewButton = findViewById(R.id.reviewbutton);
        ratingBar = findViewById(R.id.ratingBar);
        reviewView = findViewById(R.id.reviewView);

        // Get data from the Intent
        String recipeName = getIntent().getStringExtra("RECIPE_NAME");
        String userID = getIntent().getStringExtra("USER_ID");
        String recipeDescription = getIntent().getStringExtra("RECIPE_DESCRIPTION");
        String recipeImageUrl = getIntent().getStringExtra("RECIPE_IMAGE_URL");
        String recipeVideoUrl = getIntent().getStringExtra("RECIPE_VIDEO_URL");
        String recipeID =  getIntent().getStringExtra("RECIPE_ID");
        String category =  getIntent().getStringExtra("CATEGORY");
        float rating = getIntent().getFloatExtra("RATING",0.0F);
        int rateCount = getIntent().getIntExtra("RATE_COUNT",0);
        ArrayList<String> reviewsList = getIntent().getStringArrayListExtra("REVIEWS");

        // Initialize the reviews list with the data from the intent
        if (reviewsList != null) {
            reviews = new ArrayList<>(reviewsList);
        } else {
            reviews = new ArrayList<>();  // Empty list if reviewsList is null
        }

        ReviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeViewActivity.this, review.class);
            intent.putExtra("RECIPE_NAME", recipeName);
            intent.putExtra("USER_ID", userID);
            intent.putExtra("RECIPE_DESCRIPTION", recipeDescription);
            intent.putExtra("RECIPE_IMAGE_URL", recipeImageUrl);
            intent.putExtra("RECIPE_VIDEO_URL", recipeVideoUrl);
            intent.putExtra("RECIPE_ID", recipeID);
            intent.putExtra("CATEGORY", category);
            intent.putExtra("RATING", rating);
            intent.putExtra("RATE_COUNT", rateCount);
            intent.putStringArrayListExtra("REVIEWS", reviewsList);
            startActivity(intent);
            finish();
        });

        // Set the data to the views
        recipeNameTextView.setText(recipeName);
        recipeDescriptionTextView.setText(recipeDescription);
        ratingBar.setRating(rating);

        // Only try to display reviews if there are any
        if (reviews != null && !reviews.isEmpty()) {
            for (int i = 0; i < reviews.size(); i++) {
                // Create a new CardView
                CardView cardView = new CardView(this);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                cardParams.setMargins(25, 25, 25, 25);  // Set margins
                cardView.setLayoutParams(cardParams);
                cardView.setRadius(20f);  // Set corner radius
                cardView.setCardBackgroundColor(getResources().getColor(R.color.notification_card_color)); // Use your color

                // Create the description TextView, which will hold the review text
                TextView descriptionTextView = new TextView(this);
                descriptionTextView.setText(reviews.get(i));  // Set the review text from the list
                descriptionTextView.setPadding(25, 25, 25, 20);

                cardView.addView(descriptionTextView);

                // Add the CardView to the LinearLayout
                reviewView.addView(cardView);
            }
        } else {
            // Handle case when there are no reviews
            TextView noReviewsTextView = new TextView(this);
            noReviewsTextView.setText("No reviews available");
            reviewView.addView(noReviewsTextView);
        }

        // Load and display the image using Glide
        Glide.with(this).load(recipeImageUrl).into(recipeImageView);

        // Check if there is a video URL
        if (recipeVideoUrl != null && !recipeVideoUrl.isEmpty()) {
            Uri videoUri = Uri.parse(recipeVideoUrl);
            recipeVideoView.setVideoURI(videoUri);

            MediaController mediaController = new MediaController(this);
            recipeVideoView.setMediaController(mediaController);
            mediaController.setAnchorView(recipeVideoView);

            playButton.setOnClickListener(v -> recipeVideoView.start());
        } else {
            recipeVideoView.setVisibility(VideoView.GONE);
            playButton.setVisibility(Button.GONE);
        }
    }
}
