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

import java.io.IOException;

public class EditRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView recipeImage;
    private Button uploadImageButton, submitRecipeButton;
    private Uri imageUri;
    private EditText recipeNameInput, recipeDescription;
    private Spinner tc_category;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private String recipeId;
    private String currentImageUrl;

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

        // Get recipe ID from the intent
        recipeId = getIntent().getStringExtra("RECIPE_ID");
        loadRecipeDetails(recipeId);

        // Set listeners for buttons
        uploadImageButton.setOnClickListener(v -> openImageChooser());
        submitRecipeButton.setOnClickListener(v -> submitUpdatedRecipe());
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
                recipeImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadRecipeDetails(String recipeId) {
        mDatabase.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipeNameInput.setText(recipe.getRecipeName());
                        recipeDescription.setText(recipe.getDescription());
                        currentImageUrl = recipe.getImageUrl();

                        // Load image using Glide
                        Glide.with(EditRecipeActivity.this).load(currentImageUrl).into(recipeImage);

                        // Set spinner category
                        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) tc_category.getAdapter();
                        tc_category.setSelection(adapter.getPosition(recipe.getCategory()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditRecipeActivity.this, "Failed to load recipe details.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void submitUpdatedRecipe() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String recipeName = recipeNameInput.getText().toString().trim();
            String description = recipeDescription.getText().toString().trim();
            String category = tc_category.getSelectedItem().toString();

            if (imageUri != null) {
                // Create a reference for new image upload
                StorageReference imageRef = mStorage.child(user.getUid() + "/images/" + System.currentTimeMillis() + ".jpg");
                imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                        imageRef.getDownloadUrl().addOnSuccessListener(imageUrl -> updateRecipeInDatabase(recipeName, description, category, imageUrl.toString()))
                ).addOnFailureListener(e -> Toast.makeText(EditRecipeActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show());
            } else {
                // Update without uploading a new image
                updateRecipeInDatabase(recipeName, description, category, currentImageUrl);
            }
        }
    }

    private void updateRecipeInDatabase(String recipeName, String description, String category, String imageUrl) {
        Recipe updatedRecipe = new Recipe(recipeId, mAuth.getUid(), recipeName, description, imageUrl, category);
        mDatabase.child(recipeId).setValue(updatedRecipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditRecipeActivity.this, "Recipe updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditRecipeActivity.this, "Failed to update recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
