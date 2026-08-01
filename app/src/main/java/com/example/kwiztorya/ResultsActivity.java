package com.example.kwiztorya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class ResultsActivity extends AppCompatActivity {

    private TextView scoreTextView, titleTextView, messageTextView;
    private TextView correctCountTextView, totalCountTextView, percentageTextView;
    private Button playAgainButton, backToChaptersButton;
    private ImageView resultIcon, confetti;
    private CardView mainCard;
    private MediaPlayer successSound, failSound;
    private int score, totalQuestions;
    private boolean passed;
    private String chapterId;
    private int eraIndex;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize shared preferences for sound settings
        preferences = getSharedPreferences("KwiztoryaPrefs", MODE_PRIVATE);

        initializeViews();
        retrieveIntentData();
        setupUI();
        setupAnimations();
        setupButtonListeners();
        playResultSound();
    }

    private void initializeViews() {
        scoreTextView = findViewById(R.id.text_view_score);
        titleTextView = findViewById(R.id.text_view_title);
        messageTextView = findViewById(R.id.text_view_message);
        correctCountTextView = findViewById(R.id.text_view_correct_count);
        totalCountTextView = findViewById(R.id.text_view_total_count);
        percentageTextView = findViewById(R.id.text_view_percentage);
        playAgainButton = findViewById(R.id.button_play_again);
        backToChaptersButton = findViewById(R.id.button_back_to_chapters);
        resultIcon = findViewById(R.id.image_result_icon);
        confetti = findViewById(R.id.image_confetti);
        mainCard = findViewById(R.id.card_view);

        // Initialize sound effects only if sound is enabled
        if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
            try {
                successSound = MediaPlayer.create(this, R.raw.correct_answer);
                failSound = MediaPlayer.create(this, R.raw.wrong_answer);
            } catch (Exception e) {
                Log.e("ResultsActivity", "Error loading sound effects: " + e.getMessage());
                // Use button_click as fallback if specific sounds don't exist
                successSound = MediaPlayer.create(this, R.raw.button_click);
                failSound = MediaPlayer.create(this, R.raw.button_click);
            }
        }
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        score = intent.getIntExtra("SCORE", 0);
        totalQuestions = intent.getIntExtra("TOTAL", 0);
        passed = intent.getBooleanExtra("PASSED", false);
        chapterId = intent.getStringExtra("CHAPTER_ID");
        eraIndex = intent.getIntExtra("ERA_INDEX", 0);

        // Log the received data for debugging
        Log.d("ResultsActivity", "Chapter ID: " + chapterId);
        Log.d("ResultsActivity", "Era Index: " + eraIndex);
        Log.d("ResultsActivity", "Score: " + score + "/" + totalQuestions);
        Log.d("ResultsActivity", "Passed: " + passed);
    }

    private void setupUI() {
        // Calculate percentage
        int percentage = totalQuestions > 0 ? (int) ((score / (float) totalQuestions) * 100) : 0;

        // Update text views
        scoreTextView.setText(String.format("%d/%d", score, totalQuestions));
        correctCountTextView.setText(String.valueOf(score));
        totalCountTextView.setText(String.valueOf(totalQuestions));
        percentageTextView.setText(String.format("%d%%", percentage));

        // Set result based on performance
        if (percentage >= 90) {
            setResultUI("Quiz Master!", "Outstanding! Your knowledge of Philippine history is truly exceptional!",
                    R.drawable.ic_quiz_trophy, R.color.correct_green);
        } else if (percentage >= 70) {
            setResultUI("Great Job!", "Well done! You have a solid understanding of Philippine history!",
                    R.drawable.ic_star, R.color.correct_green);
        } else if (percentage >= 50) {
            setResultUI("Good Effort!", "Not bad! Keep learning about Philippine history!",
                    R.drawable.ic_flag, R.color.dark_brown);
        } else {
            setResultUI("Keep Learning!", "Don't give up! Philippine history is fascinating - try again!",
                    R.drawable.ic_book, R.color.incorrect_red);
        }

        // Show confetti for excellent scores (70% or higher)
        if (percentage >= 70 && confetti != null) {
            confetti.setVisibility(View.VISIBLE);
        }
    }

    private void setResultUI(String title, String message, int iconRes, int colorRes) {
        titleTextView.setText(title);
        messageTextView.setText(message);

        // Only set icon if the resource exists
        try {
            resultIcon.setImageResource(iconRes);
        } catch (Exception e) {
            Log.e("ResultsActivity", "Icon resource not found: " + iconRes);
            // Use a default icon or leave as is
        }

        int color = ContextCompat.getColor(this, colorRes);
        scoreTextView.setTextColor(color);
        correctCountTextView.setTextColor(color);
    }

    private void setupAnimations() {
        try {
            // Scale animation for the main card
            Animation cardAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up_bounce);
            if (mainCard != null && cardAnimation != null) {
                mainCard.startAnimation(cardAnimation);
            }

            // Show confetti with delay for excellent scores
            if (passed && confetti != null && confetti.getVisibility() == View.VISIBLE) {
                new Handler().postDelayed(() -> {
                    try {
                        Animation confettiAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                        if (confettiAnim != null) {
                            confetti.startAnimation(confettiAnim);
                        }
                    } catch (Exception e) {
                        Log.e("ResultsActivity", "Error loading confetti animation");
                    }
                }, 500);
            }
        } catch (Exception e) {
            Log.e("ResultsActivity", "Error setting up animations: " + e.getMessage());
        }
    }

    private void setupButtonListeners() {
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonClickSound();
                animateButtonClick(v);

                // Log what we're passing back
                Log.d("ResultsActivity", "Play Again - Chapter ID: " + chapterId);
                Log.d("ResultsActivity", "Play Again - Era Index: " + eraIndex);

                // Go back to the same chapter/era with the correct data
                Intent intent = new Intent(ResultsActivity.this, GameplayActivity.class);
                intent.putExtra("CHAPTER_ID", chapterId);
                intent.putExtra("ERA_INDEX", eraIndex);
                startActivity(intent);
                finish();
            }
        });

        backToChaptersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonClickSound();
                animateButtonClick(v);

                Intent intent = new Intent(ResultsActivity.this, ChapterSelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void animateButtonClick(View view) {
        try {
            Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_up);
            if (scaleAnim != null) {
                view.startAnimation(scaleAnim);
            }
        } catch (Exception e) {
            Log.e("ResultsActivity", "Error animating button: " + e.getMessage());
        }
    }

    private void playResultSound() {
        if (!SettingsActivity.areSoundEffectsEnabled(preferences)) {
            return; // Don't play sounds if disabled
        }

        try {
            if (passed && successSound != null) {
                successSound.start();
            } else if (!passed && failSound != null) {
                failSound.start();
            }
        } catch (Exception e) {
            Log.e("ResultsActivity", "Error playing result sound: " + e.getMessage());
        }
    }

    private void playButtonClickSound() {
        if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
            try {
                MediaPlayer.create(this, R.raw.button_click).start();
            } catch (Exception e) {
                Log.e("ResultsActivity", "Error playing button click sound: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop sounds when activity is not in focus
        if (successSound != null && successSound.isPlaying()) {
            successSound.pause();
        }
        if (failSound != null && failSound.isPlaying()) {
            failSound.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up media players
        if (successSound != null) {
            successSound.release();
            successSound = null;
        }
        if (failSound != null) {
            failSound.release();
            failSound = null;
        }
    }
}