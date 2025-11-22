package minesweeper.model;

/**
 * Represents a single quiz question with multiple choice options.
 * This class is immutable to ensure data integrity after loading from CSV.
 *
 * Fields match the CSV schema:
 * ID, Question, Difficulty, A, B, C, D, Correct Answer
 */
public class Question {

    private final int id;
    private final String text;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final char correctOption;  // 'A', 'B', 'C', or 'D'
    private final Difficulty difficulty;

    /**
     * Constructs a new Question with all required fields.
     *
     * @param id            Unique identifier for the question
     * @param text          The question text
     * @param optionA       Answer option A
     * @param optionB       Answer option B
     * @param optionC       Answer option C
     * @param optionD       Answer option D
     * @param correctOption The correct answer letter ('A', 'B', 'C', or 'D')
     * @param difficulty    The difficulty level
     */
    public Question(int id, String text, String optionA, String optionB,
                    String optionC, String optionD, char correctOption,
                    Difficulty difficulty) {
        this.id = id;
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = Character.toUpperCase(correctOption);
        this.difficulty = difficulty;
    }

    // Getters (no setters - immutable class)

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public char getCorrectOption() {
        return correctOption;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the text of the correct answer option.
     *
     * @return The correct answer text
     */
    public String getCorrectAnswerText() {
        switch (correctOption) {
            case 'A': return optionA;
            case 'B': return optionB;
            case 'C': return optionC;
            case 'D': return optionD;
            default: return "";
        }
    }

    /**
     * Returns a formatted display text for the question.
     * As specified in the UML diagram's getDisplayText() method.
     *
     * @return Formatted question text with all options
     */
    public String getDisplayText() {
        return String.format("%s\n  A) %s\n  B) %s\n  C) %s\n  D) %s",
                text, optionA, optionB, optionC, optionD);
    }

    /**
     * Checks if a given answer is correct.
     *
     * @param answer The answer to check ('A', 'B', 'C', or 'D')
     * @return true if the answer matches the correct option
     */
    public boolean isCorrectAnswer(char answer) {
        return Character.toUpperCase(answer) == correctOption;
    }

    @Override
    public String toString() {
        return String.format("Question[id=%d, text='%s', correct=%c, difficulty=%s]",
                id, text, correctOption, difficulty);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Question question = (Question) obj;
        return id == question.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
