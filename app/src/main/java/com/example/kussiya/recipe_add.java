package com.example.kussiya;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.Kussiya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class recipe_add extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        // Initialize Spinner for categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.recipe_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tc_category.setAdapter(adapter);

        // Set listeners for buttons
        uploadImageButton.setOnClickListener(v -> openImageChooser());
        uploadVideoButton.setOnClickListener(v -> openVideoChooser());
        submitRecipeButton.setOnClickListener(v -> submitRecipe());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            recipeImage.setImageURI(imageUri);
        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            recipeVideo.setVideoURI(videoUri);
            Toast.makeText(this, "Video selected: " + videoUri.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void submitRecipe() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String recipeName = recipeNameInput.getText().toString().trim();
        String description = recipeDescription.getText().toString().trim();
        String category = tc_category.getSelectedItem().toString();

        // Ensure unique ID generation
        DatabaseReference recipeRef = mDatabase.push(); // Generate a unique key
        String recipeId = recipeRef.getKey(); // Use the generated unique key

        // Disable submit button to prevent multiple clicks
        submitRecipeButton.setEnabled(false);

        // Upload image if available
        if (imageUri != null) {
            StorageReference imageRef = mStorage.child(userId + "/images/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String uploadedImageUrl = uri.toString();
                    if (videoUri != null) {
                        uploadVideo(recipeId, userId, recipeName, description, uploadedImageUrl, category);
                    } else {
                        saveNewRecipe(recipeId, userId, recipeName, description, uploadedImageUrl, category, null);
                    }
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                submitRecipeButton.setEnabled(true);
            });
        } else if (videoUri != null) {
            uploadVideo(recipeId, userId, recipeName, description, null, category);
        } else {
            // No new media selected, just add text fields
            saveNewRecipe(recipeId, userId, recipeName, description, null, category, null);
        }
    }
    private void uploadVideo(String recipeId, String userId, String recipeName, String description, String imageUrl, String category) {
        StorageReference videoRef = mStorage.child(userId + "/videos/" + System.currentTimeMillis() + ".mp4");
        videoRef.putFile(videoUri).addOnSuccessListener(taskSnapshot -> {
            videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String uploadedVideoUrl = uri.toString();
                saveNewRecipe(recipeId, userId, recipeName, description, imageUrl, category, uploadedVideoUrl);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to upload video", Toast.LENGTH_SHORT).show();
            submitRecipeButton.setEnabled(true);
        });
    }

    private void saveNewRecipe(String recipeId, String userId, String recipeName, String description, String imageUrl, String category, String videoUrl) {
        // Correctly mapped Recipe object creation
        Recipe newRecipe = new Recipe(
                recipeId,       // Correct recipeId
                userId,         // Correct userId (currentUser's ID)
                recipeName,     // Correct recipe name
                description,    // Correct description
                imageUrl,       // Correct image URL (or null if no image)
                category,       // Correct category
                videoUrl        // Correct video URL (or null if no video)
        );

        // Save the new recipe to Firebase
        mDatabase.child(recipeId).setValue(newRecipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Recipe added successfully", Toast.LENGTH_SHORT).show();
                finish(); // Go back to the previous screen
            } else {
                Toast.makeText(this, "Failed to add recipe", Toast.LENGTH_SHORT).show();
            }
            submitRecipeButton.setEnabled(true);
        });
    }



}
