package com.example.Kussiya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kussiya.home;
import com.example.kussiya.login;
import com.example.Kussiya.forgotPass;

public class forgetPass extends AppCompatActivity {
    private EditText emailField;
    private Button ConButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate to the signup activity
            Intent intent = new Intent(forgetPass.this, login.class);
            startActivity(intent);
        });

        emailField = findViewById(R.id.email);
        ConButton = findViewById(R.id.continueButton);

        // Set up the login button click listener
        ConButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();


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


            Intent intent = new Intent(forgetPass.this, forgotPass.class);
            startActivity(intent);
            finish(); // Close the login activity
        });
    }
}