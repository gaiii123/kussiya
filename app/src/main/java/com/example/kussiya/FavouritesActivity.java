package com.example.kussiya;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Kussiya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView favouritesRecyclerView;
    private ArrayList<Recipe> favouriteRecipesList;
    private RecipiesAdapter favouritesAdapter;
    private FirebaseAuth auth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        favouritesRecyclerView = findViewById(R.id.favouritesRecyclerView);
        favouritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        favouriteRecipesList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            userId = user.getUid();
            loadFavoriteRecipes();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFavoriteRecipes() {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favouriteRecipesList.clear();
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    String recipeId = recipeSnapshot.getKey(); // Get the recipe ID
                    // Now, retrieve the recipe details from the "recipes" reference
                    DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipeId);
                    recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot recipeSnapshot) {
                            Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                            if (recipe != null) {
                                favouriteRecipesList.add(recipe);
                                // Notify the adapter of the new data
                                if (favouritesAdapter == null) {
                                    favouritesAdapter = new RecipiesAdapter(favouriteRecipesList, userId);
                                    favouritesRecyclerView.setAdapter(favouritesAdapter);
                                } else {
                                    favouritesAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle possible errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}
