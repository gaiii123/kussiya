package com.example.kussiya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.Kussiya.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;
//IM/2021/069
//IM/2021/097
public class add_item extends AppCompatActivity {

    ImageView recipeImage;
    Uri uri;
    EditText txt_name, txt_description, txt_price;
    String imageUrl, userID;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize UI elements
        recipeImage = findViewById(R.id.iv_foodImage);
        txt_name = findViewById(R.id.txt_recipe_name);
        txt_description = findViewById(R.id.text_description);
        txt_price = findViewById(R.id.text_price);
        Button btnSelectImage = findViewById(R.id.btn_select_image);
        Button btnUploadRecipe = findViewById(R.id.btn_upload_recipe);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        // Set OnClickListeners
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSelectImage(v);
            }
        });

        btnUploadRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUploadRecipe(v);
            }
        });

        // Set up BottomNavigationView using BottomNavigationHelper
        bottomNavigationHelper.setupBottomNavigation(this, bottomNavigationView, R.id.bottom_add);
    }


    public void btnSelectImage(View view) {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            recipeImage.setImageURI(uri);
        } else {
            Snackbar.make(findViewById(R.id.txt_recipe_name), "You haven't picked an image", Snackbar.LENGTH_LONG).show();
        }
    }

    public void uploadImage(final View view) {
        if (uri == null) {
            Snackbar.make(view, "Please Upload Image", Snackbar.LENGTH_LONG).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("RecipeImage").child(uri.getLastPathSegment());


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Recipe Uploading...");
        progressDialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            imageUrl = task.getResult().toString();
                            uploadRecipe(view);
                        } else {
                            Snackbar.make(view, "Failed to get image URL", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Snackbar.make(view, "Image Upload Failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void btnUploadRecipe(View view) {
        if (uri == null) {
            Snackbar.make(view, "Please Upload Image", Snackbar.LENGTH_LONG).show();
        } else if (txt_name.getText().toString().isEmpty()) {
            Snackbar.make(view, "Please Write Recipe Name", Snackbar.LENGTH_LONG).show();
        } else if (txt_description.getText().toString().isEmpty()) {
            Snackbar.make(view, "Please Write Recipe Description", Snackbar.LENGTH_LONG).show();
        } else {
            uploadImage(view);
        }
    }

    public void uploadRecipe(final View view) {
        FoodData foodData = new FoodData(txt_name.getText().toString(),
                txt_description.getText().toString(),
                txt_price.getText().toString(),
                imageUrl);

        String myCurrentDateTime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        FirebaseDatabase.getInstance().getReference("Recipe").child(myCurrentDateTime).setValue(foodData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Recipe added to Database");
                            Snackbar.make(view, "Recipe Uploaded Successfully!", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Log.d("TAG", "Recipe not added to Database " + task.getException().getMessage());
                        }
                    }
                });

        String recipeID = firebaseFirestore.collection("users").document(userID).collection("Recipes").document().getId();
        FoodData fireStoreFoodData = new FoodData(txt_name.getText().toString(),
                txt_description.getText().toString(),
                txt_price.getText().toString(),
                imageUrl,
                myCurrentDateTime,
                recipeID);

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID)
                .collection("Recipes").document(recipeID);
        documentReference.set(fireStoreFoodData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Recipe added to Firestore");
                        finish(); // Finish activity after successful upload
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Recipe not added to Firestore " + e.getMessage());
                        Snackbar.make(view, "Failed to Upload Recipe: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
