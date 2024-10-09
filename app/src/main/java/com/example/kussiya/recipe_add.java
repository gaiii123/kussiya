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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Kussiya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class recipe_add extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView recipeImage;
    private Button uploadImageButton, submitRecipeButton;
    private Uri imageUri;
    private EditText recipeNameInput, recipeDescription;
    private Spinner tc_category; // Spinner for categories
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_add);

        // Initialize UI components
        recipeImage = findViewById(R.id.imageView3);
        uploadImageButton = findViewById(R.id.addbutton3);
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
        submitRecipeButton.setOnClickListener(v -> submitRecipe());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Recipe Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                recipeImage.setImageBitmap(bitmap); // Display the image
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void submitRecipe() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && imageUri != null) {
            String userId = user.getUid();
            String recipeName = recipeNameInput.getText().toString().trim();
            String description = recipeDescription.getText().toString().trim();
            String category = tc_category.getSelectedItem().toString(); // Get selected category

            // Create a reference for image upload
            StorageReference imageRef = mStorage.child(userId + "/images/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(imageUrl -> {
                    String recipeId = mDatabase.push().getKey();
                    Recipe recipe = new Recipe(recipeId, userId, recipeName, description, imageUrl.toString(), category); // Removed isFavorite

                    if (recipeId != null) {
                        mDatabase.child(recipeId).setValue(recipe).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(recipe_add.this, "Recipe added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(recipe_add.this, "Failed to add recipe", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }).addOnFailureListener(e -> Toast.makeText(recipe_add.this, "Failed to upload image.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(recipe_add.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

}
