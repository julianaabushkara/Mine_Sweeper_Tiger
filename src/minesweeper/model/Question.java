package minesweeper.model;


import java.util.Random;

public class Question {
    // Nested enum for question difficulties
    public enum QuestionDifficulty {
        EASY, MEDIUM, HARD, EXPERT
    }
    
    private String questionText;
    private String[] answers;
    private int correctAnswerIndex;
    private QuestionDifficulty difficulty;
    private String category;
    
    // Constructor for custom questions
    public Question(String questionText, String[] answers, int correctAnswerIndex, 
                   QuestionDifficulty difficulty, String category) {
        this.questionText = questionText;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.difficulty = difficulty;
        this.category = category;
    }
    
    // Default constructor with sample question
    public Question() {
        this.questionText = "What is 2 + 2?";
        this.answers = new String[]{"4", "3", "5", "6"};
        this.correctAnswerIndex = 0;
        this.difficulty = QuestionDifficulty.EASY;
        this.category = "Mathematics";
    }
    
    // Static method to get random question difficulty
    public static QuestionDifficulty getRandomDifficulty() {
        Random rand = new Random();
        QuestionDifficulty[] difficulties = QuestionDifficulty.values();
        return difficulties[rand.nextInt(difficulties.length)];
    }
    
    // Check if answer is correct
    public boolean isCorrectAnswer(int answerIndex) {
        return answerIndex == correctAnswerIndex;
    }
    
    // Getters and Setters
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    
    public String[] getAnswers() { return answers; }
    public void setAnswers(String[] answers) { this.answers = answers; }
    
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public void setCorrectAnswerIndex(int correctAnswerIndex) { 
        this.correctAnswerIndex = correctAnswerIndex; 
    }
    
    public QuestionDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(QuestionDifficulty difficulty) { this.difficulty = difficulty; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}