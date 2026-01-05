package minesweeper.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable version of Question for editing purposes.
 * Provides validation and conversion to/from immutable Question objects.
 */
public class MutableQuestion {

    private int id;
    private String text;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private char correctOption;
    private QuestionDifficulty difficulty;

    /**
     * Creates an empty MutableQuestion with default values.
     */
    public MutableQuestion() {
        this.id = 0;
        this.text = "";
        this.optionA = "";
        this.optionB = "";
        this.optionC = "";
        this.optionD = "";
        this.correctOption = 'A';
        this.difficulty = QuestionDifficulty.EASY;
    }

    /**
     * Creates a MutableQuestion from an existing Question (for editing).
     *
     * @param question The question to copy data from
     */
    public MutableQuestion(Question question) {
        this.id = question.getId();
        this.text = question.getText();
        this.optionA = question.getOptionA();
        this.optionB = question.getOptionB();
        this.optionC = question.getOptionC();
        this.optionD = question.getOptionD();
        this.correctOption = question.getCorrectOption();
        this.difficulty = question.getDifficulty();
    }

    /**
     * Converts this MutableQuestion to an immutable Question.
     *
     * @return A new Question with the same data
     * @throws IllegalStateException if the question data is invalid
     */
    public Question toQuestion() {
        if (!isValid()) {
            throw new IllegalStateException("Cannot convert invalid question: " +
                    String.join(", ", getValidationErrors()));
        }
        return new Question(id, text, optionA, optionB, optionC, optionD,
                correctOption, difficulty);
    }

    /**
     * Validates the question data.
     *
     * @return true if all fields are valid
     */
    public boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    /**
     * Gets a list of validation error messages.
     *
     * @return List of error messages (empty if valid)
     */
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        if (id <= 0) {
            errors.add("ID must be a positive number");
        }

        if (text == null || text.trim().isEmpty()) {
            errors.add("Question text cannot be empty");
        }

        if (optionA == null || optionA.trim().isEmpty()) {
            errors.add("Option A cannot be empty");
        }

        if (optionB == null || optionB.trim().isEmpty()) {
            errors.add("Option B cannot be empty");
        }

        if (optionC == null || optionC.trim().isEmpty()) {
            errors.add("Option C cannot be empty");
        }

        if (optionD == null || optionD.trim().isEmpty()) {
            errors.add("Option D cannot be empty");
        }

        if (correctOption != 'A' && correctOption != 'B' &&
            correctOption != 'C' && correctOption != 'D') {
            errors.add("Correct answer must be A, B, C, or D");
        }

        if (difficulty == null) {
            errors.add("Difficulty must be set");
        }

        return errors;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public char getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(char correctOption) {
        this.correctOption = Character.toUpperCase(correctOption);
    }

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return String.format("MutableQuestion[id=%d, text='%s', valid=%b]",
                id, text, isValid());
    }
}
