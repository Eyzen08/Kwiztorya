package com.example.kwiztorya;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {
    TextInputEditText signupUsername, signupPassword, signupConfirmPassword;
    TextView loginRedirectText;
    Button signupButton;
    ImageButton backButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmPassword = findViewById(R.id.signup_confirm_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);
        backButton = findViewById(R.id.backButton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) {
                    registerUser();
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(Signup.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void registerUser() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        String username = signupUsername.getText().toString().trim();
        String password = signupPassword.getText().toString();

        reference.child(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    signupUsername.setError("Username already taken");
                    Toast.makeText(Signup.this, "Username already exists, please choose another", Toast.LENGTH_SHORT).show();
                } else {
                    HelperClass helperClass = new HelperClass(username, password);
                    reference.child(username).setValue(helperClass)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(Signup.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                                goToLogin();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Signup.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(Signup.this, "Database error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        String username = signupUsername.getText().toString().trim();
        String password = signupPassword.getText().toString();
        String confirmPassword = signupConfirmPassword.getText().toString();

        boolean isValid = true;

        signupUsername.setError(null);
        signupPassword.setError(null);
        signupConfirmPassword.setError(null);

        if (username.isEmpty()) {
            signupUsername.setError("Username is required.");
            isValid = false;
        } else if (username.length() < 3) {
            signupUsername.setError("Username must be at least 3 characters.");
            isValid = false;
        }

        if (password.isEmpty()) {
            signupPassword.setError("Password is required.");
            isValid = false;
        } else if (password.length() < 8) {
            signupPassword.setError("Password must be at least 8 characters.");
            isValid = false;
        } else if (!isPasswordComplex(password)) {
            signupPassword.setError("Must contain uppercase, lowercase, number, and symbol.");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            signupConfirmPassword.setError("Confirm password is required.");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            signupConfirmPassword.setError("Passwords do not match.");
            isValid = false;
        }

        return isValid;
    }

    private boolean isPasswordComplex(String password) {
        Pattern uppercasePattern = Pattern.compile(".*[A-Z].*");
        Matcher hasUppercase = uppercasePattern.matcher(password);

        Pattern lowercasePattern = Pattern.compile(".*[a-z].*");
        Matcher hasLowercase = lowercasePattern.matcher(password);

        Pattern digitPattern = Pattern.compile(".*[0-9].*");
        Matcher hasDigit = digitPattern.matcher(password);

        Pattern specialCharPattern = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        Matcher hasSpecialChar = specialCharPattern.matcher(password);

        return hasUppercase.matches() &&
                hasLowercase.matches() &&
                hasDigit.matches() &&
                hasSpecialChar.matches();
    }
}