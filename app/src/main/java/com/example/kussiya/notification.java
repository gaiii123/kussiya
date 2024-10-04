package com.example.kussiya;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.Kussiya.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class notification extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    LinearLayout notificationLayout;  // Add a reference to the LinearLayout where notifications will be added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.notification);

        // Initialize Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Initialize Bottom Navigation View
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set up Bottom Navigation View
        bottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView, R.id.bottom_notification);

        // Initialize the LinearLayout where you want to add the CardViews
        notificationLayout = findViewById(R.id.notification_layout);  // Ensure your LinearLayout in XML has this ID

        // Dynamically create and add 15 CardView notifications
        for (int i = 0; i < 15; i++) {
            // Create a new CardView
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(25, 25, 25, 25);  // Set margins
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(20f);  // Set corner radius
            cardView.setCardBackgroundColor(getResources().getColor(R.color.notification_card_color)); // Use your color

            // Create the title TextView
            TextView titleTextView = new TextView(this);
            titleTextView.setText("Ace Your Next Exam! " + (i + 1));  // Unique title for each notification
            titleTextView.setTextSize(18);
            titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
            titleTextView.setPadding(25, 10, 15, 0);

            // Create the description TextView
            TextView descriptionTextView = new TextView(this);
            descriptionTextView.setText("Your personalized quiz is ready. Let's focus on your weak points and boost your score! 📚💪");
            descriptionTextView.setPadding(25, 80, 25, 20);

            // Add the TextViews to the CardView
            cardView.addView(titleTextView);
            cardView.addView(descriptionTextView);

            // Add the CardView to the LinearLayout
            notificationLayout.addView(cardView);
        }

        // Handle edge-to-edge display insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
