package com.example.kussiya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;

import com.example.Kussiya.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class home extends AppCompatActivity {

    private Button logoutButton;
    private FirebaseAuth auth;
    private ImageView imageViewBreakfast;
    private ImageView imageViewLunch;
    private ImageView imageViewDinner;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge mode
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.home);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        imageViewBreakfast = findViewById(R.id.imageView_breakfast);
        imageViewLunch = findViewById(R.id.imageView_lunch);
        imageViewDinner = findViewById(R.id.imageView_dinner);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Image click listeners
        imageViewBreakfast.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, RecipeCategoryActivity.class);
            intent.putExtra("CATEGORY", "Breakfast");
            startActivity(intent);
        });

        imageViewLunch.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, RecipeCategoryActivity.class);
            intent.putExtra("CATEGORY", "Lunch");
            startActivity(intent);
        });

        imageViewDinner.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, RecipeCategoryActivity.class);
            intent.putExtra("CATEGORY", "Dinner");
            startActivity(intent);
        });

        // Set up BottomNavigationView
        bottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView, R.id.bottom_home);

        // Handle edge-to-edge display insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
