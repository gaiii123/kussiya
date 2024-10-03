package com.example.kussiya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.Kussiya.R;
import com.google.firebase.auth.FirebaseAuth;

public class home extends AppCompatActivity {

    private Button logoutButton;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);

        // Handle edge-to-edge display insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        logoutButton = findViewById(R.id.logout_button1);

        // Set up the logout button click listener
        logoutButton.setOnClickListener(v -> {
            // Sign out user from Firebase
            auth.signOut();
            // Navigate to the login activity
            Intent intent = new Intent(home.this, login.class);
            startActivity(intent);
            finish(); // Close the home activity
        });
    }
}
