package com.example.kwiztorya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {
    private static final String USER_PREFS = "UserPrefs";
    private static final String USER_LOGGED_IN = "user_logged_in";
    private MediaPlayer backgroundMusic;

    private TextView textProfileTitle;
    private TextView textTotalScore;
    private TextView crownIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Check if user is logged in
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        if (!userPrefs.getBoolean(USER_LOGGED_IN, false)) {
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize views
        initializeViews();

        // Load user stats
        loadUserStats();

        // Initialize background music
        SharedPreferences preferences = getSharedPreferences("KwiztoryaPrefs", MODE_PRIVATE);
        initializeBackgroundMusic(preferences);

        // Set up button listeners
        setupButtonListeners(preferences);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        textProfileTitle = findViewById(R.id.text_profile_title);
        textTotalScore = findViewById(R.id.text_total_score);
        crownIcon = findViewById(R.id.crown_icon);

        if (textProfileTitle == null) Log.e("Home", "text_profile_title not found");
        if (textTotalScore == null) Log.e("Home", "text_total_score not found");
        if (crownIcon == null) Log.e("Home", "crown_icon not found");
    }

    private void setupButtonListeners(SharedPreferences preferences) {
        View.OnClickListener navigateToChapterSelection = v -> {
            playButtonClickSound(preferences);
            Intent intent = new Intent(Home.this, ChapterSelectionActivity.class);
            startActivity(intent);
        };

        View.OnClickListener navigateToSettings = v -> {
            playButtonClickSound(preferences);
            Intent intent = new Intent(Home.this, SettingsActivity.class);
            startActivity(intent);
        };

        View.OnClickListener navigateToStoryMode = v -> {
            playButtonClickSound(preferences);
            Intent intent = new Intent(Home.this, StoryModeActivity.class);
            startActivity(intent);
        };

        // Initialize buttons
        Button playButton = findViewById(R.id.btn_play);
        Button storyModeButton = findViewById(R.id.btn_story_mode);
        Button chapterSelectionButton = findViewById(R.id.btn_chapter_selection);
        Button settingsButton = findViewById(R.id.btn_settings);

        if (playButton != null) playButton.setOnClickListener(navigateToChapterSelection);
        if (storyModeButton != null) storyModeButton.setOnClickListener(navigateToStoryMode);
        if (chapterSelectionButton != null) chapterSelectionButton.setOnClickListener(navigateToChapterSelection);
        if (settingsButton != null) settingsButton.setOnClickListener(navigateToSettings);
    }

    private void loadUserStats() {
        if (textProfileTitle == null || textTotalScore == null || crownIcon == null) {
            Log.e("Home", "Text views not initialized - cannot load stats");
            return;
        }

        // Use ProgressManager instead of database
        ProgressManager progressManager = new ProgressManager(this);

        int totalScore = 0;
        int completedEras = 0;

        // Calculate total score and completed eras
        for (int i = 0; i < 5; i++) {
            totalScore += progressManager.getEraScore(i);
            if (progressManager.isEraCompleted(i)) {
                completedEras++;
            }
        }

        String rank = calculateRank(totalScore, completedEras);

        // Update UI
        textProfileTitle.setText(rank);
        String scoreText = "Total Score: " + totalScore + " • " + completedEras + "/5 Eras";
        textTotalScore.setText(scoreText);

        // Update crown icon based on rank
        updateCrownIcon(rank);
    }

    private String calculateRank(int totalScore, int completedEras) {
        if (completedEras == 5 && totalScore >= 25) {
            return "History Master";
        } else if (completedEras >= 3 && totalScore >= 15) {
            return "Expert Historian";
        } else if (completedEras >= 1 && totalScore >= 8) {
            return "Adept Learner";
        } else {
            return "History Novice";
        }
    }

    private void updateCrownIcon(String rank) {
        if (crownIcon != null) {
            switch (rank) {
                case "History Master":
                    crownIcon.setText("👑");
                    break;
                case "Expert Historian":
                    crownIcon.setText("⭐");
                    break;
                case "Adept Learner":
                    crownIcon.setText("📚");
                    break;
                default:
                    crownIcon.setText("🌱");
                    break;
            }
        }
    }

    private void initializeBackgroundMusic(SharedPreferences preferences) {
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
            Log.e("Home", "Error initializing background music: " + e.getMessage());
        }
    }

    private void startBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            try {
                backgroundMusic.start();
            } catch (Exception e) {
                Log.e("Home", "Error starting background music: " + e.getMessage());
            }
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    private void playButtonClickSound(SharedPreferences preferences) {
        if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
            try {
                MediaPlayer.create(this, R.raw.button_click).start();
            } catch (Exception e) {
                Log.e("Home", "Error playing button click sound: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user stats when returning to home
        loadUserStats();

        SharedPreferences preferences = getSharedPreferences("KwiztoryaPrefs", MODE_PRIVATE);
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
        // No database to close anymore
    }
}