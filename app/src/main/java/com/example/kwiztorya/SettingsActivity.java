package com.example.kwiztorya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "KwiztoryaPrefs";
    private static final String KEY_SOUND_EFFECTS = "sound_effects";
    private static final String KEY_BACKGROUND_MUSIC = "background_music";
    private static final String USER_PREFS = "UserPrefs";
    private static final String USER_LOGGED_IN = "user_logged_in";

    private MediaPlayer backgroundMusic;
    private MediaPlayer soundEffectPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up back button
        setupBackButton();

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        setupAudioControls();
        setupLogoutButton();
        initializeBackgroundMusic();
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Play button click sound if enabled
                    if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
                        try {
                            MediaPlayer.create(SettingsActivity.this, R.raw.button_click).start();
                        } catch (Exception e) {
                            Log.e("Settings", "Error playing back button sound");
                        }
                    }
                    // Go back to previous activity (Home)
                    finish();
                }
            });
        }
    }

    private void setupAudioControls() {
        SwitchCompat soundEffectsSwitch = findViewById(R.id.soundEffectsSwitch);
        SwitchCompat backgroundMusicSwitch = findViewById(R.id.backgroundMusicSwitch);

        // Load saved preferences
        soundEffectsSwitch.setChecked(preferences.getBoolean(KEY_SOUND_EFFECTS, true));
        backgroundMusicSwitch.setChecked(preferences.getBoolean(KEY_BACKGROUND_MUSIC, true));

        // Set listeners
        soundEffectsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(KEY_SOUND_EFFECTS, isChecked).apply();
            Log.d("Settings", "Sound effects: " + isChecked);

            // Play test sound when enabling
            if (isChecked) {
                playTestSound();
            }
        });

        backgroundMusicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(KEY_BACKGROUND_MUSIC, isChecked).apply();
            Log.d("Settings", "Background music: " + isChecked);

            if (isChecked) {
                startBackgroundMusic();
            } else {
                stopBackgroundMusic();
            }
        });
    }

    private void setupLogoutButton() {
        Button logoutButton = findViewById(R.id.logoutButton);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Play button click sound if enabled
                    if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
                        try {
                            MediaPlayer.create(SettingsActivity.this, R.raw.button_click).start();
                        } catch (Exception e) {
                            Log.e("Settings", "Error playing logout button sound");
                        }
                    }
                    logoutUser();
                }
            });
        }
    }

    /**
     * Logs out the user and returns to Login screen
     */
    private void logoutUser() {
        // Clear user session data
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putBoolean(USER_LOGGED_IN, false);
        editor.remove("username"); // Remove stored username if any
        editor.apply();

        // Stop background music
        stopBackgroundMusic();

        // Show logout message
        Log.d("Settings", "User logged out successfully");

        // Navigate to Login and clear back stack
        Intent intent = new Intent(SettingsActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Only change the volume line in initializeBackgroundMusic():
    private void initializeBackgroundMusic() {
        try {
            backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
            if (backgroundMusic != null) {
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(1.0f, 1.0f); // FIXED: Changed from 10.0f to 1.0f

                if (preferences.getBoolean(KEY_BACKGROUND_MUSIC, true)) {
                    backgroundMusic.start();
                }
            }
        } catch (Exception e) {
            Log.e("Settings", "Error initializing background music: " + e.getMessage());
        }
    }

    private void playTestSound() {
        try {
            // Release previous sound effect if playing
            if (soundEffectPlayer != null) {
                soundEffectPlayer.release();
                soundEffectPlayer = null;
            }

            // Create and play button click sound
            soundEffectPlayer = MediaPlayer.create(this, R.raw.button_click);
            if (soundEffectPlayer != null) {
                soundEffectPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    soundEffectPlayer = null;
                });
                soundEffectPlayer.start();
            }
        } catch (Exception e) {
            Log.e("Settings", "Error playing test sound: " + e.getMessage());
        }
    }

    private void startBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            try {
                backgroundMusic.start();
            } catch (Exception e) {
                Log.e("Settings", "Error starting background music: " + e.getMessage());
            }
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause background music when app is not visible
        stopBackgroundMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume background music if setting is enabled
        if (preferences.getBoolean(KEY_BACKGROUND_MUSIC, true)) {
            startBackgroundMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up media players
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
        if (soundEffectPlayer != null) {
            soundEffectPlayer.release();
            soundEffectPlayer = null;
        }
    }

    // Helper methods for other activities to check settings
    public static boolean areSoundEffectsEnabled(SharedPreferences prefs) {
        return prefs.getBoolean(KEY_SOUND_EFFECTS, true);
    }

    public static boolean isBackgroundMusicEnabled(SharedPreferences prefs) {
        return prefs.getBoolean(KEY_BACKGROUND_MUSIC, true);
    }
}