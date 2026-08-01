package com.example.kwiztorya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ChapterSelectionActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private MediaPlayer backgroundMusic;
    private LinearLayout chaptersContainer;

    // Era data
    private final String[] eraTitles = {
            "Pre-Colonial Era",
            "Spanish Colonial Era",
            "American Period",
            "Japanese Occupation",
            "Modern Era"
    };

    private final String[] eraDescriptions = {
            "Explore indigenous societies and cultures before Spanish arrival",
            "300 years of Spanish rule, from Magellan to revolution",
            "American colonization and road to independence",
            "World War II and Japanese occupation period",
            "Post-war Philippines to contemporary times"
    };

    private final String[] eraTags = {
            "pre_colonial",
            "spanish_colonial",
            "american_period",
            "japanese_occupation",
            "modern_era"
    };

    private final String[] eraIcons = {
            "🏝️",  // Pre-Colonial
            "⛪",   // Spanish Colonial
            "🗽",   // American Period
            "🎌",   // Japanese Occupation
            "🏙️"    // Modern Era
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_selection);

        // Initialize shared preferences
        preferences = getSharedPreferences("KwiztoryaPrefs", MODE_PRIVATE);

        // Initialize background music
        initializeBackgroundMusic();

        // Find the back button (ImageButton)
        ImageButton backButton = findViewById(R.id.btn_back);
        chaptersContainer = findViewById(R.id.chapters_container);

        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                // Play button click sound if enabled
                playButtonClickSound();
                finish();
            });
        }

        // Load eras with progression
        loadErasWithProgression();
    }

    private void loadErasWithProgression() {
        // Clear existing views
        chaptersContainer.removeAllViews();

        for (int i = 0; i < eraTitles.length; i++) {
            // Inflate the era item layout
            View eraItem = getLayoutInflater().inflate(R.layout.era_item_layout, null);

            TextView eraTitle = eraItem.findViewById(R.id.era_title);
            TextView eraDescription = eraItem.findViewById(R.id.era_description);
            TextView eraStatus = eraItem.findViewById(R.id.era_status);
            TextView eraQuestions = eraItem.findViewById(R.id.era_questions);
            TextView eraIcon = eraItem.findViewById(R.id.era_icon);
            View lockIcon = eraItem.findViewById(R.id.lock_icon);

            // Set era data
            eraTitle.setText(eraTitles[i]);
            eraDescription.setText(eraDescriptions[i]);
            eraQuestions.setText("6 Historical Questions");
            eraIcon.setText(eraIcons[i]);

            // Check if era is unlocked using ProgressManager only
            boolean isUnlocked = isEraUnlocked(i);
            if (isUnlocked) {
                eraItem.setAlpha(1.0f);
                lockIcon.setVisibility(View.GONE);
                eraStatus.setText("AVAILABLE");
                eraStatus.setBackgroundResource(R.drawable.status_available);

                final int eraIndex = i;
                final String chapterId = eraTags[i];

                eraItem.setOnClickListener(v -> {
                    playButtonClickSound();
                    Log.d("ChapterSelection", "Opening era: " + eraIndex + " with chapterId: " + chapterId);
                    openEraQuiz(eraIndex, chapterId);
                });
            } else {
                eraItem.setAlpha(0.6f);
                lockIcon.setVisibility(View.VISIBLE);
                eraStatus.setText("LOCKED");
                eraStatus.setBackgroundResource(R.drawable.status_locked);
                eraItem.setOnClickListener(v -> {
                    playButtonClickSound();
                    // Show locked message
                    android.widget.Toast.makeText(this, "Complete the previous era to unlock this one!", android.widget.Toast.LENGTH_SHORT).show();
                });
            }

            chaptersContainer.addView(eraItem);
        }
    }

    private boolean isEraUnlocked(int eraIndex) {
        // Use ProgressManager only - no database dependency
        ProgressManager progressManager = new ProgressManager(this);
        return progressManager.isEraUnlocked(eraIndex);
    }

    private void openEraQuiz(int eraIndex, String chapterId) {
        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtra("CHAPTER_ID", chapterId);
        intent.putExtra("ERA_INDEX", eraIndex);
        startActivity(intent);
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
            }
        } catch (Exception e) {
            Log.e("ChapterSelection", "Error initializing background music: " + e.getMessage());
        }
    }

    private void startBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            try {
                backgroundMusic.start();
            } catch (Exception e) {
                Log.e("ChapterSelection", "Error starting background music: " + e.getMessage());
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
                Log.e("ChapterSelection", "Error playing button click sound: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the era progression when returning from gameplay
        loadErasWithProgression();

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