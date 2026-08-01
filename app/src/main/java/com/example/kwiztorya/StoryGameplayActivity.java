package com.example.kwiztorya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class StoryGameplayActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private MediaPlayer backgroundMusic;
    private int currentJourney;
    private int currentChapter = 1;
    private int totalChapters;
    private int score = 0;

    private TextView textStoryTitle, textChapterProgress, textStoryContent, textQuestion;
    private LinearLayout layoutAnswers;
    private ProgressBar progressChapter;
    private Button btnNext;

    private List<StoryChapter> storyChapters;
    private StoryChapter currentStoryChapter;

    private static final String TAG = "StoryGameplay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_gameplay);

        Log.d(TAG, "=== STORY GAMEPLAY STARTED ===");

        // Get journey number from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("JOURNEY_NUMBER")) {
            currentJourney = intent.getIntExtra("JOURNEY_NUMBER", 1);
            Log.d(TAG, "Received journey number: " + currentJourney);
        } else {
            currentJourney = 1;
            Log.e(TAG, "No JOURNEY_NUMBER found, defaulting to 1");
        }

        // Initialize shared preferences
        preferences = getSharedPreferences("KwiztoryaPrefs", MODE_PRIVATE);

        // Initialize views
        initializeViews();

        // Set up the story based on journey
        setupStoryJourney();

        // Load first chapter
        if (storyChapters != null && !storyChapters.isEmpty()) {
            loadChapter(currentChapter);
        } else {
            Log.e(TAG, "No chapters available!");
            Toast.makeText(this, "Error: No story content available", Toast.LENGTH_LONG).show();
            finish();
        }

        // Initialize background music
        initializeBackgroundMusic();

        // Set up button listeners
        setupButtonListeners();
    }

    private void initializeViews() {
        textStoryTitle = findViewById(R.id.text_story_title);
        textChapterProgress = findViewById(R.id.text_chapter_progress);
        textStoryContent = findViewById(R.id.text_story_content);
        textQuestion = findViewById(R.id.text_question);
        layoutAnswers = findViewById(R.id.layout_answers);
        progressChapter = findViewById(R.id.progress_chapter);
        btnNext = findViewById(R.id.btn_next);

        // Verify all views are found
        if (textStoryTitle == null) Log.e(TAG, "textStoryTitle not found");
        if (textChapterProgress == null) Log.e(TAG, "textChapterProgress not found");
        if (textStoryContent == null) Log.e(TAG, "textStoryContent not found");
        if (textQuestion == null) Log.e(TAG, "textQuestion not found");
        if (layoutAnswers == null) Log.e(TAG, "layoutAnswers not found");
        if (progressChapter == null) Log.e(TAG, "progressChapter not found");
        if (btnNext == null) Log.e(TAG, "btnNext not found");
    }

    private void setupStoryJourney() {
        storyChapters = new ArrayList<>();
        Log.d(TAG, "Setting up journey: " + currentJourney);

        switch (currentJourney) {
            case 1: // Pre-Colonial Philippines
                totalChapters = 8;
                textStoryTitle.setText("Journey 1: Pre-Colonial Philippines");
                initializePreColonialChapters();
                break;
            case 2: // Spanish Colonial Period
                totalChapters = 10;
                textStoryTitle.setText("Journey 2: Spanish Colonial Period");
                initializeSpanishColonialChapters();
                break;
            case 3: // American Colonial Period
                totalChapters = 9;
                textStoryTitle.setText("Journey 3: American Colonial Period");
                initializeAmericanPeriodChapters();
                break;
            case 4: // Japanese Occupation
                totalChapters = 7;
                textStoryTitle.setText("Journey 4: Japanese Occupation");
                initializeJapaneseOccupationChapters();
                break;
            case 5: // Modern Philippines
                totalChapters = 11;
                textStoryTitle.setText("Journey 5: Modern Philippines");
                initializeModernPhilippinesChapters();
                break;
            default:
                totalChapters = 8;
                textStoryTitle.setText("Journey 1: Pre-Colonial Philippines");
                initializePreColonialChapters();
                break;
        }

        Log.d(TAG, "Total chapters for journey " + currentJourney + ": " + storyChapters.size());
        updateProgress();
    }

    private void initializePreColonialChapters() {
        Log.d(TAG, "Initializing Pre-Colonial chapters...");

        // Chapter 1
        storyChapters.add(new StoryChapter(
                "Chapter 1: The Young Datu's Heir",
                "You are Lakan, the eldest son of Datu Makisig of the barangay of Masagana. Today, your father is teaching you about the ancient trading practices with neighboring islands. 'Remember, Lakan,' he says, 'our people have traded with the Chinese, Malays, and Indians for centuries. The barangay survives through these connections.'",
                "What was the primary economic activity of pre-colonial Philippine societies that involved exchanging goods with neighboring countries?",
                new String[]{"Agriculture and farming", "Maritime trade and commerce", "Mining and gold production", "Weaving and textile making"},
                1 // Correct answer index (0-based)
        ));

        // Chapter 2
        storyChapters.add(new StoryChapter(
                "Chapter 2: The Baybayin Script",
                "As you walk through the village, you see the village scribe teaching children to write on bamboo strips using the ancient Baybayin script. Each symbol represents a syllable, and you remember your own lessons. 'Knowledge preserved is power earned,' your father often says.",
                "What writing system was used in the Philippines before Spanish colonization?",
                new String[]{"Hanunoo Script", "Baybayin Script", "Kawi Script", "Sanskrit"},
                1
        ));

        // Chapter 3
        storyChapters.add(new StoryChapter(
                "Chapter 3: Social Structure",
                "Your father explains the social hierarchy: 'At the top are the datu and maharlika, then the timawa, and finally the alipin. Each has their role in maintaining balance in our society.' You notice how everyone works together during the harvest festival.",
                "Which social class in pre-colonial Philippines consisted of freemen and commoners?",
                new String[]{"Maharlika", "Timawa", "Alipin", "Datu"},
                1
        ));

        // Chapter 4
        storyChapters.add(new StoryChapter(
                "Chapter 4: Spiritual Beliefs",
                "The babaylan (shaman) performs a ritual to honor the ancestral spirits. You watch as she offers prayers to Bathala, the supreme god, and the anitos (spirits) of nature. 'Our connection to the spiritual world guides our daily lives,' she explains.",
                "What was the name of the supreme god in pre-colonial Philippine belief system?",
                new String[]{"Bathala", "Maykapal", "Kabunian", "Laon"},
                0
        ));

        // Chapter 5
        storyChapters.add(new StoryChapter(
                "Chapter 5: Maritime Culture",
                "You join the fishermen as they prepare their balangay (boats) for a trading expedition to neighboring islands. The boat captain shows you how to navigate using the stars and ocean currents. 'The sea is our highway,' he says proudly.",
                "What were the ancient boats used by early Filipinos for trade and travel called?",
                new String[]{"Karakoa", "Balangay", "Vinta", "Bangka"},
                1
        ));

        // Chapter 6
        storyChapters.add(new StoryChapter(
                "Chapter 6: Agricultural Practices",
                "During the planting season, you help the community in the kaingin (slash-and-burn farming) system. The elders teach you which crops to plant and when, following the cycles of the moon and seasons.",
                "What agricultural system involved clearing land by cutting and burning trees?",
                new String[]{"Irrigation farming", "Kaingin system", "Terrace farming", "Crop rotation"},
                1
        ));

        // Chapter 7
        storyChapters.add(new StoryChapter(
                "Chapter 7: Conflict Resolution",
                "A dispute arises between two families in the barangay. Your father, as datu, mediates the conflict using traditional laws and customs. 'Justice must be served, but harmony must be preserved,' he reminds everyone.",
                "How were disputes typically resolved in pre-colonial barangays?",
                new String[]{"Through trial by combat", "By the datu's mediation", "Through voting", "By elder council decision"},
                1
        ));

        // Chapter 8
        storyChapters.add(new StoryChapter(
                "Chapter 8: Cultural Legacy",
                "As you prepare to become the next datu, you reflect on everything you've learned about your people's rich culture, traditions, and governance systems that have sustained your society for generations.",
                "Which of these was NOT a characteristic of pre-colonial Philippine society?",
                new String[]{"Literate society with writing systems", "Complex social structure", "Foreign currency system", "Advanced maritime technology"},
                2
        ));

        Log.d(TAG, "Pre-colonial chapters initialized: " + storyChapters.size());
    }

    private void initializeSpanishColonialChapters() {
        Log.d(TAG, "Initializing Spanish Colonial chapters...");

        // Real Spanish colonial content
        storyChapters.add(new StoryChapter(
                "Chapter 1: Arrival of the Spanish",
                "You are Miguel, a mestizo scholar in Manila during the early 1600s. You witness the arrival of Spanish galleons and the establishment of Intramuros. The Spanish friars are beginning to convert the local population to Catholicism.",
                "Who was the first Spanish explorer to arrive in the Philippines in 1521?",
                new String[]{"Miguel López de Legazpi", "Ferdinand Magellan", "Juan Sebastián Elcano", "Ruy López de Villalobos"},
                1
        ));

        storyChapters.add(new StoryChapter(
                "Chapter 2: The Reduccion System",
                "You observe the Spanish implementing the reduccion system, forcing scattered communities to settle in organized towns under the church bells' reach. This makes it easier to collect taxes and convert people.",
                "What was the policy of resettling Filipinos into planned towns called?",
                new String[]{"Encomienda", "Reduccion", "Polo y servicio", "Tributo"},
                1
        ));

        // Add more real chapters...
        for (int i = 3; i <= 10; i++) {
            storyChapters.add(new StoryChapter(
                    "Chapter " + i + ": Spanish Colonial Period",
                    "This is chapter " + i + " content about Spanish colonial period. The 300-year rule brought significant changes to Philippine society, culture, and governance.",
                    "Sample question about Spanish colonial period " + i + "?",
                    new String[]{"Option A", "Option B", "Option C", "Option D"},
                    0
            ));
        }

        Log.d(TAG, "Spanish colonial chapters initialized: " + storyChapters.size());
    }

    private void initializeAmericanPeriodChapters() {
        for (int i = 1; i <= 9; i++) {
            storyChapters.add(new StoryChapter(
                    "Chapter " + i + ": American Colonial Period",
                    "This is chapter " + i + " of the American colonial journey. The American period brought public education and new political systems.",
                    "Sample question for chapter " + i + "?",
                    new String[]{"Option A", "Option B", "Option C", "Option D"},
                    0
            ));
        }
    }

    private void initializeJapaneseOccupationChapters() {
        for (int i = 1; i <= 7; i++) {
            storyChapters.add(new StoryChapter(
                    "Chapter " + i + ": Japanese Occupation",
                    "This is chapter " + i + " of the Japanese occupation journey. World War II brought hardship but also resistance.",
                    "Sample question for chapter " + i + "?",
                    new String[]{"Option A", "Option B", "Option C", "Option D"},
                    0
            ));
        }
    }

    private void initializeModernPhilippinesChapters() {
        for (int i = 1; i <= 11; i++) {
            storyChapters.add(new StoryChapter(
                    "Chapter " + i + ": Modern Philippines",
                    "This is chapter " + i + " of the modern Philippines journey. The post-war era brought independence and new challenges.",
                    "Sample question for chapter " + i + "?",
                    new String[]{"Option A", "Option B", "Option C", "Option D"},
                    0
            ));
        }
    }

    private void loadChapter(int chapterNumber) {
        Log.d(TAG, "Loading chapter " + chapterNumber + " of " + storyChapters.size());

        // Check if we have valid chapters
        if (storyChapters == null || storyChapters.isEmpty()) {
            Log.e(TAG, "No chapters available to load!");
            Toast.makeText(this, "Error: No story content available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Check if we've completed all chapters
        if (chapterNumber > storyChapters.size()) {
            Log.d(TAG, "All chapters completed! Total: " + storyChapters.size() + ", Current: " + chapterNumber);
            completeJourney();
            return;
        }

        // Validate chapter number
        if (chapterNumber < 1 || chapterNumber > storyChapters.size()) {
            Log.e(TAG, "Invalid chapter number: " + chapterNumber);
            Toast.makeText(this, "Error loading chapter", Toast.LENGTH_SHORT).show();
            return;
        }

        currentStoryChapter = storyChapters.get(chapterNumber - 1);
        Log.d(TAG, "Loaded chapter: " + currentStoryChapter.getTitle());

        // Update UI with story content
        if (textStoryContent != null) {
            textStoryContent.setText(currentStoryChapter.getStoryText());
            Log.d(TAG, "Story text set");
        }

        if (textQuestion != null) {
            textQuestion.setText(currentStoryChapter.getQuestion());
            Log.d(TAG, "Question set: " + currentStoryChapter.getQuestion());
        }

        // Clear previous answers and add new ones
        if (layoutAnswers != null) {
            layoutAnswers.removeAllViews();
            Log.d(TAG, "Cleared previous answers");

            String[] answers = currentStoryChapter.getAnswers();
            Log.d(TAG, "Adding " + answers.length + " answer buttons");

            for (int i = 0; i < answers.length; i++) {
                Button answerButton = new Button(this);
                answerButton.setText(answers[i]);
                answerButton.setTag(i);
                answerButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.color_default));
                answerButton.setTextColor(ContextCompat.getColor(this, R.color.white));
                answerButton.setTextSize(16);
                answerButton.setPadding(32, 16, 32, 16);
                answerButton.setAllCaps(false);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 8, 0, 8);
                answerButton.setLayoutParams(params);

                final int answerIndex = i;
                answerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswer(answerIndex);
                    }
                });

                layoutAnswers.addView(answerButton);
                Log.d(TAG, "Added answer button: " + answers[i]);
            }
        }

        // Update progress and hide next button
        updateProgress();
        if (btnNext != null) {
            btnNext.setVisibility(View.GONE);
        }

        Log.d(TAG, "Chapter " + chapterNumber + " loaded successfully");
    }

    private void checkAnswer(int selectedAnswerIndex) {
        Log.d(TAG, "Checking answer: " + selectedAnswerIndex + ", Correct: " + currentStoryChapter.getCorrectAnswer());

        if (layoutAnswers == null) return;

        // Disable all answer buttons
        for (int i = 0; i < layoutAnswers.getChildCount(); i++) {
            View child = layoutAnswers.getChildAt(i);
            child.setEnabled(false);

            if (child instanceof Button) {
                Button answerButton = (Button) child;
                int answerIndex = (Integer) answerButton.getTag();

                if (answerIndex == currentStoryChapter.getCorrectAnswer()) {
                    // Highlight correct answer in green
                    answerButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.correct_green));
                } else if (answerIndex == selectedAnswerIndex) {
                    // Highlight wrong answer in red
                    answerButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.incorrect_red));
                }
            }
        }

        // Check if answer is correct
        if (selectedAnswerIndex == currentStoryChapter.getCorrectAnswer()) {
            score += 10;
            playCorrectSound();
            Toast.makeText(this, "Correct! +10 points", Toast.LENGTH_SHORT).show();
        } else {
            playIncorrectSound();
            String correctAnswer = currentStoryChapter.getAnswers()[currentStoryChapter.getCorrectAnswer()];
            Toast.makeText(this, "Incorrect! The right answer was: " + correctAnswer, Toast.LENGTH_LONG).show();
        }

        // Show next button after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (btnNext != null) {
                    btnNext.setVisibility(View.VISIBLE);
                }
            }
        }, 1500);
    }

    private void nextChapter() {
        Log.d(TAG, "Moving to next chapter. Current: " + currentChapter + ", Total: " + totalChapters);
        currentChapter++;
        if (currentChapter <= totalChapters) {
            loadChapter(currentChapter);
        } else {
            completeJourney();
        }
    }

    private void completeJourney() {
        Log.d(TAG, "Completing journey " + currentJourney + " with score: " + score);

        // Save progress using ProgressManager
        ProgressManager progressManager = new ProgressManager(this);
        progressManager.completeJourney(currentJourney);
        progressManager.saveStoryProgress(currentJourney, totalChapters);

        // Show completion message
        Toast.makeText(this, "Journey " + currentJourney + " completed! Final Score: " + score, Toast.LENGTH_LONG).show();

        // Return to StoryModeActivity
        Intent intent = new Intent(this, StoryModeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void updateProgress() {
        if (textChapterProgress != null) {
            textChapterProgress.setText(String.format("Chapter %d/%d", currentChapter, totalChapters));
        }
        if (progressChapter != null) {
            int progress = (currentChapter * 100) / totalChapters;
            progressChapter.setProgress(progress);
        }
    }

    private void setupButtonListeners() {
        if (btnNext != null) {
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Next button clicked");
                    nextChapter();
                }
            });
        }

        // Back button
        View backButton = findViewById(R.id.btn_back);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playButtonClickSound();
                    finish();
                }
            });
        }
    }

    private void initializeBackgroundMusic() {
        try {
            backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
            if (backgroundMusic != null) {
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(0.3f, 0.3f);

                if (SettingsActivity.isBackgroundMusicEnabled(preferences)) {
                    backgroundMusic.start();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing background music: " + e.getMessage());
        }
    }

    private void playCorrectSound() {
        if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
            try {
                MediaPlayer.create(this, R.raw.button_click).start();
            } catch (Exception e) {
                Log.e(TAG, "Error playing correct sound: " + e.getMessage());
            }
        }
    }

    private void playIncorrectSound() {
        if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
            try {
                MediaPlayer.create(this, R.raw.button_click).start();
            } catch (Exception e) {
                Log.e(TAG, "Error playing incorrect sound: " + e.getMessage());
            }
        }
    }

    private void playButtonClickSound() {
        if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
            try {
                MediaPlayer.create(this, R.raw.button_click).start();
            } catch (Exception e) {
                Log.e(TAG, "Error playing button click sound: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backgroundMusic != null && !backgroundMusic.isPlaying() &&
                SettingsActivity.isBackgroundMusicEnabled(preferences)) {
            backgroundMusic.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }

    // Story Chapter data class
    private static class StoryChapter {
        private String title;
        private String storyText;
        private String question;
        private String[] answers;
        private int correctAnswer;

        public StoryChapter(String title, String storyText, String question, String[] answers, int correctAnswer) {
            this.title = title;
            this.storyText = storyText;
            this.question = question;
            this.answers = answers;
            this.correctAnswer = correctAnswer;
        }

        public String getTitle() { return title; }
        public String getStoryText() { return storyText; }
        public String getQuestion() { return question; }
        public String[] getAnswers() { return answers; }
        public int getCorrectAnswer() { return correctAnswer; }
    }
}