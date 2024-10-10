package com.example.kussiya;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.Kussiya.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class account extends AppCompatActivity {

    private Button logoutButton;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private TextView accountTextView;  // Reference to the TextView
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.account);

        // Initialize Firebase Auth and UI elements
        auth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout_button2);
        accountTextView = findViewById(R.id.account_textView);  // Initialize the TextView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Retrieve current user ID
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

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

        // Logout button click listener
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(account.this, login.class);
            startActivity(intent);
            finish();
        });

        // Setup Bottom Navigation
        bottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView, R.id.bottom_profile);
    }
}

