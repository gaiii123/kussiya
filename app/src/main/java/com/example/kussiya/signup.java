package com.example.kussiya;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.Kussiya.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class signup extends AppCompatActivity {

    private EditText emailField, passwordField, confirmPasswordField, usernameField;
    private Button signupButton;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    TextView name, mail;

   /* private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                auth = FirebaseAuth.getInstance();
                                Toast.makeText(signup.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(signup.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    });*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge mode
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.signup);

        FirebaseApp.initializeApp(this);

        // Log for debugging Firebase initialization
        if (FirebaseApp.getApps(this).isEmpty()) {
            Log.e("Signup", "Firebase initialization failed.");
        } else {
            Log.d("Signup", "Firebase initialized successfully.");
        }

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id)) // Ensure client_id is correctly configured
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(signup.this, options);

        SignInButton signInButton = findViewById(R.id.google_login);
        signInButton.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
        //  activityResultLauncher.launch(intent);
        });

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        if (auth == null) {
            Log.e("Signup", "FirebaseAuth initialization failed.");
        }

        // Initialize UI elements
        usernameField = findViewById(R.id.username);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.ConfirmPassword);
        signupButton = findViewById(R.id.button2);

        // Set up the sign-up button click listener
        signupButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();

            // Validate user input
            if (username.isEmpty()) {
                usernameField.setError("Username is required");
                usernameField.requestFocus();
                return;
            }

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

            if (!password.equals(confirmPassword)) {
                confirmPasswordField.setError("Passwords do not match");
                confirmPasswordField.requestFocus();
                return;
            }

            // Log the event before attempting user creation
            Log.d("Signup", "Attempting to create user with email: " + email);

            // Create user in Firebase
            createUser(email, password);
        });

        // Set up the sign-up text click listener
        TextView LoginTextView = findViewById(R.id.textView8);
        LoginTextView.setOnClickListener(v -> {
            // Navigate to the login activity
            Intent intent = new Intent(signup.this, login.class);
            startActivity(intent);
        });
    }

    private void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-up successful
                        Log.d("Signup", "User created successfully.");
                        Toast.makeText(signup.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                        // Navigate the user to another activity, e.g., home page
                        Intent intent = new Intent(signup.this, home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign-up fails, display a message to the user
                        Log.e("Signup", "Registration failed: " + task.getException().getMessage());
                        Toast.makeText(signup.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
