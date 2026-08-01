package com.example.kwiztorya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameplayActivity extends AppCompatActivity {

    private TextView textViewQuestion;
    private TextView textViewProgress;
    private TextView textViewScore;
    private TextView textViewQuestionNumber;
    private Button buttonA, buttonB, buttonC, buttonD;
    private Button btnNext;
    private CardView cardA, cardB, cardC, cardD;
    private ImageButton backButton;
    private List<Question> questionsList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private ColorStateList defaultButtonColor;
    private boolean answerSubmitted = false;
    private String chapterId;
    private int eraIndex;
    private SharedPreferences preferences;
    private MediaPlayer backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        // Get chapter ID and era index from the intent
        chapterId = getIntent().getStringExtra("CHAPTER_ID");
        eraIndex = getIntent().getIntExtra("ERA_INDEX", 0);

        // Add logging to debug
        Log.d("GameplayActivity", "Chapter ID: " + chapterId);
        Log.d("GameplayActivity", "Era Index: " + eraIndex);

        if (chapterId == null) {
            chapterId = "Default Chapter";
            Log.d("GameplayActivity", "Chapter ID was null, using default");
        }

        // Initialize shared preferences (no database helper)
        preferences = getSharedPreferences("KwiztoryaPrefs", MODE_PRIVATE);

        initializeViews();
        defaultButtonColor = buttonA.getBackgroundTintList();

        // Setup the question list
        initializeQuestions();
        Collections.shuffle(questionsList);

        loadQuestion();

        // Initialize background music
        initializeBackgroundMusic();
    }

    private void initializeViews() {
        textViewQuestion = findViewById(R.id.text_view_question);
        textViewProgress = findViewById(R.id.text_view_progress);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionNumber = findViewById(R.id.text_view_question_number);
        buttonA = findViewById(R.id.btn_answer_a);
        buttonB = findViewById(R.id.btn_answer_b);
        buttonC = findViewById(R.id.btn_answer_c);
        buttonD = findViewById(R.id.btn_answer_d);
        btnNext = findViewById(R.id.btn_next);
        cardA = findViewById(R.id.card_answer_a);
        cardB = findViewById(R.id.card_answer_b);
        cardC = findViewById(R.id.card_answer_c);
        cardD = findViewById(R.id.card_answer_d);
        backButton = findViewById(R.id.btn_back);

        setupBackButton();
        updateScoreDisplay();

        // Set up Next button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerSubmitted) {
                    currentQuestionIndex++;
                    loadQuestion();
                }
            }
        });
    }

    private void setupBackButton() {
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                playButtonClickSound();
                showExitConfirmation();
            });
        }
    }

    private void showExitConfirmation() {
        Toast.makeText(this, "Quiz exited", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateScoreDisplay() {
        if (textViewScore != null) {
            textViewScore.setText(String.valueOf(score));
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
            }
        } catch (Exception e) {
            Log.e("Gameplay", "Error initializing background music: " + e.getMessage());
        }
    }

    private void startBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            try {
                backgroundMusic.start();
            } catch (Exception e) {
                Log.e("Gameplay", "Error starting background music: " + e.getMessage());
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
                Log.e("Gameplay", "Error playing button click sound: " + e.getMessage());
            }
        }
    }

    private void initializeQuestions() {
        questionsList = new ArrayList<>();

        // Load questions based on the selected chapter
        switch (chapterId) {
            case "pre_colonial":
                Log.d("GameplayActivity", "Loading Pre-Colonial questions");
                loadPreColonialQuestions();
                break;
            case "spanish_colonial":
                Log.d("GameplayActivity", "Loading Spanish Colonial questions");
                loadSpanishColonialQuestions();
                break;
            case "american_period":
                Log.d("GameplayActivity", "Loading American Period questions");
                loadAmericanPeriodQuestions();
                break;
            case "japanese_occupation":
                Log.d("GameplayActivity", "Loading Japanese Occupation questions");
                loadJapaneseOccupationQuestions();
                break;
            case "modern_era":
                Log.d("GameplayActivity", "Loading Modern Era questions");
                loadModernEraQuestions();
                break;
            default:
                Log.d("GameplayActivity", "Loading Default questions");
                loadDefaultQuestions();
                break;
        }

        Log.d("GameplayActivity", "Loaded " + questionsList.size() + " questions for chapter: " + chapterId);
    }

    private void loadPreColonialQuestions() {
        questionsList.add(new Question("What was the primary form of government in pre-colonial Philippines?",
                "Barangay", "Kingdom", "Empire", "Tribal Council", 1));
        questionsList.add(new Question("What was the writing system used in pre-colonial Philippines?",
                "Baybayin", "Hanunoo", "Kawi", "Sanskrit", 1));
        questionsList.add(new Question("What was the main economic activity in pre-colonial Philippines?",
                "Agriculture", "Fishing", "Trading", "Mining", 3));
        questionsList.add(new Question("Who were the datu's advisors in the barangay?",
                "Elders", "Warriors", "Priests", "Merchants", 1));
        questionsList.add(new Question("What was the practice of sharing resources in the community called?",
                "Bayanihan", "Pagdadamayan", "Saranay", "Tulungan", 1));
        questionsList.add(new Question("Which pre-colonial social class consisted of freemen and commoners?",
                "Timawa", "Maharlika", "Alipin", "Datu", 1));
    }

    private void loadSpanishColonialQuestions() {
        questionsList.add(new Question("When did Miguel López de Legazpi establish Spanish settlement in the Philippines?",
                "1565", "1521", "1542", "1571", 1));
        questionsList.add(new Question("What was the Spanish policy that required Filipinos to render forced labor?",
                "Polo y Servicio", "Encomienda", "Tributo", "Galleon Trade", 1));
        questionsList.add(new Question("What was the main product of the Manila-Acapulco Galleon Trade?",
                "Silk", "Spices", "Silver", "Porcelain", 2));
        questionsList.add(new Question("Who led the first recorded revolt against Spanish rule in 1574?",
                "Lakandula", "Rajah Sulayman", "Dagohoy", "Tamblot", 1));
        questionsList.add(new Question("What was the Spanish religious mission to convert Filipinos called?",
                "Reduccion", "Mision", "Conversion", "Evangelization", 1));
        questionsList.add(new Question("Which Spanish governor-general established the tobacco monopoly?",
                "José Basco y Vargas", "Miguel López de Legazpi", "Santiago de Vera", "Guido de Lavezaris", 1));
    }

    private void loadAmericanPeriodQuestions() {
        questionsList.add(new Question("When did the American colonial period officially begin in the Philippines?",
                "1898", "1899", "1901", "1902", 1));
        questionsList.add(new Question("What was the established public education system during American period?",
                "Thomasites", "Peace Corps", "Missionaries", "Volunteers", 1));
        questionsList.add(new Question("What was the first political party established during American rule?",
                "Federalista Party", "Nacionalista Party", "Liberal Party", "Katipunan", 1));
        questionsList.add(new Question("What law promised Philippine independence after a 10-year transition?",
                "Tydings-McDuffie Act", "Jones Law", "Philippine Organic Act", "Bell Trade Act", 1));
        questionsList.add(new Question("Who was the first Filipino chief justice under American rule?",
                "Cayetano Arellano", "Jose Abad Santos", "Ramon Avanceña", "Manuel Roxas", 1));
        questionsList.add(new Question("What was the first university established during American period?",
                "University of the Philippines", "Ateneo de Manila", "University of Santo Tomas", "De La Salle University", 1));
    }

    private void loadJapaneseOccupationQuestions() {
        questionsList.add(new Question("When did the Japanese forces invade the Philippines?",
                "December 8, 1941", "December 7, 1941", "January 2, 1942", "March 15, 1942", 1));
        questionsList.add(new Question("What was the Filipino guerrilla force that resisted Japanese occupation?",
                "Hukbalahap", "USAFFE", "Philippine Constabulary", "Makapili", 1));
        questionsList.add(new Question("Where did the Japanese forces surrender in the Philippines?",
                "Baguio", "Manila", "Corregidor", "Tokyo", 1));
        questionsList.add(new Question("What was the Japanese-sponsored republic established in 1943?",
                "Second Philippine Republic", "Puppet Republic", "Japanese Philippines", "New Philippines", 1));
        questionsList.add(new Question("Who was the president of the Japanese-sponsored republic?",
                "Jose P. Laurel", "Manuel L. Quezon", "Sergio Osmeña", "Manuel Roxas", 1));
        questionsList.add(new Question("What was the death march that occurred in 1942?",
                "Bataan Death March", "Corregidor Death March", "Manila Death March", "Capas Death March", 1));
    }

    private void loadModernEraQuestions() {
        questionsList.add(new Question("When was the Third Philippine Republic established?",
                "1946", "1935", "1973", "1987", 1));
        questionsList.add(new Question("Who was the first president of the Third Republic?",
                "Manuel Roxas", "Elpidio Quirino", "Ramon Magsaysay", "Manuel Quezon", 1));
        questionsList.add(new Question("What year was martial law declared by Ferdinand Marcos?",
                "1972", "1965", "1978", "1981", 1));
        questionsList.add(new Question("When did the People Power Revolution occur?",
                "1986", "1972", "1983", "1991", 1));
        questionsList.add(new Question("Who became president after the People Power Revolution?",
                "Corazon Aquino", "Fidel Ramos", "Joseph Estrada", "Gloria Macapagal Arroyo", 1));
        questionsList.add(new Question("Which president implemented the Comprehensive Agrarian Reform Program?",
                "Corazon Aquino", "Fidel Ramos", "Joseph Estrada", "Gloria Macapagal Arroyo", 1));
    }

    private void loadDefaultQuestions() {
        questionsList.add(new Question("What is the largest city by population in the world?",
                "San Francisco", "Tokyo", "Berlin", "Lisbon", 2));
        questionsList.add(new Question("What year did the Philippines declare its independence from Spain?",
                "1898", "1896", "1946", "1899", 1));
        questionsList.add(new Question("The Aztec capital, Tenochtitlan, is now which modern city?",
                "Mexico City", "Lima", "Bogota", "Guatemala City", 1));
        questionsList.add(new Question("Who wrote the epic poem 'The Iliad'?",
                "Homer", "Virgil", "Socrates", "Plato", 1));
        questionsList.add(new Question("What major event began on July 28, 1914?",
                "World War I", "World War II", "The Cold War", "The Great Depression", 1));
        questionsList.add(new Question("The Byzantine Empire was the continuation of which former empire?",
                "Western Roman Empire", "Persian Empire", "Holy Roman Empire", "Mongol Empire", 1));
    }

    private void loadQuestion() {
        if (currentQuestionIndex < questionsList.size()) {
            answerSubmitted = false;
            resetButtons();

            Question currentQuestion = questionsList.get(currentQuestionIndex);

            // Update progress display
            textViewProgress.setText(String.format("Question %d of %d", currentQuestionIndex + 1, questionsList.size()));
            textViewQuestionNumber.setText(String.valueOf(currentQuestionIndex + 1));
            textViewQuestion.setText(currentQuestion.getQuestionText());

            List<String> options = new ArrayList<>();
            options.add(currentQuestion.getOptionA());
            options.add(currentQuestion.getOptionB());
            options.add(currentQuestion.getOptionC());
            options.add(currentQuestion.getOptionD());

            Collections.shuffle(options);

            buttonA.setText(options.get(0));
            buttonB.setText(options.get(1));
            buttonC.setText(options.get(2));
            buttonD.setText(options.get(3));

            // Hide Next button until answer is selected
            btnNext.setVisibility(View.GONE);

        } else {
            finishQuiz();
        }
    }

    private void resetButtons() {
        // Reset card backgrounds
        cardA.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_beige));
        cardB.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_beige));
        cardC.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_beige));
        cardD.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_beige));

        // Reset button text colors
        buttonA.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
        buttonB.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
        buttonC.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
        buttonD.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));

        // Make buttons clickable again
        buttonA.setClickable(true);
        buttonB.setClickable(true);
        buttonC.setClickable(true);
        buttonD.setClickable(true);
    }

    public void checkAnswer(View view) {
        if (answerSubmitted) {
            return;
        }
        answerSubmitted = true;

        playSoundEffect(R.raw.button_click);

        Button clickedButton = (Button) view;
        String selectedAnswer = clickedButton.getText().toString();

        Question currentQuestion = questionsList.get(currentQuestionIndex);
        String correctAnswerText = currentQuestion.getCorrectAnswer();

        int correctColor = ContextCompat.getColor(this, R.color.correct_green);
        int incorrectColor = ContextCompat.getColor(this, R.color.incorrect_red);

        CardView clickedCard = getCardForButton(clickedButton);

        if (selectedAnswer.equals(correctAnswerText)) {
            score++;
            updateScoreDisplay();
            clickedCard.setCardBackgroundColor(correctColor);
            clickedButton.setTextColor(Color.WHITE);
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            playSoundEffect(R.raw.correct_answer);
        } else {
            clickedCard.setCardBackgroundColor(incorrectColor);
            clickedButton.setTextColor(Color.WHITE);
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
            playSoundEffect(R.raw.wrong_answer);
            highlightCorrectAnswer(correctAnswerText, correctColor);
        }

        // Disable all buttons
        buttonA.setClickable(false);
        buttonB.setClickable(false);
        buttonC.setClickable(false);
        buttonD.setClickable(false);

        // Show Next button
        btnNext.setVisibility(View.VISIBLE);
    }

    private CardView getCardForButton(Button button) {
        if (button.getId() == R.id.btn_answer_a) return cardA;
        if (button.getId() == R.id.btn_answer_b) return cardB;
        if (button.getId() == R.id.btn_answer_c) return cardC;
        if (button.getId() == R.id.btn_answer_d) return cardD;
        return cardA;
    }

    private void highlightCorrectAnswer(String correctAnswerText, int correctColor) {
        Button[] buttons = {buttonA, buttonB, buttonC, buttonD};
        CardView[] cards = {cardA, cardB, cardC, cardD};

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getText().toString().equals(correctAnswerText)) {
                cards[i].setCardBackgroundColor(correctColor);
                buttons[i].setTextColor(Color.WHITE);
                break;
            }
        }
    }

    private void playSoundEffect(int soundResource) {
        if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
            try {
                MediaPlayer.create(this, soundResource).start();
            } catch (Exception e) {
                Log.e("Gameplay", "Error playing sound effect: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBackgroundMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SettingsActivity.isBackgroundMusicEnabled(preferences)) {
            startBackgroundMusic();
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

    private void finishQuiz() {
        if (SettingsActivity.areSoundEffectsEnabled(preferences)) {
            try {
                MediaPlayer.create(this, R.raw.button_click).start();
            } catch (Exception e) {
                Log.e("Gameplay", "Error playing completion sound");
            }
        }

        stopBackgroundMusic();

        // Save era progress using ProgressManager only
        boolean passed = score >= questionsList.size() / 2;

        ProgressManager progressManager = new ProgressManager(this);
        progressManager.saveEraProgress(eraIndex, passed, score);

        Intent intent = new Intent(GameplayActivity.this, ResultsActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questionsList.size());
        intent.putExtra("ERA_INDEX", eraIndex);
        intent.putExtra("PASSED", passed);
        intent.putExtra("CHAPTER_ID", chapterId);
        startActivity(intent);
        finish();
    }
}