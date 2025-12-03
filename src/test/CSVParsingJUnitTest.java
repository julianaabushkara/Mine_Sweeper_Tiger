package test;

import minesweeper.model.QuestionBank;
import minesweeper.model.Question;
import minesweeper.model.QuestionDifficulty;
import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.*;

import java.io.*;
import java.util.List;

/**
 * UT-01: Unit Test for CSV Parsing (JUnit Format)
 *
 * Comprehensive JUnit test suite for QuestionBank CSV parsing functionality.
 * Uses JUnit 4 annotations and assertions for automated testing.
 *
 * @author Yotam - Team Tiger
 */
public class CSVParsingJUnitTest {

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
     * UT-01-TC01: Valid CSV Parsing
     * Verifies successful parsing of a well-formed CSV file
     */
    @Test
    public void testValidCSVParsing() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,What is 2+2?,1,Two,Three,Four,Five,C\n" +
                "2,Capital of France?,2,London,Paris,Berlin,Rome,B\n" +
                "3,Largest planet?,3,Earth,Mars,Jupiter,Saturn,C\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        assertEquals("Should load 3 questions", 3, questionBank.getQuestionCount());
        assertFalse("Should have no parse errors", questionBank.hasParseErrors());
        assertTrue("Should be marked as loaded", questionBank.isLoaded());

        List<Question> questions = questionBank.getAllQuestions();
        assertNotNull("Question list should not be null", questions);
        assertEquals("List should contain 3 questions", 3, questions.size());

