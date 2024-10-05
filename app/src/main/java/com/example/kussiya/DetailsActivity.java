package com.example.kussiya;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.view.View;
import android.graphics.drawable.ColorDrawable;

import com.example.Kussiya.R;
import java.util.Objects;
import com.example.Kussiya.videoView;
//IM/2021/069
//IM/2021/097

public class DetailsActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle;
    ImageView detailImage;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailDesc = findViewById(R.id.detailDesc);
        detailTitle = findViewById(R.id.detailTitle);
        detailImage = findViewById(R.id.detailImage);
        ratingBar = findViewById(R.id.ratingBar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailDesc.setText(bundle.getString("Desc"));
            detailImage.setImageResource(bundle.getInt("Image"));
            detailTitle.setText(bundle.getString("Title"));
        }

        // Show rating dialog
        RateUs rateUsDialog = new RateUs(DetailsActivity.this);
        Objects.requireNonNull(rateUsDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        rateUsDialog.setCancelable(false);
        rateUsDialog.show();
    }

    public void submitStars(View view) {
        System.out.println("Star amount: " + ratingBar.getRating());
    }
}
