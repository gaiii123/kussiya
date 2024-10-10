package com.example.kussiya;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.Kussiya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


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

        emailField = findViewById(R.id.login_email);
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
        TextView signupTextView = findViewById(R.id.SignUp_textview);
        signupTextView.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, signup.class);
            startActivity(intent);
        });


        TextView forgotpassTextView = findViewById(R.id.forgotpassword);
        forgotpassTextView.setOnClickListener(v -> {
            // Navigate to the signup activity
            Intent intent = new Intent(login.this, forgottenPassword1.class);
            startActivity(intent);
        });

    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Get the current user
                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null && user.isEmailVerified()) {
                            // If the email is verified, navigate to the home screen
                            Intent intent = new Intent(login.this, home.class);
                            startActivity(intent);
                            finish();  // Close the current activity
                        } else {
                            // If the email is not verified, show a message
                            Toast.makeText(login.this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        // If login fails, show an error message
                        Log.e("Login", "Login failed: " + task.getException().getMessage());
                        Toast.makeText(login.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }
}
