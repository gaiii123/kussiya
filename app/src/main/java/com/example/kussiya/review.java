package com.example.kussiya;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.Kussiya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class review extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button ReviewButton;
    private EditText reviewText;
    private RatingBar ratingbar;
    private List<String> reviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

        reviewText = findViewById(R.id.reviewtext);
        ratingbar= findViewById(R.id.ratingBar);
        ReviewButton = findViewById(R.id.reviewSubmit);
        // Set up Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
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
        reviews = new ArrayList<>(reviewsList);

        ReviewButton.setOnClickListener(v -> submitReview(recipeID, userID, recipeName, recipeDescription, recipeImageUrl, category, recipeVideoUrl,rating,rateCount,reviews));
    }

    private void submitReview(String recipeId, String userId, String recipeName, String description, String imageUrl, String category, String videoUrl,float oldRating,int rateCount,List<String> reviews) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        float newRating;

        String newReview = reviewText.getText().toString().trim();
        float rating = ratingbar.getRating();
        if (rateCount == 0) {
            // If there are no prior ratings, set newRating to the current rating
            newRating = rating;
        } else {
            // Compute the new rating with the existing rateCount
            newRating = (oldRating * rateCount + rating) / (rateCount + 1);
        }
        rateCount=rateCount+1;
        // Check the conditions
        if (reviews.size() == 1 && reviews.get(0).equals("No Reviews yet")) {
            // Replace the "No Reviews yet" with the new review
            reviews.set(0, newReview);
        } else {
            // Either the list is empty or has more than one item, so add the new review
            reviews.add(newReview);
        }
        // Disable submit button to prevent multiple clicks
        ReviewButton.setEnabled(false);

        // Upload image if available
        if ((newReview != null) & (rating != 0.0F)) {
            saveUpdatedRecipe(recipeId, userId, recipeName, description, imageUrl, category, videoUrl,newRating,rateCount,reviews);
        } else {
            Toast.makeText(this, "Please Rate and Review the Recipe", Toast.LENGTH_SHORT).show();
            ReviewButton.setEnabled(true); // Re-enable button
        }
    }

    private void saveUpdatedRecipe(String recipeId, String userId, String recipeName, String description, String imageUrl, String category, String videoUrl,float rating,int rateCount,List<String> reviews) {
        Recipe updatedRecipe = new Recipe(recipeId, userId, recipeName, description, imageUrl, category, videoUrl,rating,rateCount,reviews);
        mDatabase.child(recipeId).setValue(updatedRecipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Review added successfully", Toast.LENGTH_SHORT).show();
                finish();  // Go back to the previous screen
            } else {
                Toast.makeText(this, "Failed to add review", Toast.LENGTH_SHORT).show();
            }
            ReviewButton.setEnabled(true);  // Re-enable button
        });
    }
}