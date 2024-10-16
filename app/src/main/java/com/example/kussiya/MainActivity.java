package com.example.kussiya;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.Kussiya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//IM/2021/041

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DURATION = 2000; // 2 seconds delay
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in, navigate to home screen
            startActivity(new Intent(MainActivity.this, home.class));
            finish();  // Close this activity to prevent back navigation to it
        } else {

            // Delay for splash screen and then move to login screen
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
                finish(); // Finish the current activity so it can't be returned to
            }, SPLASH_SCREEN_DURATION);
        }
    }
}
