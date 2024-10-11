package com.example.kussiya;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import com.example.Kussiya.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.IOException;


public class account extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button logoutButton;
    private FirebaseAuth auth;
    private DatabaseReference userRef, recipesRef;
    private TextView accountTextView;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView myRecipesRecyclerView;
    private ArrayList<Recipe> myRecipesList;
    private RecipiesAdapter myRecipesAdapter;
    private ImageView imageViewUser, imageViewAdd;
    private Uri imageUri; // For the selected image URI
    private StorageReference storageRef;
    private FirebaseUser user;
    private String userId;
    private Button FavouriteButton;// Declare userId as a member variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        // Initialize Firebase Auth and UI elements
        auth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout_button2);
        accountTextView = findViewById(R.id.account_textView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        myRecipesRecyclerView = findViewById(R.id.myRecipesRecyclerView);
        imageViewUser = findViewById(R.id.imageView_user);
        imageViewAdd = findViewById(R.id.imageView_add);
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");
        FavouriteButton = findViewById(R.id.favourite_button); // Ensure you have this button in your layout

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid(); // Set userId here

            // Get the reference to the user's data in Firebase Realtime Database
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Retrieve the username from the database and set it in the TextView
            userRef.child("username").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String username = task.getResult().getValue(String.class);
                    if (username != null) {
                        accountTextView.setText(username);
                    } else {
                        accountTextView.setText("User");
                    }
                } else {
                    accountTextView.setText("User");
                }
            });
        }

        // Setup RecyclerView
        myRecipesList = new ArrayList<>();
        if (user != null) {
            myRecipesAdapter = new RecipiesAdapter(myRecipesList, userId);
            myRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            myRecipesRecyclerView.setAdapter(myRecipesAdapter);

            // Retrieve user's recipes from Firebase
            recipesRef = FirebaseDatabase.getInstance().getReference("recipes");
            recipesRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    myRecipesList.clear();
                    for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                        Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                        if (recipe != null) {
                            myRecipesList.add(recipe);
                        }
                    }
                    myRecipesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors
                }
            });
        }

        FavouriteButton.setOnClickListener(v -> {
            Intent intent = new Intent(account.this, FavouritesActivity.class); // Corrected here
            startActivity(intent);
        });

        // Logout button functionality
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(account.this, login.class);
            startActivity(intent);
            finish();
        });

        // Handle imageView_add click to select new profile picture
        imageViewAdd.setOnClickListener(v -> openImagePicker());

        // Setup bottom navigation
        bottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView, R.id.bottom_profile);
    }


        // Method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                // Show selected image in the ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewUser.setImageBitmap(bitmap);

                // Upload the image to Firebase Storage
                uploadImageToFirebase();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to upload the image to Firebase Storage
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child(userId + ".jpg"); // Use class-level userId

            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                // Get the image URL after successful upload
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Update the user's profile image URL in the Firebase Database
                    updateProfileImageUrl(imageUrl);
                    Toast.makeText(account.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> Toast.makeText(account.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }


    // Method to update the profile image URL in Firebase Database
    private void updateProfileImageUrl(String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.child("profileImageUrl").setValue(imageUrl);
    }

    private void addToFavorites(String recipeId) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");
        favoritesRef.child(recipeId).setValue(true)
                .addOnSuccessListener(aVoid -> Toast.makeText(account.this, "Added to Favorites", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(account.this, "Failed to add to Favorites", Toast.LENGTH_SHORT).show());
    }

}
