package com.example.kussiya;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation;

import com.example.Kussiya.R;

public class RateUs extends Dialog {

    private float userRate = 0;

    public RateUs(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rateus);

        final AppCompatButton rateNowBtn = findViewById(R.id.rateNowBtn);
        final AppCompatButton laterBtn = findViewById(R.id.laterBtn);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        final ImageView rate1img = findViewById(R.id.rate1img);

        // Set RatingBar listener
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Handle rating change
                System.out.println("Rating: " + rating);

                if (rating <= 1) {
                    rate1img.setImageResource(R.drawable.emoji1);
                } else if (rating <= 2) {
                    rate1img.setImageResource(R.drawable.emoji2);
                } else if (rating <= 3) {
                    rate1img.setImageResource(R.drawable.emoji3);
                } else if (rating <= 4) {
                    rate1img.setImageResource(R.drawable.emoji4);
                } else if (rating <= 5) {
                    rate1img.setImageResource(R.drawable.emoji5);
                }

                // Optional: Add animation to image
                animateImage(rate1img);

                userRate = rating; // Save the user rating
            }
        });

        // Set click listener for "Rate Now" button
        rateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle rate now action
                dismiss(); // Close the dialog after rating
            }
        });

        // Set click listener for "Later" button
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle later action
                dismiss(); // Close the dialog for later
            }
        });
    }

    // Animation for image view
    private void animateImage(ImageView ratingImage) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0, 1f,   // From X to X (scaling horizontally)
                0, 1f,   // From Y to Y (scaling vertically)
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point X
                Animation.RELATIVE_TO_SELF, 0.5f  // Pivot point Y
        );
        scaleAnimation.setFillAfter(true);  // Keep the scale after animation ends
        scaleAnimation.setDuration(200);    // Duration in milliseconds
        ratingImage.startAnimation(scaleAnimation);  // Start animation
    }
}
