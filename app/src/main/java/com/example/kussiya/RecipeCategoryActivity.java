package com.example.kussiya;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class RecipeCategoryActivity extends AppCompatActivity {

    private RecyclerView recipesRecyclerView;
    private RecipiesAdapter recipesAdapter;
    private List<Recipe> recipeList;
    private DatabaseReference mDatabase;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_category);

        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeList = new ArrayList<>();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recipesAdapter = new RecipiesAdapter(recipeList, currentUserId);
        recipesRecyclerView.setAdapter(recipesAdapter);

        // Get category from intent
        String category = getIntent().getStringExtra("CATEGORY");
        toolbar.setTitle(category);
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if (recipe != null && recipe.getCategory().equals(category)) {
                        recipeList.add(recipe);
                    }
                }
                recipesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}