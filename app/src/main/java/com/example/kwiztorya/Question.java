package com.example.kwiztorya;

public class Question {
    private String questionText;
    private String optionA, optionB, optionC, optionD;
    private int correctAnswerIndex;

    public Question(String questionText, String optionA, String optionB, String optionC, String optionD, int correctAnswerIndex) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() { return questionText; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }

    public String getCorrectAnswer() {
        switch (correctAnswerIndex) {
            case 1: return optionA;
            case 2: return optionB;
            case 3: return optionC;
            case 4: return optionD;
            default: return "";
        }
    }
}