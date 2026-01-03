package minesweeper.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Manages the collection of questions loaded from a CSV file.
 * Provides robust CSV parsing with comprehensive error handling.
 *
 * CSV Format Expected:
 * ID,Question,Difficulty,A,B,C,D,Correct Answer
 *
 * Example:
 * 1,What color is the sky?,1,Blue,Green,Red,Yellow,A
 */
public class QuestionBank {

    // Expected CSV header columns
    private static final String[] EXPECTED_HEADERS = {
            "ID", "Question", "Difficulty", "A", "B", "C", "D", "Correct Answer"
    };
    private static final int EXPECTED_COLUMN_COUNT = 8;

    private List<Question> questions;
    private String csvPath;
    private List<String> parseErrors;  // Tracks errors for reporting

    /**
     * Creates a QuestionBank and loads questions from the specified CSV file.
     *
     * @param csvPath Path to the CSV file (classpath resource path, e.g., "/Questions/Questions.csv")
     */
    public QuestionBank(String csvPath) {
        this.csvPath = csvPath;
        this.questions = new ArrayList<>();
        this.parseErrors = new ArrayList<>();
    }

    /**
     * Creates an empty QuestionBank (for testing or manual loading).
     */
    public QuestionBank() {
        this.csvPath = null;
        this.questions = new ArrayList<>();
        this.parseErrors = new ArrayList<>();
    }

    /**
     * Loads questions from the CSV file specified in the constructor.
     * This method includes comprehensive error handling for:
     * - File not found
     * - Empty files
     * - Invalid headers
     * - Malformed rows
     * - Invalid data types
     *
     * @throws CSVParseException if the file cannot be parsed or contains critical errors
     */
    public void loadFromCsv() throws CSVParseException {
        if (csvPath == null || csvPath.isEmpty()) {
            throw new CSVParseException("No CSV file path specified.");
        }
        loadFromCsv(csvPath);
    }

