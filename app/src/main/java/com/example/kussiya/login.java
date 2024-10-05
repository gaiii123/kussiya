package com.example.kussiya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Kussiya.R;
import com.example.Kussiya.forgetPass;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        auth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        // Set up the login button click listener
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Validate user input
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

            // Log in user in Firebase
            loginUser(email, password);
        });

        // Set up the sign-up text click listener
        TextView signUpTextView = findViewById(R.id.SignUp_textview);
        signUpTextView.setOnClickListener(v -> {
            // Navigate to the signup activity
            Intent intent = new Intent(login.this, signup.class);
            startActivity(intent);
        });

        TextView forgotpassTextView = findViewById(R.id.forgotpassword);
        forgotpassTextView.setOnClickListener(v -> {
            // Navigate to the signup activity
            Intent intent = new Intent(login.this, forgetPass.class);
            startActivity(intent);
        });


    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        // Navigate to the home activity
                         Intent intent = new Intent(login.this, home.class);
                        startActivity(intent);
                        finish(); // Close the login activity
                    } else {
                        // If login fails, display a message to the user
                        Toast.makeText(login.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
