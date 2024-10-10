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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class signup extends AppCompatActivity {

    private EditText emailField, passwordField, confirmPasswordField, usernameField, mobileNo;

    private Button signupButton;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    TextView name, mail;

   /* private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                        auth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(signup.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(signup.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                }
            }
    );
*/

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

      /*  GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Your client ID from Firebase
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(signup.this, gso);

        SignInButton signInButton = findViewById(R.id.google_login);
        signInButton.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
        //  activityResultLauncher.launch(intent);
        });*/

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
        mobileNo=findViewById(R.id.MobileNumber);


        // Set up the sign-up button click listener
        signupButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();
            String mobileNumber=mobileNo.getText().toString().trim();

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

            if (mobileNumber.isEmpty()){
                mobileNo.setError("Mobile is required");
                mobileNo.requestFocus();
            }

            if(mobileNumber.length()!=10){
                mobileNo.setError("Enter 10 Digit Number");
                mobileNo.requestFocus();
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
                        // User account created successfully
                        Log.d("Signup", "User created successfully.");

                        // Get the current user
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Send a verification email
                            sendVerificationEmail(user);

                            // Save user details to the database
                            String userId = user.getUid(); // Get the user ID from FirebaseAuth
                            String username = usernameField.getText().toString().trim();
                            String mobileNumber = mobileNo.getText().toString().trim();
                            saveUserDetails(userId, username, email, mobileNumber, password);
                        }

                    } else {
                        // Handle the case where the account already exists or other errors
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Log.d("Signup", "Account already exists. Deleting the old account.");

                            FirebaseUser existingUser = auth.getCurrentUser();
                            if (existingUser != null) {
                                existingUser.delete().addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        Log.d("Signup", "Old account deleted. Attempting sign up again.");
                                        createUser(email, password);
                                    } else {
                                        Log.e("Signup", "Failed to delete old account: " + deleteTask.getException().getMessage());
                                        Toast.makeText(signup.this, "Error occurred. Try again later.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Log.e("Signup", "Sign up failed: " + task.getException().getMessage());
                            Toast.makeText(signup.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
            if (verificationTask.isSuccessful()) {
                Log.d("Signup", "Verification email sent successfully.");
                Toast.makeText(signup.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();

                // Redirect to a verification page if needed
                Intent intent = new Intent(signup.this, VerifyEmailActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.e("Signup", "Failed to send verification email: " + verificationTask.getException().getMessage());
                Toast.makeText(signup.this, "Failed to send verification email. Try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }
    private DatabaseReference databaseReference;
    // Save the username to Firebase Realtime Database
    private void saveUserDetails(String userId, String username, String email, String mobileNumber, String password) {
        // Encrypt the password before saving (more on this below)
        String encryptedPassword = encryptPassword(password);

        // Create a reference to the Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(userId);

        // Save user data in a HashMap
        HashMap<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("mobileNumber", mobileNumber);
        userData.put("password", encryptedPassword);  // Save the encrypted password

        // Write to Firebase Realtime Database
        userRef.setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Signup", "User data saved in Realtime Database.");
            } else {
                Log.e("Signup", "Failed to save user data: " + task.getException().getMessage());
            }
        });


    }
    private String encryptPassword(String plainPassword) {
        // Generate a salt and hash the password
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(plainPassword, salt);
    }


}
