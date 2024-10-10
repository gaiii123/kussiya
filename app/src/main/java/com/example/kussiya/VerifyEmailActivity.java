package com.example.kussiya;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Kussiya.R;
import com.example.kussiya.home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        auth = FirebaseAuth.getInstance();

        // Add a button to refresh and check if the email is verified
        Button checkVerificationButton = findViewById(R.id.checkVerificationButton);
        checkVerificationButton.setOnClickListener(v -> {
            // Refresh the user data
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (user.isEmailVerified()) {
                        // If email is verified, navigate to the home screen
                        Intent intent = new Intent(VerifyEmailActivity.this, home.class);
                        startActivity(intent);
                        finish();  // Close the current activity
                    } else {
                        Toast.makeText(VerifyEmailActivity.this, "Email not verified yet. Please check your inbox.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