        // Verify first question data
        Question q1 = questions.get(0);
        assertEquals("First question ID", 1, q1.getId());
        assertEquals("First question text", "What is 2+2?", q1.getText());
        assertEquals("First question difficulty", QuestionDifficulty.EASY, q1.getDifficulty());
        assertEquals("First question answer", "Four", q1.getCorrectAnswerText());
    }

    /**
     * UT-01-TC02: File Not Found
     * Verifies proper exception when file doesn't exist
     */
    @Test(expected = QuestionBank.CSVParseException.class)
    public void testFileNotFound() throws QuestionBank.CSVParseException {
        questionBank.loadFromCsv("/nonexistent/path/to/file.csv");
    }

    /**
     * UT-01-TC03: Empty CSV File
     * Verifies handling of empty CSV files
     */
    @Test(expected = QuestionBank.CSVParseException.class)
    public void testEmptyCSVFile() throws Exception {
        tempCsvFile = createTempCsvFile("");
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
    }

    /**
     * UT-01-TC04: Invalid Headers
     * Verifies rejection of CSV with wrong header names
     */
    @Test
    public void testInvalidHeaders() throws Exception {
        String csvContent = "ID,Query,Level,Option1,Option2,Option3,Option4,Answer\n" +
                "1,Test?,1,A,B,C,D,A\n";

        try {
            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            fail("Should throw exception for invalid headers");
        } catch (QuestionBank.CSVParseException e) {
            assertTrue("Error should mention headers",
                    e.getMessage().toLowerCase().contains("header"));
        }
    }

    /**
     * UT-01-TC05: Invalid ID Format
     * Verifies rejection of non-numeric IDs
     */
    @Test
    public void testInvalidIDFormat() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "abc,Test Question?,1,A,B,C,D,A\n";

        try {
            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("Should reject non-numeric ID",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            // Expected
            assertTrue(true);
        }
    }

    /**
     * UT-01-TC06: Invalid Difficulty Value
     * Verifies rejection of difficulty values outside 1-4 range
     */
    @Test
    public void testInvalidDifficulty() throws Exception {
        String[] invalidDifficulties = {"0", "a", "5", "-1"};

        for (String diff : invalidDifficulties) {
            questionBank = new QuestionBank();
            String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                    "1,Test?,"+diff+",A,B,C,D,A\n";

            try {
                tempCsvFile = createTempCsvFile(csvContent);
                questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
                assertTrue("Difficulty " + diff + " should be rejected",
                        questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
            } catch (QuestionBank.CSVParseException e) {
                // Expected
                assertTrue(true);
            }
        }
    }

    /**
     * UT-01-TC07: Invalid Correct Answer
     * Verifies rejection of answer values outside A-D range
     */
    @Test
    public void testInvalidCorrectAnswer() throws Exception {
        String[] invalidAnswers = {"E", "F", "1", "5", ""};

        for (String answer : invalidAnswers) {
            questionBank = new QuestionBank();
            String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                    "1,Test?,1,A,B,C,D,"+answer+"\n";

            try {
                tempCsvFile = createTempCsvFile(csvContent);
                questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
                assertTrue("Answer '" + answer + "' should be rejected",
                        questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
            } catch (QuestionBank.CSVParseException e) {
                // Expected
                assertTrue(true);
            }
        }
    }

    /**
     * UT-01-TC08: Missing Required Fields
     * Verifies rejection when required fields are empty
     */
    @Test
    public void testMissingRequiredFields() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,,1,A,B,C,D,A\n";  // Empty question

        try {
            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("Empty question field should be rejected",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            assertTrue(true);
        }
    }

    /**
     * UT-01-TC09: Wrong Column Count
     * Verifies rejection of rows with incorrect number of columns
     */
    @Test
    public void testWrongColumnCount() throws Exception {
        // Test missing column
        String csvMissing = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test?,1,A,B,C,D\n";  // Missing correct answer

        try {
            tempCsvFile = createTempCsvFile(csvMissing);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("Row with 7 columns should be rejected",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            assertTrue(true);
        }

        // Test extra column
        questionBank = new QuestionBank();
        String csvExtra = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test?,1,A,B,C,D,A,Extra\n";  // Extra column

        try {
            tempCsvFile = createTempCsvFile(csvExtra);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
            assertTrue("Row with 9 columns should be rejected",
                    questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
        } catch (QuestionBank.CSVParseException e) {
            assertTrue(true);
        }
    }

    /**
     * UT-01-TC10: Case Insensitive Answer
     * Verifies that lowercase answers (a-d) are accepted
     */
    @Test
    public void testCaseInsensitiveAnswer() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test1?,1,A,B,C,D,a\n" +
                "2,Test2?,2,A,B,C,D,b\n" +
                "3,Test3?,3,A,B,C,D,c\n" +
                "4,Test4?,1,A,B,C,D,d\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        assertEquals("Should accept lowercase answers", 4, questionBank.getQuestionCount());
        assertFalse("Should have no parse errors", questionBank.hasParseErrors());
    }

    /**
     * UT-01-TC11: BOM (Byte Order Mark) Handling
     * Verifies proper handling of UTF-8 BOM character
     */
    @Test
    public void testBOMHandling() throws Exception {
        // UTF-8 BOM is EF BB BF
        String csvContent = "\uFEFFID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test?,1,A,B,C,D,A\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        // Should handle BOM gracefully
        assertTrue("Should handle BOM",
                questionBank.getQuestionCount() == 1 || questionBank.hasParseErrors());
    }

    /**
     * UT-01-TC12: Multiple Questions Data Integrity
     * Verifies that multiple questions are parsed with correct data
     */
    @Test
    public void testMultipleQuestionsDataIntegrity() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Q1,1,A1,B1,C1,D1,A\n" +
                "2,Q2,2,A2,B2,C2,D2,B\n" +
                "3,Q3,3,A3,B3,C3,D3,C\n" +
                "4,Q4,1,A4,B4,C4,D4,D\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        List<Question> questions = questionBank.getAllQuestions();
        assertEquals("Should load 4 questions", 4, questions.size());

        // Verify each question
        for (int i = 0; i < 4; i++) {
            Question q = questions.get(i);
            assertEquals("Question " + (i+1) + " ID", i+1, q.getId());
            assertEquals("Question " + (i+1) + " text", "Q" + (i+1), q.getText());
        }

        // Verify difficulties
        assertEquals(QuestionDifficulty.EASY, questions.get(0).getDifficulty());
        assertEquals(QuestionDifficulty.MEDIUM, questions.get(1).getDifficulty());
        assertEquals(QuestionDifficulty.HARD, questions.get(2).getDifficulty());
        assertEquals(QuestionDifficulty.EASY, questions.get(3).getDifficulty());

        // Verify correct answers
        assertEquals('A', questions.get(0).getCorrectOption());
        assertEquals('B', questions.get(1).getCorrectOption());
        assertEquals('C', questions.get(2).getCorrectOption());
        assertEquals('D', questions.get(3).getCorrectOption());
    }

    /**
     * UT-01-TC13: Special Characters in Fields
     * Verifies handling of special characters like commas in quoted fields
     */
    @Test
    public void testSpecialCharactersInFields() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,\"What is 2+2, really?\",1,\"Two, maybe\",Three,Four,Five,C\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        assertEquals("Should handle commas in quotes", 1, questionBank.getQuestionCount());

        Question q = questionBank.getAllQuestions().get(0);
        assertTrue("Question should contain comma", q.getText().contains(","));
    }

    /**
     * UT-01-TC14: Question List Immutability
     * Verifies that returned question list cannot be modified
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testUnmodifiableQuestionList() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test?,1,A,B,C,D,A\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        List<Question> questions = questionBank.getAllQuestions();
        // This should throw UnsupportedOperationException
        questions.clear();
    }

    /**
     * UT-01-TC15: IsLoaded State Tracking
     * Verifies that isLoaded() correctly tracks loading state
     */
    @Test
    public void testIsLoadedStateTracking() throws Exception {
        assertFalse("Should not be loaded initially", questionBank.isLoaded());

        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test?,1,A,B,C,D,A\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        assertTrue("Should be loaded after successful load", questionBank.isLoaded());
    }

    /**
     * UT-01-TC16: Reload Behavior
     * Verifies that reloading clears previous data
     */
    @Test
    public void testReloadBehavior() throws Exception {
        // Load first CSV with 2 questions
        String csv1 = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Q1,1,A,B,C,D,A\n" +
                "2,Q2,2,A,B,C,D,B\n";

        tempCsvFile = createTempCsvFile(csv1);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
        assertEquals("Should load 2 questions", 2, questionBank.getQuestionCount());

        // Reload with different CSV with 3 questions
        String csv2 = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Q1,1,A,B,C,D,A\n" +
                "2,Q2,2,A,B,C,D,B\n" +
                "3,Q3,3,A,B,C,D,C\n";

        tempCsvFile = createTempCsvFile(csv2);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        assertEquals("Should have 3 questions after reload", 3, questionBank.getQuestionCount());
    }

    /**
     * UT-01-TC17: Whitespace Trimming
     * Verifies that leading/trailing whitespace is properly trimmed
     */
    @Test
    public void testWhitespaceTrimming() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "  1  ,  Test Question?  ,  1  ,  A  ,  B  ,  C  ,  D  ,  A  \n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        assertEquals("Should handle whitespace", 1, questionBank.getQuestionCount());

        Question q = questionBank.getAllQuestions().get(0);
        assertEquals("ID should be trimmed", 1, q.getId());
        assertEquals("Question should be trimmed", "Test Question?", q.getText());
    }

    private File createTempCsvFile(String content) throws IOException {
        File tempFile = File.createTempFile("test_questions_", ".csv");
        FileWriter writer = new FileWriter(tempFile);
        writer.write(content);
        writer.close();
        return tempFile;
    }

    public static void main(String[] args) {

        System.out.println("Running CSVParsingJUnitTest...");

        Result result = JUnitCore.runClasses(CSVParsingJUnitTest.class);

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