package com.example.kussiya;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Kussiya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyRecipeView extends AppCompatActivity {

    private RecyclerView myRecipeRecyclerView;
    private RecipiesAdapter myRecipesAdapter;
    private List<Recipe> myRecipeList;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe_view);

        myRecipeRecyclerView = findViewById(R.id.myrecipeRecyclerView);
        myRecipeList = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Setup RecyclerView
        myRecipesAdapter = new RecipiesAdapter(myRecipeList, currentUserId);
        myRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecipeRecyclerView.setAdapter(myRecipesAdapter);

        // Fetch recipes from Firebase
        fetchMyRecipes();
    }

    private void fetchMyRecipes() {
        DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("recipes");

        recipesRef.orderByChild("userId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRecipeList.clear(); // Clear existing recipes
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipe.setRecipeId(recipeSnapshot.getKey()); // Set the recipe ID
                        myRecipeList.add(recipe); // Add recipe to the list
                    }
                }
                myRecipesAdapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}
