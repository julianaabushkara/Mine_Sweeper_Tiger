package minesweeper.model;

/**
 * Enumeration representing the difficulty levels for questions.
 * Maps to integer values in the CSV file:
 * 1 = EASY, 2 = MEDIUM, 3 = HARD
 */
public enum QuestionDifficulty {
    EASY(1),
    MEDIUM(2),
    HARD(3);

    private final int value;

    QuestionDifficulty(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts an integer value from CSV to the corresponding Difficulty enum.
     *
     * @param value The integer value (1, 2, or 3)
     * @return The corresponding Difficulty enum
     * @throws IllegalArgumentException if the value is not 1, 2, or 3
     */
    public static QuestionDifficulty fromValue(int value) {
        for (QuestionDifficulty d : values()) {
            if (d.value == value) {
                return d;
            }
        }
        throw new IllegalArgumentException("Invalid difficulty value: " + value + ". Expected 1 (EASY), 2 (MEDIUM), or 3 (HARD).");
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
