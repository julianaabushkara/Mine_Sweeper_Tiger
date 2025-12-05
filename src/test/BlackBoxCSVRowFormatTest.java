package test;

import minesweeper.model.QuestionBank;
import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.*;

import java.io.*;

/**
 * BB-01: Black Box Test for CSV Row Format Validation
 * Tests CSV validation using Equivalence Class Partitioning and Boundary Value Analysis
 * WITHOUT examining internal code implementation
 *
 * Testing Techniques Used:
 * - Equivalence Class Partitioning: Dividing input domain into valid/invalid classes
 * - Boundary Value Analysis: Testing at the edges of valid ranges
 * - Error Guessing: Testing common error scenarios
 *
 * @author of CSV Wizard is Yotam (mintycake420)
 */
public class BlackBoxCSVRowFormatTest {

    private QuestionBank questionBank;
    private File tempCsvFile;

    @Before
    public void setUp() {
        questionBank = new QuestionBank();
        tempCsvFile = null;
    }

    @After
    public void tearDown() {
        if (tempCsvFile != null && tempCsvFile.exists()) {
            tempCsvFile.delete();
        }
    }

    /**
     * BB-01-TC01: Valid Row - All Fields Correct
     *
     * EQUIVALENCE CLASS: Valid Input Class (All Fields)
     * - ID: Positive integers {1, 2, 3, ..., ∞}
     * - Question: Non-empty strings
     * - Difficulty: {1, 2, 3, 4}
     * - Options A-D: Non-empty strings
     * - Correct Answer: {A, B, C, D, a, b, c, d}
     *
     * TEST TECHNIQUE: Equivalence Class Partitioning
     * Testing one representative from the VALID equivalence class
     *
     * WHY THIS TEST IS NEEDED:
     * Verifies that the system correctly accepts well-formed CSV rows.
     * This is the baseline test - if valid input fails, the system is broken.
     * Establishes that the "happy path" works before testing error cases.
     *
     * EXPECTED BEHAVIOR: CSV loads successfully with 1 question
     */
    @Test
    public void testValidRow() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,What is 2+2?,1,Three,Four,Five,Six,B\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        assertEquals("Should load 1 valid question", 1, questionBank.getQuestionCount());
        assertFalse("Should have no errors", questionBank.hasParseErrors());
    }

    /**
     * BB-01-TC02: ID Boundary Value - Minimum Valid (1)
     *
     * EQUIVALENCE CLASS: Valid ID Class
     * - Valid: {1, 2, 3, ..., ∞}
     * - Invalid: {0, -1, -2, ..., -∞} ∪ {non-numeric values}
     *
     * BOUNDARY VALUES:
     * - 0 (invalid boundary) ← Just below valid range
     * - 1 (valid boundary)   ← Minimum valid value  TESTING THIS
     *
     * TEST TECHNIQUE: Boundary Value Analysis
     * Testing the MINIMUM VALID boundary
     *
     * WHY THIS TEST IS NEEDED:
     * Boundary values are where bugs often occur (off-by-one errors).
     * Verifies that ID=1 is accepted as the smallest valid question ID.
     * Common mistake: Systems might require ID > 1 instead of ID ≥ 1.
     *
     * EXPECTED BEHAVIOR: ID=1 accepted successfully
     */
    @Test
    public void testIDMinimumValidBoundary() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test?,1,A,B,C,D,A\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        assertEquals("ID=1 (minimum valid) should be accepted",
                1, questionBank.getQuestionCount());
    }

    /**
     * BB-01-TC03: ID Boundary Value - Zero (Invalid)
     *
     * EQUIVALENCE CLASS: Invalid ID Class (Zero/Negative)
     * - Invalid: {0, -1, -2, ..., -∞}
     * - Valid: {1, 2, 3, ..., ∞}
     *
     * BOUNDARY VALUES:
     * - 0 (invalid boundary)  TESTING THIS ← Just below valid range
     * - 1 (valid boundary)   ← Minimum valid value
     *
     * TEST TECHNIQUE: Boundary Value Analysis
     * Testing the MAXIMUM INVALID boundary (closest invalid value to valid range)
     *
     * WHY THIS TEST IS NEEDED:
     * Zero is a special case - neither positive nor negative.
     * Tests that the system properly rejects ID=0 (invalid boundary).
     * Common mistake: Systems might accept 0 as a valid ID.
     * Critical for data integrity - question IDs must be positive.
     *
     * EXPECTED BEHAVIOR: ID=0 rejected with error
     */
    @Test
    public void testIDZeroBoundary() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "0,Test?,1,A,B,C,D,A\n";

        try {
            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("ID=0 should be rejected",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            // Expected - ID must be positive
            assertTrue(true);
        }
    }

    /**
     * BB-01-TC04: ID Equivalence Class - Negative (Invalid)
     *
     * EQUIVALENCE CLASS: Invalid ID Class (Negative Numbers)
     * - Invalid: {-1, -2, -3, ..., -∞}
     * - Valid: {1, 2, 3, ..., ∞}
     *
     * TEST TECHNIQUE: Equivalence Class Partitioning
     * Testing one representative from the INVALID (negative) equivalence class
     *
     * WHY THIS TEST IS NEEDED:
     * Verifies that ALL negative numbers are rejected (not just zero).
     * Tests error handling for negative ID values.
     * Ensures data validation prevents illogical question IDs.
     * Representative value (-1) stands for entire negative number class.
     *
     * EXPECTED BEHAVIOR: Negative ID rejected with error
     */
    @Test
    public void testIDNegative() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "-1,Test?,1,A,B,C,D,A\n";

        try {
            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("ID=-1 should be rejected",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            assertTrue(true);
        }
    }

    /**
     * BB-01-TC05: ID Equivalence Class - Non-Numeric (Invalid)
     *
     * EQUIVALENCE CLASS: Invalid ID Class (Non-Numeric Values)
     * - Invalid: {strings, special chars, empty}
     * - Valid: {1, 2, 3, ..., ∞}
     *
     * TEST TECHNIQUE: Equivalence Class Partitioning + Error Guessing
     * Testing one representative from the INVALID (non-numeric) equivalence class
     *
     * WHY THIS TEST IS NEEDED:
     * Tests type validation - ID must be numeric.
     * Common user error: entering text instead of numbers.
     * Verifies the system handles type mismatch gracefully.
     * Prevents database corruption from non-integer ID values.
     *
     * EXPECTED BEHAVIOR: Non-numeric ID rejected with error
     */
    @Test
    public void testIDNonNumeric() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "abc,Test?,1,A,B,C,D,A\n";

        try {
            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("ID=abc should be rejected",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            assertTrue(true);
        }
    }

    /**
     * BB-01-TC06: Difficulty Equivalence Class - Valid (1, 2, 3, 4)
     *
     * EQUIVALENCE CLASS: Valid Difficulty Class
     * - Valid: {1, 2, 3, 4}
     *   • 1 = Easy
     *   • 2 = Medium
     *   • 3 = Hard
     *   • 4 = Expert
     * - Invalid: {0, 5, 6, ..., ∞} ∪ {negative numbers} ∪ {non-numeric}
     *
     * TEST TECHNIQUE: Equivalence Class Partitioning
     * Testing ALL members of the VALID equivalence class
     *
     * WHY THIS TEST IS NEEDED:
     * Verifies that all four difficulty levels are accepted.
     * Critical for game functionality - questions need difficulty ratings.
     * Ensures no difficulty level is accidentally excluded.
     * Tests the complete valid range (not just boundaries).
     *
     * EXPECTED BEHAVIOR: All four difficulty values (1, 2, 3, 4) accepted
     */
    @Test
    public void testDifficultyValidClass() throws Exception {
        String[] validDifficulties = {"1", "2", "3", "4"};

        for (String diff : validDifficulties) {
            questionBank = new QuestionBank();
            String csv = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?," + diff + ",A,B,C,D,A\n";

            tempCsvFile = createTempCsvFile(csv);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

            assertEquals("Difficulty=" + diff + " should be valid",
                    1, questionBank.getQuestionCount());
        }
    }

    /**
     * BB-01-TC07: Difficulty Boundary - Below Minimum (0)
     *
     * EQUIVALENCE CLASS: Invalid Difficulty Class (Below Range)
     * - Invalid: {0, -1, -2, ..., -∞}
     * - Valid: {1, 2, 3, 4}
     *
     * BOUNDARY VALUES:
     * - 0 (invalid boundary)  ← Just below minimum valid
     * - 1 (valid boundary)   ← Minimum valid value (Easy)
     *
     * TEST TECHNIQUE: Boundary Value Analysis
     * Testing the LOWER INVALID boundary
     *
     * WHY THIS TEST IS NEEDED:
     * Tests that difficulty=0 is properly rejected.
     * Boundary testing often reveals off-by-one errors.
     * Zero is a common edge case that needs explicit testing.
     * Prevents invalid difficulty levels from entering the system.
     *
     * EXPECTED BEHAVIOR: Difficulty=0 rejected with error
     */
    @Test
    public void testDifficultyBelowMinimum() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test?,0,A,B,C,D,A\n";

        try {
            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("Difficulty=0 should be rejected",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            assertTrue(true);
        }
    }

    /**
     * BB-01-TC08: Difficulty Boundary - Above Maximum (5)
     *
     * EQUIVALENCE CLASS: Invalid Difficulty Class (Above Range)
     * - Invalid: {5, 6, 7, ..., ∞}
     * - Valid: {1, 2, 3, 4}
     *
     * BOUNDARY VALUES:
     * - 4 (valid boundary)   ← Maximum valid value (Expert)
     * - 5 (invalid boundary)  TESTING THIS ← Just above maximum valid
     *
     * TEST TECHNIQUE: Boundary Value Analysis
     * Testing the UPPER INVALID boundary
     *
     * WHY THIS TEST IS NEEDED:
     * Tests that difficulty=5 is properly rejected (system only has 4 levels).
     * Critical boundary test - validates upper limit enforcement.
     * Prevents undefined difficulty levels from being added.
     * Common mistake: Systems might accept any positive number.
     *
     * EXPECTED BEHAVIOR: Difficulty=5 rejected with error
     */
    @Test
    public void testDifficultyAboveMaximum() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test?,5,A,B,C,D,A\n";

        try {
            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("Difficulty=5 should be rejected",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            assertTrue(true);
        }
    }

    /**
     * BB-01-TC09: Answer Equivalence Class - Valid Uppercase (A, B, C, D)
     *
     * EQUIVALENCE CLASS: Valid Answer Class (Uppercase)
     * - Valid (uppercase): {A, B, C, D}
     * - Valid (lowercase): {a, b, c, d}
     * - Invalid: {E-Z, 0-9, special chars, empty}
     *
     * TEST TECHNIQUE: Equivalence Class Partitioning
     * Testing ALL members of the VALID (uppercase) equivalence class
     *
     * WHY THIS TEST IS NEEDED:
     * Verifies that all four answer options (A, B, C, D) are accepted.
     * Critical for multiple-choice functionality - must support all options.
     * Tests that each valid answer choice can be marked as correct.
     * Ensures no valid answer option is accidentally excluded.
     *
     * EXPECTED BEHAVIOR: All uppercase answers (A, B, C, D) accepted
     */
    @Test
    public void testAnswerValidClassUppercase() throws Exception {
        String[] validAnswers = {"A", "B", "C", "D"};

        for (String answer : validAnswers) {
            questionBank = new QuestionBank();
            String csv = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,1,A,B,C,D," + answer + "\n";

            tempCsvFile = createTempCsvFile(csv);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

            assertEquals("Answer=" + answer + " should be valid",
                    1, questionBank.getQuestionCount());
        }
    }

    /**
     * BB-01-TC10: Answer Equivalence Class - Valid Lowercase (a, b, c, d)
     *
     * EQUIVALENCE CLASS: Valid Answer Class (Lowercase)
     * - Valid (uppercase): {A, B, C, D}
     * - Valid (lowercase): {a, b, c, d}
     * - Invalid: {E-Z, 0-9, special chars, empty}
     *
     * TEST TECHNIQUE: Equivalence Class Partitioning
     * Testing ALL members of the VALID (lowercase) equivalence class
     *
     * WHY THIS TEST IS NEEDED:
     * Tests case-insensitivity feature for answer validation.
     * Improves user experience - users shouldn't worry about capitalization.
     * Common real-world scenario - users type in lowercase.
     * Verifies that case normalization works correctly.
     * Prevents rejection of valid answers due to case differences.
     *
     * EXPECTED BEHAVIOR: All lowercase answers (a, b, c, d) accepted
     */
    @Test
    public void testAnswerValidClassLowercase() throws Exception {
        String[] validAnswers = {"a", "b", "c", "d"};

        for (String answer : validAnswers) {
            questionBank = new QuestionBank();
            String csv = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,1,A,B,C,D," + answer + "\n";

            tempCsvFile = createTempCsvFile(csv);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

            assertEquals("Answer=" + answer + " (lowercase) should be valid",
                    1, questionBank.getQuestionCount());
        }
    }

    /**
     * BB-01-TC11: Answer Equivalence Class - Invalid Letters (E, F, etc.)
     *
     * EQUIVALENCE CLASS: Invalid Answer Class (Wrong Letters)
     * - Valid: {A, B, C, D, a, b, c, d}
     * - Invalid: {E-Z, e-z, ...}
     *
     * TEST TECHNIQUE: Equivalence Class Partitioning + Error Guessing
     * Testing representatives from the INVALID (wrong letters) equivalence class
     *
     * WHY THIS TEST IS NEEDED:
     * Tests that only A-D are accepted (no E, F, etc.).
     * Prevents invalid answer choices that don't match question options.
     * Common user error: typing E or F when only 4 options exist.
     * Ensures data consistency - answer must match available options.
     *
     * EXPECTED BEHAVIOR: Letters E-Z rejected with error
     */
    @Test
    public void testAnswerInvalidLetters() throws Exception {
        String[] invalidAnswers = {"E", "F", "Z"};

        for (String answer : invalidAnswers) {
            questionBank = new QuestionBank();
            String csv = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,1,A,B,C,D," + answer + "\n";

            try {
                tempCsvFile = createTempCsvFile(csv);
                questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
                assertTrue("Answer=" + answer + " should be rejected",
                        questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
            } catch (QuestionBank.CSVParseException e) {
                assertTrue(true);
            }
        }
    }

    /**
     * BB-01-TC12: Answer Equivalence Class - Invalid Numeric
     *
     * EQUIVALENCE CLASS: Invalid Answer Class (Numeric Values)
     * - Valid: {A, B, C, D, a, b, c, d}
     * - Invalid: {0, 1, 2, 3, 4, ...}
     *
     * TEST TECHNIQUE: Equivalence Class Partitioning + Error Guessing
     * Testing one representative from the INVALID (numeric) equivalence class
     *
     * WHY THIS TEST IS NEEDED:
     * Tests type validation - answer must be a letter, not a number.
     * Common user error: entering "1" instead of "A".
     * Prevents confusion between answer position and answer letter.
     * Ensures consistent answer format (letters only).
     *
     * EXPECTED BEHAVIOR: Numeric answer rejected with error
     */
    @Test
    public void testAnswerNumeric() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test?,1,A,B,C,D,1\n";

        try {
            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("Numeric answer should be rejected",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            assertTrue(true);
        }
    }

    /**
     * BB-01-TC13: Question Field - Empty String (Invalid)
     *
     * EQUIVALENCE CLASS: Invalid Question Class
     * - Valid: {non-empty strings}
     * - Invalid: {empty string, whitespace only}
     *
     * TEST TECHNIQUE: Equivalence Class Partitioning + Error Guessing
     * Testing one representative from the INVALID (empty) equivalence class
     *
     * WHY THIS TEST IS NEEDED:
     * Questions must have content - empty questions are meaningless.
     * Tests required field validation for question text.
     * Prevents invalid quiz questions from being created.
     * Common data quality issue - missing required content.
     * Ensures database integrity (no null/empty questions).
     *
     * EXPECTED BEHAVIOR: Empty question rejected with error
     */
    @Test
    public void testQuestionFieldEmpty() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,,1,A,B,C,D,A\n";

        try {
            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("Empty question should be rejected",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            assertTrue(true);
        }
    }

    /**
     * BB-01-TC14: Option Fields - Empty String (Invalid)
     *
     * EQUIVALENCE CLASS: Invalid Option Class
     * - Valid: {non-empty strings}
     * - Invalid: {empty string, whitespace only}
     *
     * TEST TECHNIQUE: Equivalence Class Partitioning + Error Guessing
     * Testing representatives from INVALID (empty) class for EACH option field
     *
     * WHY THIS TEST IS NEEDED:
     * All four answer options must have content - empty options are invalid.
     * Tests required field validation for each answer choice (A, B, C, D).
     * Prevents incomplete multiple-choice questions.
     * Ensures all answer options are meaningful.
     * Critical for game playability - users need valid choices.
     * Tests each field independently (not just one representative).
     *
     * EXPECTED BEHAVIOR: Empty option (A, B, C, or D) rejected with error
     */
    @Test
    public void testOptionFieldsEmpty() throws Exception {
        // Test each option field being empty
        String[] emptyOptionCSVs = {
                "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,1,,B,C,D,B\n",  // Empty A
                "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,1,A,,C,D,A\n",  // Empty B
                "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,1,A,B,,D,A\n",  // Empty C
                "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,1,A,B,C,,A\n"   // Empty D
        };

        for (String csv : emptyOptionCSVs) {
            questionBank = new QuestionBank();

            try {
                tempCsvFile = createTempCsvFile(csv);
                questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
                assertTrue("Empty option field should be rejected",
                        questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
            } catch (QuestionBank.CSVParseException e) {
                assertTrue(true);
            }
        }
    }


    private File createTempCsvFile(String content) throws IOException {
        File tempFile = File.createTempFile("test_", ".csv");
        FileWriter writer = new FileWriter(tempFile);
        writer.write(content);
        writer.close();
        return tempFile;
    }

    public static void main(String[] args) {

        System.out.println("Running BlackBoxCSVRowFormatTest...");

        Result result = JUnitCore.runClasses(BlackBoxCSVRowFormatTest.class);

        System.out.println("\n=== TEST RESULTS ===");
        System.out.println("Run count: " + result.getRunCount());
        System.out.println("Failures : " + result.getFailureCount());
        System.out.println("Ignored  : " + result.getIgnoreCount());
        System.out.println("Run time : " + result.getRunTime() + " ms");

        if (!result.wasSuccessful()) {
            System.out.println("\n=== FAILURES ===");
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        } else {
            System.out.println("\nALL TESTS PASSED ✔️");
        }
    }
}