    /**
     * Loads questions from a specified CSV file path (classpath resource).
     *
     * @param filePath Path to the CSV file in classpath (e.g., "/Questions/Questions.csv")
     * @throws CSVParseException if the file cannot be parsed
     */
    public void loadFromCsv(String filePath) throws CSVParseException {
        this.csvPath = filePath;
        this.questions.clear();
        this.parseErrors.clear();

        // Load from classpath
        InputStream inputStream = getClass().getResourceAsStream(filePath);

        if (inputStream == null) {
            throw new CSVParseException("File not found in classpath: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();

            // Handle BOM (Byte Order Mark) if present
            if (headerLine != null && headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }

            // Check for empty file
            if (headerLine == null || headerLine.trim().isEmpty()) {
                throw new CSVParseException("CSV file is empty.");
            }

            // Validate headers
            validateHeaders(headerLine);

            // Parse data rows
            String line;
            int lineNumber = 1; // Start after header
            int successCount = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    Question question = parseLine(line, lineNumber);
                    if (question != null) {
                        questions.add(question);
                        successCount++;
                    }
                } catch (RowParseException e) {
                    // Record the error but continue parsing other rows
                    parseErrors.add(e.getMessage());
                }
            }

            // If no valid questions were loaded, that's an error
            if (questions.isEmpty() && lineNumber > 1) {
                throw new CSVParseException("No valid questions found in CSV. All rows had errors:\n" +
                        String.join("\n", parseErrors));
            }

        } catch (IOException e) {
            throw new CSVParseException("Error reading CSV file: " + e.getMessage());
        }
    }

    /**
     * Loads questions from an InputStream (for custom user uploads when GUI with 'select CSV file' is implemented)
     *
     * @param inputStream The input stream containing CSV data
     * @throws CSVParseException if the stream cannot be parsed
     */
    public void loadFromInputStream(InputStream inputStream) throws CSVParseException {
        this.questions.clear();
        this.parseErrors.clear();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();

            // Handle BOM
            if (headerLine != null && headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }

            if (headerLine == null || headerLine.trim().isEmpty()) {
                throw new CSVParseException("CSV data is empty.");
            }

            validateHeaders(headerLine);

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                try {
                    Question question = parseLine(line, lineNumber);
                    if (question != null) {
                        questions.add(question);
                    }
                } catch (RowParseException e) {
                    parseErrors.add(e.getMessage());
                }
            }

            if (questions.isEmpty() && lineNumber > 1) {
                throw new CSVParseException("No valid questions found in CSV data.");
            }

        } catch (IOException e) {
            throw new CSVParseException("Error reading CSV data: " + e.getMessage());
        }
    }

    /**
     * Validates that the CSV header row contains the expected columns.
     *
     * @param headerLine The first line of the CSV file
     * @throws CSVParseException if headers don't match expected format
     */
    private void validateHeaders(String headerLine) throws CSVParseException {
        String[] headers = parseCSVLine(headerLine);

        if (headers.length != EXPECTED_COLUMN_COUNT) {
            throw new CSVParseException(
                    String.format("Invalid header count. Expected %d columns, found %d. " +
                                    "Expected columns: %s",
                            EXPECTED_COLUMN_COUNT, headers.length,
                            String.join(", ", EXPECTED_HEADERS)));
        }

        // Check each header (case-insensitive)
        for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
            String expected = EXPECTED_HEADERS[i].trim().toLowerCase();
            String actual = headers[i].trim().toLowerCase();

            if (!expected.equals(actual)) {
                throw new CSVParseException(
                        String.format("Invalid header at column %d. Expected '%s', found '%s'.",
                                i + 1, EXPECTED_HEADERS[i], headers[i]));
            }
        }
    }

    /**
     * Parses a single CSV data line into a Question object.
     *
     * @param line The CSV line to parse
     * @param lineNumber The line number (for error reporting)
     * @return The parsed Question object
     * @throws RowParseException if the row contains invalid data
     */
    private Question parseLine(String line, int lineNumber) throws RowParseException {
        String[] fields = parseCSVLine(line);

        // Validate field count
        if (fields.length != EXPECTED_COLUMN_COUNT) {
            throw new RowParseException(
                    String.format("Row %d: Expected %d columns, found %d.",
                            lineNumber, EXPECTED_COLUMN_COUNT, fields.length));
        }

        try {
            // Parse ID (must be a positive integer)
            int id = parseId(fields[0].trim(), lineNumber);

            // Parse Question text (cannot be empty)
            String questionText = parseRequiredField(fields[1].trim(), "Question", lineNumber);

            // Parse Difficulty (must be 1, 2, or 3)
            QuestionDifficulty difficulty = parseDifficulty(fields[2].trim(), lineNumber);

            // Parse options (cannot be empty)
            String optionA = parseRequiredField(fields[3].trim(), "Option A", lineNumber);
            String optionB = parseRequiredField(fields[4].trim(), "Option B", lineNumber);
            String optionC = parseRequiredField(fields[5].trim(), "Option C", lineNumber);
            String optionD = parseRequiredField(fields[6].trim(), "Option D", lineNumber);

            // Parse correct answer (must be A, B, C, or D)
            char correctAnswer = parseCorrectAnswer(fields[7].trim(), lineNumber);

            return new Question(id, questionText, optionA, optionB, optionC, optionD,
                    correctAnswer, difficulty);

        } catch (RowParseException e) {
            throw e; // Re-throw with original message
        } catch (Exception e) {
            throw new RowParseException(
                    String.format("Row %d: Unexpected error - %s", lineNumber, e.getMessage()));
        }
    }

    /**
     * Parses a CSV line, handling quoted fields correctly.
     * Supports fields with commas inside quotes.
     */
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString()); // Add last field

        return fields.toArray(new String[0]);
    }

    private int parseId(String value, int lineNumber) throws RowParseException {
        try {
            int id = Integer.parseInt(value);
            if (id <= 0) {
                throw new RowParseException(
                        String.format("Row %d: ID must be a positive integer, found '%s'.",
                                lineNumber, value));
            }
            return id;
        } catch (NumberFormatException e) {
            throw new RowParseException(
                    String.format("Row %d: Invalid ID format '%s'. Expected a positive integer.",
                            lineNumber, value));
        }
    }

    private String parseRequiredField(String value, String fieldName, int lineNumber)
            throws RowParseException {
        if (value == null || value.isEmpty()) {
            throw new RowParseException(
                    String.format("Row %d: %s cannot be empty.", lineNumber, fieldName));
        }
        return value;
    }

    private QuestionDifficulty parseDifficulty(String value, int lineNumber) throws RowParseException {
        try {
            int diffValue = Integer.parseInt(value);
            return QuestionDifficulty.fromValue(diffValue);
        } catch (NumberFormatException e) {
            throw new RowParseException(
                    String.format("Row %d: Invalid difficulty format '%s'. Expected 1, 2, or 3.",
                            lineNumber, value));
        } catch (IllegalArgumentException e) {
            throw new RowParseException(
                    String.format("Row %d: %s", lineNumber, e.getMessage()));
        }
    }

    private char parseCorrectAnswer(String value, int lineNumber) throws RowParseException {
        if (value == null || value.isEmpty()) {
            throw new RowParseException(
                    String.format("Row %d: Correct Answer cannot be empty.", lineNumber));
        }

        char answer = Character.toUpperCase(value.charAt(0));
        if (answer != 'A' && answer != 'B' && answer != 'C' && answer != 'D') {
            throw new RowParseException(
                    String.format("Row %d: Invalid Correct Answer '%s'. Must be A, B, C, or D.",
                            lineNumber, value));
        }
        return answer;
    }

    // Public API methods (as specified in UML)

    /**
     * Returns all loaded questions.
     *
     * @return Unmodifiable list of questions
     */
    public List<Question> getAllQuestions() {
        return Collections.unmodifiableList(questions);
    }

    /**
     * Returns the number of loaded questions.
     *
     * @return Question count
     */
    public int getQuestionCount() {
        return questions.size();
    }

    /**
     * Returns any parse errors encountered during loading.
     *
     * @return List of error messages
     */
    public List<String> getParseErrors() {
        return Collections.unmodifiableList(parseErrors);
    }

    /**
     * Checks if there were any parse errors.
     *
     * @return true if errors occurred
     */
    public boolean hasParseErrors() {
        return !parseErrors.isEmpty();
    }

    /**
     * Returns the path of the loaded CSV file.
     *
     * @return The CSV file path
     */
    public String getCsvPath() {
        return csvPath;
    }

    /**
     * Checks if questions have been loaded.
     *
     * @return true if questions are loaded
     */
    public boolean isLoaded() {
        return !questions.isEmpty();
    }

    // ==================== CRUD Operations ====================

    /**
     * Adds a new question to the bank.
     *
     * @param question The question to add
     * @throws IllegalArgumentException if a question with the same ID already exists
     */
    public void addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        // Check for duplicate ID
        if (questions.stream().anyMatch(q -> q.getId() == question.getId())) {
            throw new IllegalArgumentException(
                    "A question with ID " + question.getId() + " already exists");
        }

        questions.add(question);
    }

    /**
     * Updates an existing question in the bank.
     *
     * @param question The updated question (matched by ID)
     * @throws IllegalArgumentException if no question with the given ID exists
     */
    public void updateQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        int index = findQuestionIndexById(question.getId());
        if (index == -1) {
            throw new IllegalArgumentException(
                    "No question found with ID " + question.getId());
        }

        questions.set(index, question);
    }

    /**
     * Deletes a question from the bank.
     *
     * @param questionId The ID of the question to delete
     * @throws IllegalArgumentException if no question with the given ID exists
     */
    public void deleteQuestion(int questionId) {
        int index = findQuestionIndexById(questionId);
        if (index == -1) {
            throw new IllegalArgumentException(
                    "No question found with ID " + questionId);
        }

        questions.remove(index);
    }

    /**
     * Finds the index of a question by its ID.
     *
     * @param id The question ID to search for
     * @return The index of the question, or -1 if not found
     */
    private int findQuestionIndexById(int id) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Saves the current question bank to a CSV file.
     *
     * @param filePath The file path to save to
     * @throws IOException if the file cannot be written
     */
    public void saveToFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath, StandardCharsets.UTF_8))) {

            // Write header
            writer.write(String.join(",", EXPECTED_HEADERS));
            writer.newLine();

            // Write each question
            for (Question q : questions) {
                String line = formatQuestionAsCSV(q);
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Formats a question as a CSV line, properly escaping fields with commas.
     *
     * @param q The question to format
     * @return CSV-formatted string
     */
    private String formatQuestionAsCSV(Question q) {
        return String.format("%d,%s,%d,%s,%s,%s,%s,%c",
                q.getId(),
                escapeCSVField(q.getText()),
                q.getDifficulty().getValue(),
                escapeCSVField(q.getOptionA()),
                escapeCSVField(q.getOptionB()),
                escapeCSVField(q.getOptionC()),
                escapeCSVField(q.getOptionD()),
                q.getCorrectOption()
        );
    }

    /**
     * Escapes a CSV field by wrapping it in quotes if it contains commas.
     *
     * @param field The field to escape
     * @return Escaped field
     */
    private String escapeCSVField(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            // Escape quotes by doubling them and wrap in quotes
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Gets the next available ID for a new question.
     *
     * @return The next ID (max ID + 1, or 1 if bank is empty)
     */
    public int getNextAvailableId() {
        return questions.stream()
                .mapToInt(Question::getId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Exception thrown when CSV parsing fails at the file level.
     */
    public static class CSVParseException extends Exception {
        public CSVParseException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a single row fails to parse.
     */
    public static class RowParseException extends Exception {
        public RowParseException(String message) {
            super(message);
        }
    }
}