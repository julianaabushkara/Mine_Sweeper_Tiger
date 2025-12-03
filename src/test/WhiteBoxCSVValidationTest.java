package test;

import minesweeper.model.QuestionBank;
import minesweeper.model.QuestionDifficulty;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;

/**
 * WB-01: White Box Test for CSV Validation Logic (JUnit Format)
 *
 * This test examines the internal control flow of the CSV validation process
 * in the QuestionBank class, focusing on path coverage and branch testing.
 *
 * Control Flow Graph Analysis:
 * - validateHeaders() has 3 decision points
 * - parseDifficulty() has multiple branches
 * - parseCorrectAnswer() has validation branches
 *
 * @author Yotam - Team Tiger
 */
public class WhiteBoxCSVValidationTest {

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
     * WB-01-TC01: Valid Headers Path
     * Path: Entry → Count Check (Pass) → Name Loop (All Pass) → Exit
     */
    @Test
    public void testValidHeaders() throws Exception {
        String csvContent = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n" +
                "1,Test Question,1,Opt A,Opt B,Opt C,Opt D,A\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        assertEquals("Should load 1 question", 1, questionBank.getQuestionCount());
        assertFalse("Should have no parse errors", questionBank.hasParseErrors());
    }

    /**
     * WB-01-TC02: Invalid Header Count Branch
     * Path: Entry → Count Check (Fail) → Exception
     */
    @Test
    public void testInvalidHeaderCount() {
        try {
            String csvContent = "ID,Question,Difficulty,A,B,C,D\n" +
                    "1,Test Question,1,Opt A,Opt B,Opt C,Opt D\n";

            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

            fail("Should throw exception for invalid header count");
        } catch (QuestionBank.CSVParseException e) {
            assertTrue("Error should mention header count",
                    e.getMessage().contains("Invalid header count"));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass().getName());
        }
    }

    /**
     * WB-01-TC03: Invalid Header Name Branch
     * Path: Entry → Count Check (Pass) → Name Loop → Name Check (Fail) → Exception
     */
    @Test
    public void testInvalidHeaderName() {
        try {
            String csvContent = "ID,Question,Difficulty,A,B,C,D,Answer\n" +
                    "1,Test Question,1,Opt A,Opt B,Opt C,Opt D,A\n";

            tempCsvFile = createTempCsvFile(csvContent);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

            fail("Should throw exception for invalid header name");
        } catch (QuestionBank.CSVParseException e) {
            assertTrue("Error should mention invalid header",
                    e.getMessage().contains("Invalid header at column"));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass().getName());
        }
    }

    /**
     * WB-01-TC04: Case-Insensitive Branch
     * Tests the toLowerCase() branch in header comparison
     */
    @Test
    public void testCaseInsensitiveHeaders() throws Exception {
        String csvContent = "id,question,difficulty,a,b,c,d,correct answer\n" +
                "1,Test Question,1,Opt A,Opt B,Opt C,Opt D,A\n";

        tempCsvFile = createTempCsvFile(csvContent);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

        assertEquals("Should accept lowercase headers", 1, questionBank.getQuestionCount());
    }

    /**
     * WB-01-TC05: Difficulty Validation - Valid Branches
     * Tests parseDifficulty() valid branches (1, 2, 3, 4)
     */
    @Test
    public void testValidDifficultyValues() throws Exception {
        // Test difficulty 1
        String csv1 = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,1,A,B,C,D,A\n";
        tempCsvFile = createTempCsvFile(csv1);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
        assertEquals("Difficulty 1 accepted", 1, questionBank.getQuestionCount());

        // Test difficulty 2
        questionBank = new QuestionBank();
        String csv2 = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,2,A,B,C,D,A\n";
        tempCsvFile = createTempCsvFile(csv2);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
        assertEquals("Difficulty 2 accepted", 1, questionBank.getQuestionCount());

        // Test difficulty 3
        questionBank = new QuestionBank();
        String csv3 = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,3,A,B,C,D,A\n";
        tempCsvFile = createTempCsvFile(csv3);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
        assertEquals("Difficulty 3 accepted", 1, questionBank.getQuestionCount());
    }

    /**
     * WB-01-TC06: Difficulty Validation - Invalid Branches
     * Tests parseDifficulty() invalid branches (0, 5, negative)
     */
    @Test
    public void testInvalidDifficultyValues() throws Exception {
        String[] invalidDiffs = {"0", "5", "-1", "easy"};

        for (String diff : invalidDiffs) {
            questionBank = new QuestionBank();
            String csv = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?," + diff + ",A,B,C,D,A\n";

            try {
                tempCsvFile = createTempCsvFile(csv);
                questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
                assertTrue("Difficulty " + diff + " should be rejected",
                        questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
            } catch (QuestionBank.CSVParseException e) {
                // Expected - invalid difficulty
                assertTrue(true);
            }
        }
    }

    /**
     * WB-01-TC07: Answer Validation - Valid Branches
     * Tests parseCorrectAnswer() valid branches (A-D, case insensitive)
     */
    @Test
    public void testValidCorrectAnswers() throws Exception {
        String[] answers = {"A", "B", "C", "D", "a", "b", "c", "d"};

        for (String answer : answers) {
            questionBank = new QuestionBank();
            String csv = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,1,A,B,C,D," + answer + "\n";

            tempCsvFile = createTempCsvFile(csv);
            questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());

            assertEquals("Answer '" + answer + "' should be accepted",
                    1, questionBank.getQuestionCount());
        }
    }

    /**
     * WB-01-TC08: Answer Validation - Invalid Branches
     * Tests parseCorrectAnswer() invalid branches (E-Z, numbers, empty)
     */
    @Test
    public void testInvalidCorrectAnswers() throws Exception {
        String[] invalidAnswers = {"E", "F", "1", "0", ""};

        for (String answer : invalidAnswers) {
            questionBank = new QuestionBank();
            String csv = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,1,A,B,C,D," + answer + "\n";

            try {
                tempCsvFile = createTempCsvFile(csv);
                questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
                assertTrue("Answer '" + answer + "' should be rejected",
                        questionBank.hasParseErrors() || questionBank.getQuestionCount() == 0);
            } catch (QuestionBank.CSVParseException e) {
                // Expected - invalid answer
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
}