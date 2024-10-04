package com.example.kussiya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.Kussiya.R;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signup extends AppCompatActivity {

    private EditText emailField, passwordField, confirmPasswordField, usernameField;
    private Button signupButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        usernameField = findViewById(R.id.username);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.ConfirmPassword);
        signupButton = findViewById(R.id.button2);

        // Set up the sign-up button click listener
        signupButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();

            // Validate user input
            if (username.isEmpty()) {
                usernameField.setError("Username is required");
                usernameField.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                emailField.setError("Email is required");
                emailField.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.setError("Please enter a valid email");
                emailField.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordField.setError("Password is required");
                passwordField.requestFocus();
                return;
            }

            if (password.length() < 6) {
                passwordField.setError("Password must be at least 6 characters");
                passwordField.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                confirmPasswordField.setError("Passwords do not match");
                confirmPasswordField.requestFocus();
                return;
            }

            // Create user in Firebase
            createUser(email, password);
        });
        // Set up the sign-up text click listener
        TextView LoginTextView = findViewById(R.id.textView8);
        LoginTextView.setOnClickListener(v -> {
            // Navigate to the signup activity
            Intent intent = new Intent(signup.this, login.class);
            startActivity(intent);
        });
    }

    private void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-up successful
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(signup.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                        // You can navigate the user to another activity, e.g., home page
                        Intent intent = new Intent(signup.this, home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign-up fails, display a message to the user
                        Toast.makeText(signup.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


}