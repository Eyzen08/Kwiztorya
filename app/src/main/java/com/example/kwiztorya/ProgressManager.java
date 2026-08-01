package com.example.kwiztorya;

import android.content.Context;
import android.content.SharedPreferences;

public class ProgressManager {
    private SharedPreferences preferences;
    private SharedPreferences storyPreferences;

    public ProgressManager(Context context) {
        preferences = context.getSharedPreferences("KwiztoryaPrefs", Context.MODE_PRIVATE);
        storyPreferences = context.getSharedPreferences("StoryProgress", Context.MODE_PRIVATE);
    }

    // Era progress methods
    public void saveEraProgress(int eraIndex, boolean passed, int score) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("era_" + eraIndex + "_completed", passed);
        editor.putInt("era_" + eraIndex + "_score", score);
        editor.apply();
    }

    public boolean isEraUnlocked(int eraIndex) {
        if (eraIndex == 0) return true; // First era is always unlocked

        // Previous era must be completed to unlock next one
        return preferences.getBoolean("era_" + (eraIndex - 1) + "_completed", false);
    }

    public boolean isEraCompleted(int eraIndex) {
        return preferences.getBoolean("era_" + eraIndex + "_completed", false);
    }

    public int getEraScore(int eraIndex) {
        return preferences.getInt("era_" + eraIndex + "_score", 0);
    }

    // Story progress methods
    public void saveStoryProgress(int journeyNumber, int completedChapters) {
        SharedPreferences.Editor editor = storyPreferences.edit();
        editor.putInt("journey_" + journeyNumber + "_chapters", completedChapters);
        editor.apply();
    }

    public int getStoryProgress(int journeyNumber) {
        return storyPreferences.getInt("journey_" + journeyNumber + "_chapters", 0);
    }

    public void completeJourney(int journeyNumber) {
        SharedPreferences.Editor editor = storyPreferences.edit();
        editor.putInt("completed_journeys", journeyNumber);
        editor.apply();
    }

    public int getCompletedJourneys() {
        return storyPreferences.getInt("completed_journeys", 0);
    }

    public boolean isJourneyUnlocked(int journeyNumber) {
        if (journeyNumber == 1) return true; // First journey is always unlocked

        // Previous journey must be completed to unlock next one
        return storyPreferences.getInt("completed_journeys", 0) >= journeyNumber - 1;
    }
}