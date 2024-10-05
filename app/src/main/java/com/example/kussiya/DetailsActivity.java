package com.example.kussiya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Kussiya.R;
import com.example.Kussiya.videoView;
//IM/2021/069
//IM/2021/097
public class DetailsActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle;
    ImageView detailImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.Kussiya.R.layout.activity_details);

        detailDesc = findViewById(R.id.detailDesc);
        detailTitle = findViewById(R.id.detailTitle);
        detailImage = findViewById(R.id.detailImage);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDesc.setText(bundle.getInt("Desc"));
            detailImage.setImageResource(bundle.getInt("Image"));
            detailTitle.setText(bundle.getString("Title"));
        }
        Button VidButton = findViewById(R.id.playvideo);



        // Set up the logout button click listener
        VidButton.setOnClickListener(v -> {

            // Navigate to the login activity
            Intent intent = new Intent(DetailsActivity.this, videoView.class);
            startActivity(intent);
            finish(); // Close the home activity
        });
    }
}