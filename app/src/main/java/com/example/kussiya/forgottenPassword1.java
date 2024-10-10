package com.example.kussiya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Kussiya.R;
import com.example.kussiya.login;
import com.google.firebase.auth.FirebaseAuth;

public class forgottenPassword1 extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password1);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        emailField = findViewById(R.id.email);
        Button continueButton = findViewById(R.id.continueButton);
        ImageView backButton = findViewById(R.id.backButton);

        // Back button logic
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(forgottenPassword1.this, login.class);
            startActivity(intent);
        });

        // Continue button logic for sending password reset email
        continueButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();

            if (email.isEmpty()) {
                emailField.setError("Please enter an email");
                emailField.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Enter a valid email");
                emailField.requestFocus();
                return;
            }

            // Send password reset email
            auth.sendPasswordResetEmail(email).addOnCompleteListener(resetTask -> {
                if (resetTask.isSuccessful()) {
                    Toast.makeText(forgottenPassword1.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(forgottenPassword1.this, login.class);
                    startActivity(intent);
                } else {
                    // Handle errors, e.g., if the email is not registered (Firebase won't reveal the exact reason)
                    Toast.makeText(forgottenPassword1.this, "Failed to send reset link. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
