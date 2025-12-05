package test;

import minesweeper.model.QuestionBank;
import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.*;

import java.io.*;

/**
 * BB-01: Black Box Test for CSV Validation Logic for the Question Wizard
 * Checks that only CSV's in the correct format can be added to the Question Wizard
 * Uses the logic implemented in the QuestionBank Class
 * All check the error handling configs in the QuestionBank.loadFromCsv method
 *
 * Control Flow Graph Analysis:
 * - validateHeaders() has 3 decision points
 * - parseDifficulty() has multiple branches
 * - parseCorrectAnswer() has validation branches
 *
 * @author of CSV Wizard is Yotam
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
     * Tests that a CSV with Valid rows correctly loads into the question Wizard
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
     * Checks that Question ID 1 is accepted
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
     * Checks that question ID 0 is NOT accepted
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
     * Tests that negative number question ID's are not accepted
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
     * Tests that not natrual number ID's are not accepted
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
     * Checks that only four difficulties are avilable (easy, medium, hard, expert)
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
     * Checks that any other difficulty besides the four specified correctly parses as an error
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
     * BB-01-TC08: Difficulty Boundary - Above Maximum (4)
     * Tests boundary: Checks that difficulty '5' is not accepted and correctly parses as error
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
     * BB-01-TC09: Answer Equivalence Class - Valid (A, B, C, D)
     * Tests that A,B,C,D are valid answers
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
     * Checks that QuestionWizard CSV reader is not case-sensitive
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
     * BB-01-TC11: Answer Equivalence Class - Invalid (E, F, etc.)
     * Tests that answers besides A,B,C,D are not parsed
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
     * Tests numeric input for answer field
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
     * Tests that there must be a string in question
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
     * Tests that all option fields reject empty values
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



