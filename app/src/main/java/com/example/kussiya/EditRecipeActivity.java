package com.example.kussiya;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.Kussiya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class EditRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    private ImageView recipeImage;
    private VideoView recipeVideo;
    private Button uploadImageButton, uploadVideoButton, submitRecipeButton;
    private Uri imageUri, videoUri;
    private EditText recipeNameInput, recipeDescription;
    private Spinner tc_category;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private String recipeId;
    private String currentImageUrl;
    private String currentVideoUrl;
    private float rating;
    private int rateCount;
    private List<String> reviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        // Initialize UI components
        recipeImage = findViewById(R.id.imageView3);
        recipeVideo = findViewById(R.id.video);
        uploadImageButton = findViewById(R.id.addbuttonimage);
        uploadVideoButton = findViewById(R.id.addbuttonvideo);
        submitRecipeButton = findViewById(R.id.upload_recipe_button4);
        recipeNameInput = findViewById(R.id.titleTxt);
        recipeDescription = findViewById(R.id.tv_description);
        tc_category = findViewById(R.id.tc_category);

        // Set up Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
        mStorage = FirebaseStorage.getInstance().getReference("recipe_media");

        // Get the recipeId passed from the previous activity
        recipeId = getIntent().getStringExtra("recipeId");

        // Populate existing recipe data
        loadRecipeDetails();

        // Set listeners for selecting images or videos
        uploadImageButton.setOnClickListener(v -> openImageChooser());
        uploadVideoButton.setOnClickListener(v -> openVideoChooser());
        submitRecipeButton.setOnClickListener(v -> updateRecipe());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Recipe Image"), PICK_IMAGE_REQUEST);
    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Recipe Video"), PICK_VIDEO_REQUEST);
    }

    private void loadRecipeDetails() {
        // Fetch recipe details from Firebase to allow the user to edit them
        mDatabase.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);

                    if (recipe != null) {
                        recipeNameInput.setText(recipe.getRecipeName());
                        recipeDescription.setText(recipe.getDescription());
                        rating = recipe.getRating();
                        rateCount = recipe.getRateCount();
                        reviews = recipe.getReviews();

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditRecipeActivity.this,
                                R.array.recipe_categories, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        tc_category.setAdapter(adapter);

                        // Set category
                        int spinnerPosition = adapter.getPosition(recipe.getCategory());
                        tc_category.setSelection(spinnerPosition);

                        currentImageUrl = recipe.getImageUrl();
                        currentVideoUrl = recipe.getVideoUrl();

                        // Load image if available
                        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                            Glide.with(EditRecipeActivity.this).load(currentImageUrl).into(recipeImage);
                        } else {
                            recipeImage.setImageResource(R.drawable.placeholder);  // Placeholder if no image
                        }

                        // Load video if available
                        if (currentVideoUrl != null && !currentVideoUrl.isEmpty()) {
                            recipeVideo.setVideoPath(currentVideoUrl);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditRecipeActivity.this, "Failed to load recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            recipeImage.setImageURI(imageUri);  // Update image preview
        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            recipeVideo.setVideoURI(videoUri);  // Update video preview
        }
    }

    private void updateRecipe() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String recipeName = recipeNameInput.getText().toString().trim();
        String description = recipeDescription.getText().toString().trim();
        String category = tc_category.getSelectedItem().toString();

        // Disable submit button to prevent multiple clicks
        submitRecipeButton.setEnabled(false);

        // Upload image if available
        if (imageUri != null) {
            StorageReference imageRef = mStorage.child(userId + "/images/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String uploadedImageUrl = uri.toString();
                    if (videoUri != null) {
                        uploadVideo(userId, recipeName, description, uploadedImageUrl, category,rating,rateCount,reviews);
                    } else {
                        saveUpdatedRecipe(recipeId, userId, recipeName, description, uploadedImageUrl, category, currentVideoUrl,rating,rateCount,reviews);
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                submitRecipeButton.setEnabled(true);
            });
        } else if (videoUri != null) {
            uploadVideo(userId, recipeName, description, currentImageUrl, category,rating,rateCount,reviews);
        } else {
            // No new media selected, just update text fields
            saveUpdatedRecipe(recipeId, userId, recipeName, description, currentImageUrl, category, currentVideoUrl,rating,rateCount,reviews);
        }
    }

    private void uploadVideo(String userId, String recipeName, String description, String imageUrl, String category, float rating, int rateCount, List<String> reviews) {
        StorageReference videoRef = mStorage.child(userId + "/videos/" + System.currentTimeMillis() + ".mp4");
        videoRef.putFile(videoUri).addOnSuccessListener(taskSnapshot -> {
            videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String uploadedVideoUrl = uri.toString();
                saveUpdatedRecipe(recipeId, userId, recipeName, description, imageUrl, category, uploadedVideoUrl,rating,rateCount,reviews);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to upload video", Toast.LENGTH_SHORT).show();
            submitRecipeButton.setEnabled(true);
        });
    }

    private void saveUpdatedRecipe(String recipeId, String userId, String recipeName, String description, String imageUrl, String category, String videoUrl,float rating,int rateCount,List<String> reviews) {
        Recipe updatedRecipe = new Recipe(recipeId, userId, recipeName, description, imageUrl, category, videoUrl,rating,rateCount,reviews);
        mDatabase.child(recipeId).setValue(updatedRecipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Recipe updated successfully", Toast.LENGTH_SHORT).show();
                finish();  // Go back to the previous screen
            } else {
                Toast.makeText(this, "Failed to update recipe", Toast.LENGTH_SHORT).show();
            }
            submitRecipeButton.setEnabled(true);  // Re-enable button
        });
    }
}
