package com.example.kussiya;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Kussiya.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class home extends AppCompatActivity {

    private Button logoutButton;
    private FirebaseAuth auth;
    private ImageView imageViewBreakfast;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements

        imageViewBreakfast = findViewById(R.id.imageView_breakfast);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);





        imageViewBreakfast.setOnClickListener(v -> {
            // Navigate to the breakfast activity
            Intent intent = new Intent(home.this, breakfast_screen.class);
            startActivity(intent);
        });



        // Set up BottomNavigationView using BottomNavigationHelper
        bottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView, R.id.bottom_home);




        // Handle edge-to-edge display insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}