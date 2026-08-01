package com.example.kwiztorya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class StoryModeActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private MediaPlayer backgroundMusic;
    private int completedJourneys = 0;

    private TextView progressText, progressPercentage;
    private ProgressBar progressBar;
    private ImageButton backButton;

    // Journey data
    private final String[] journeyTitles = {
            "Ancient Philippines",
            "Colonial Encounters",
            "American Transition",
            "War and Occupation",
            "Modern Nation"
    };

    private final String[] journeySubtitles = {
            "Pre-Colonial Era",
            "Spanish Colonial Era",
            "American Period",
            "Japanese Occupation",
            "Modern Era"
    };

    private final String[] journeyDescriptions = {
            "Discover the rich cultures, traditions, and societies of ancient Philippines before Spanish colonization",
            "Experience 300 years of Spanish rule, from Magellan's arrival to the Philippine Revolution",
            "Witness the transition from Spanish to American rule and the road to independence",
            "Navigate through the challenges of World War II and Japanese occupation",
            "Explore the post-war era leading to contemporary Philippine society"
    };

    private final String[] journeyIcons = {
            "🏝️", "⛪", "🗽", "🎌", "🏙️"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_story_mode);
            Log.d("StoryMode", "Layout inflated successfully");

            // Initialize shared preferences
            preferences = getSharedPreferences("KwiztoryaPrefs", MODE_PRIVATE);

            // Initialize views
            initializeViews();

            // Set up back button
            setupBackButton();

            // Load story progress
            loadStoryProgress();

            // Set up button listeners
            setupButtonListeners();

            // Initialize background music
            initializeBackgroundMusic();

        } catch (Exception e) {
            Log.e("StoryMode", "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading Story Mode", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            progressText = findViewById(R.id.text_story_progress);
            progressPercentage = findViewById(R.id.text_progress_percentage);
            progressBar = findViewById(R.id.progress_story_mode);
            backButton = findViewById(R.id.btn_back);

            if (progressText == null) Log.e("StoryMode", "text_story_progress not found");
            if (progressPercentage == null) Log.e("StoryMode", "text_progress_percentage not found");
            if (progressBar == null) Log.e("StoryMode", "progress_story_mode not found");
            if (backButton == null) Log.e("StoryMode", "btn_back not found");

        } catch (Exception e) {
            Log.e("StoryMode", "Error initializing views: " + e.getMessage());
        }
    }

    private void setupBackButton() {
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                playButtonClickSound();
                finish();
            });
        }
    }

    private void loadStoryProgress() {
        try {
            ProgressManager progressManager = new ProgressManager(this);
            completedJourneys = progressManager.getCompletedJourneys();
            Log.d("StoryMode", "Loaded progress: " + completedJourneys + " journeys");

            // Update progress display
            updateProgressDisplay();

        } catch (Exception e) {
            Log.e("StoryMode", "Error loading story progress: " + e.getMessage());
        }
    }

    private void updateProgressDisplay() {
        try {
            if (progressText != null) {
                String progressString = "Progress: " + completedJourneys + "/5 Journeys";
                progressText.setText(progressString);
            }

            if (progressPercentage != null) {
                int percentage = (completedJourneys * 100) / 5;
                progressPercentage.setText(percentage + "% Complete");
            }

            if (progressBar != null) {
                int progress = (completedJourneys * 100) / 5;
                progressBar.setProgress(progress);
            }

            // Update button states based on progress
            updateButtonStates();

        } catch (Exception e) {
            Log.e("StoryMode", "Error updating progress display: " + e.getMessage());
        }
    }

    private void updateButtonStates() {
        try {
            int[] journeyButtons = {
                    R.id.btn_journey_1, R.id.btn_journey_2, R.id.btn_journey_3,
                    R.id.btn_journey_4, R.id.btn_journey_5
            };

            for (int i = 0; i < journeyButtons.length; i++) {
                Button journeyButton = findViewById(journeyButtons[i]);
                if (journeyButton != null) {
                    if (i <= completedJourneys) {
                        // Journey is unlocked or completed
                        journeyButton.setEnabled(true);
                        journeyButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.story_mode_green));
                        journeyButton.setText("Begin Journey");

                        if (i < completedJourneys) {
                            journeyButton.setText("Play Again");
                        }
                    } else if (i == completedJourneys + 1) {
                        // Next journey to unlock
                        journeyButton.setEnabled(false);
                        journeyButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
                        journeyButton.setText("Complete Journey " + i + " First");
                    } else {
                        // Future locked journey
                        journeyButton.setEnabled(false);
                        journeyButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
                        journeyButton.setText("Locked");
                    }
                }
            }

        } catch (Exception e) {
            Log.e("StoryMode", "Error updating button states: " + e.getMessage());
        }
    }

    private void setupButtonListeners() {
        try {
            int[] journeyButtons = {
                    R.id.btn_journey_1, R.id.btn_journey_2, R.id.btn_journey_3,
                    R.id.btn_journey_4, R.id.btn_journey_5
            };

            View.OnClickListener startJourneyListener = v -> {
                playButtonClickSound();

                // Determine which journey was clicked
                int journeyNumber = 1;
                int buttonId = v.getId();

                for (int i = 0; i < journeyButtons.length; i++) {
                    if (buttonId == journeyButtons[i]) {
                        journeyNumber = i + 1;
                        break;
                    }
                }

                // Check if journey is unlocked
                if (journeyNumber <= completedJourneys + 1) {
                    startJourneyActivity(journeyNumber);
                } else {
                    Toast.makeText(this, "Complete the previous journeys first!", Toast.LENGTH_SHORT).show();
                }
            };

            for (int journeyButton : journeyButtons) {
                Button button = findViewById(journeyButton);
                if (button != null) {
                    button.setOnClickListener(startJourneyListener);
                }
            }

        } catch (Exception e) {
            Log.e("StoryMode", "Error setting up button listeners: " + e.getMessage());
        }
    }

    private void startJourneyActivity(int journeyNumber) {
        try {
            Log.d("StoryMode", "Starting Journey " + journeyNumber);

            // Create intent to start StoryGameplayActivity
            Intent intent = new Intent(this, StoryGameplayActivity.class);
            intent.putExtra("JOURNEY_NUMBER", journeyNumber);
            intent.putExtra("JOURNEY_TITLE", journeyTitles[journeyNumber - 1]);
            intent.putExtra("JOURNEY_SUBTITLE", journeySubtitles[journeyNumber - 1]);
            startActivity(intent);

            Log.d("StoryMode", "StoryGameplayActivity started with journey: " + journeyNumber);

        } catch (Exception e) {
            Log.e("StoryMode", "Error starting journey: " + e.getMessage());
            Toast.makeText(this, "Error starting journey", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeBackgroundMusic() {
        try {
            backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
            if (backgroundMusic != null) {
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(0.5f, 0.5f);

                if (SettingsActivity.isBackgroundMusicEnabled(preferences)) {
                    backgroundMusic.start();
                }
            } else {
                Log.w("StoryMode", "Background music resource not found");
            }
        } catch (Exception e) {
            Log.e("StoryMode", "Error initializing background music: " + e.getMessage());
        }
    }

    private void startBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            try {
                backgroundMusic.start();
            } catch (Exception e) {
                Log.e("StoryMode", "Error starting background music: " + e.getMessage());
            }
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    private void playButtonClickSound() {
        if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
            try {
                MediaPlayer.create(this, R.raw.button_click).start();
            } catch (Exception e) {
                Log.e("StoryMode", "Error playing button click sound: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload progress when returning to this activity
        loadStoryProgress();

        if (SettingsActivity.isBackgroundMusicEnabled(preferences)) {
            startBackgroundMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBackgroundMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
}