package com.example.kwiztorya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TermsActivity extends AppCompatActivity {

    private CheckBox termsCheckbox;
    private Button acceptButton, declineButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TermsPrefs";
    private static final String TERMS_ACCEPTED = "terms_accepted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terms);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if terms are already accepted
        if (sharedPreferences.getBoolean(TERMS_ACCEPTED, false)) {
            goToLogin();
            return;
        }

        termsCheckbox = findViewById(R.id.termsCheckbox);
        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);

        // Set the terms text with HTML formatting
        TextView termsText = findViewById(R.id.termsText);
        termsText.setText(Html.fromHtml(getString(R.string.terms_and_conditions_content), Html.FROM_HTML_MODE_LEGACY));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Accept button click listener
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (termsCheckbox.isChecked()) {
                    // Save acceptance and proceed to login
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(TERMS_ACCEPTED, true);
                    editor.apply();

                    goToLogin();
                } else {
                    Toast.makeText(TermsActivity.this, "Please accept the Terms and Conditions", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Decline button click listener
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the app if terms are declined
                Toast.makeText(TermsActivity.this, "You must accept the Terms and Conditions to use this app", Toast.LENGTH_LONG).show();
                finishAffinity(); // Close the app
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(TermsActivity.this, Login.class);
        startActivity(intent);
        finish(); // Close terms activity
    }
}