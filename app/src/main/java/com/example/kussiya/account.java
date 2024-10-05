package com.example.kussiya;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.Kussiya.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

//IM/2021/078
//IM/2021/108

public class account extends AppCompatActivity {

    private Button logoutButton;
    private FirebaseAuth auth;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.account);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        logoutButton = findViewById(R.id.logout_button2);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        // Set up the logout button click listener
        logoutButton.setOnClickListener(v -> {
            // Sign out user from Firebase
            auth.signOut();
            // Navigate to the login activity
            Intent intent = new Intent(account.this, login.class);
            startActivity(intent);
            finish(); // Close the home activity
        });

        // Set up Bottom Navigation View
        bottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView, R.id.bottom_profile);



        // Handle edge-to-edge display insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
