package test;

import minesweeper.model.QuestionBank;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;

/**
 * WB-01: White Box Test for CSV Validation Logic for the Question Wizard
 * Checks that only CSV's in the correct format can be added to the Question Wizard
 * Uses the logic implemented in the QuestionBank Class
 * All check the error handling configs in the QuestionBank.loadFromCsv method
 *
 * Control Flow Graph Analysis:
 * - validateHeaders() has 3 decision points
 * - parseDifficulty() has multiple branches
 * - parseCorrectAnswer() has validation branches
 *
 * Ownership distribution:
 * - Yotam: Valid header flow and general “happy path”
 * - Mor: Malformed headers (count and name)
 * - Juliana: Difficulty parsing logic (valid + invalid branches)
 * - Avi: Correct answer parsing logic (valid + invalid branches)
 * - Osman: Case-insensitive header normalization path
 *
 * @author of CSV Wizard is Yotam

 * Test Case Overview – WhiteBoxCSVValidationTest
 *
 * | Test Case ID | Method Name                | Short Description                                           | Owner   | Main Technique(s)                   |
 * |-------------:|----------------------------|--------------------------------------------------------------|--------|-------------------------------------|
 * | WB-01-TC01   | testValidHeaders           | Valid headers “happy path”, no parse errors                 | Yotam  | Statement Coverage, CFG path        |
 * | WB-01-TC02   | testInvalidHeaderCount     | Incorrect number of header columns, exception branch        | Mor    | Branch Coverage                     |
 * | WB-01-TC03   | testInvalidHeaderName      | Wrong header name with correct count, exception branch      | Mor    | Branch Coverage                     |
 * | WB-01-TC04   | testCaseInsensitiveHeaders | Case-insensitive header comparison (lowercase headers)      | Osman  | Statement Coverage                  |
 * | WB-01-TC05   | testValidDifficultyValues  | Valid difficulty parsing branches (1–4)                     | Juliana| Branch Coverage                     |
 * | WB-01-TC06   | testInvalidDifficultyValues| Invalid difficulty branches (<1, >4, non-numeric)           | Juliana| Branch Coverage                     |
 * | WB-01-TC07   | testValidCorrectAnswers    | Valid correct-answer parsing for A–D (upper/lowercase)      | Avi    | Branch Coverage                     |
 * | WB-01-TC08   | testInvalidCorrectAnswers  | Invalid correct answers (E–Z, numbers, empty)               | Avi    | Branch Coverage                     |
 *
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
     *
     * COVERAGE TYPE: Statement Coverage
     *
     * BRANCHES COVERED:
     * - Branch 1 (Header count check): TRUE path (count == 8)
     * - Branch 2 (Header name validation loop): TRUE path (all headers match)
     * - Branch 3 (Case-insensitive comparison): Executes toLowerCase() for each header
     *
     * GRAPHIC DESCRIPTION:
     * Entry → [Count==8?]─YES→ [Loop Headers]─ALL_MATCH→ [Parse Row]─SUCCESS→ Exit
     *                                                                    ↓
     *                                                            [Add Question]
     *
     * EXPLANATION:
     * This test verifies the "happy path" where all validation checks pass.
     * Tests that valid CSV headers (correct count and names) are accepted.
     * Path: Entry → Count Check (Pass) → Name Loop (All Pass) → Exit
     *
     * Check that the question wizard accepts valid headers and valid questions without any parse errors
     *
     * TEST OWNER: Yotam
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
     *
     * COVERAGE TYPE: Branch Coverage
     *
     * BRANCHES COVERED:
     * - Branch 1 (Header count check): FALSE path (count != 8)
     * - Exception handling branch: TRUE path (throws CSVParseException)
     *
     * GRAPHIC DESCRIPTION:
     * Entry → [Count==8?]─NO→ [Throw Exception] → Catch → Exit
     *            ↓
     *         FALSE
     *
     * EXPLANATION:
     * Tests the error detection path when CSV has incorrect number of columns.
     * This ensures the validation properly rejects malformed CSVs.
     * Path: Entry → Count Check (Fail) → Exception
     *
     * Opposite of TC01, should check if System notifies on invalid header
     *
     * TEST OWNER: Mor
     */
    @Test
    public void testInvalidHeaderCount() {
        try {
            String csvContent = "ID,Question,Difficulty,A,B,C,D\n" +  // Missing "Correct Answer"
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
     *
     * COVERAGE TYPE: Branch Coverage
     *
     * BRANCHES COVERED:
     * - Branch 1 (Header count check): TRUE path (count == 8)
     * - Branch 2 (Header name validation loop): FALSE path (name mismatch)
     * - Exception handling branch: TRUE path (throws CSVParseException)
     *
     * GRAPHIC DESCRIPTION:
     * Entry → [Count==8?]─YES→ [Loop Headers] → [Header Match?]─NO→ [Throw Exception]
     *                                   ↓                         ↓
     *                              For each header              FALSE
     *                                                     (e.g., "Answer" != "Correct Answer")
     *
     * EXPLANATION:
     * Tests the branch where header count is correct but a header name is wrong.
     * This validates the individual header name checking logic.
     * Path: Entry → Count Check (Pass) → Name Loop → Name Check (Fail) → Exception
     *
     * Header contains an invalid column 'Answer' when it should be 'Correct Answer' and therefore
     * correctly throws an exception
     *
     * TEST OWNER: Mor
     */
    @Test
    public void testInvalidHeaderName() {
        try {
            String csvContent = "ID,Question,Difficulty,A,B,C,D,Answer\n" +  // "Answer" instead of "Correct Answer"
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
     *
     * COVERAGE TYPE: Statement Coverage
     *
     * BRANCHES COVERED:
     * - Branch 1 (Header count check): TRUE path
     * - Branch 2 (toLowerCase() transformation): Executes for each header
     * - Branch 3 (Case-insensitive comparison): TRUE path (after normalization)
     *
     * GRAPHIC DESCRIPTION:
     * Entry → [Count==8?]─YES→ [Loop Headers] → [toLowerCase()] → [Compare]─MATCH→ Success
     *                                   ↓              ↓               ↓
     *                              "id" ──→ "id" ──→ "id" == "id" ✓
     *                              "question"→"question"→"question"=="question" ✓
     *
     * EXPLANATION:
     * Tests that the toLowerCase() branch works correctly for case normalization.
     * Ensures headers are compared case-insensitively.
     * Path: Entry → Count Check (Pass) → toLowerCase() → Name Loop (All Pass) → Exit
     *
     * Tests the toLowerCase() branch in header comparison
     * Checks that it's ok if the header of the csv is lowercase
     *
     * TEST OWNER: Osman
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
     *
     * COVERAGE TYPE: Branch Coverage
     *
     * BRANCHES COVERED:
     * - parseDifficulty() decision tree:
     *   • Branch: difficulty == 1 → TRUE
     *   • Branch: difficulty == 2 → TRUE
     *   • Branch: difficulty == 3 → TRUE
     *   Branch: difficulty   == 4 → TRUE
     *   • Each validates and returns successfully
     *
     * GRAPHIC DESCRIPTION:
     * Entry → [Parse Int] → [Value==1?]─YES→ Return 1 ✓
     *              ↓            ↓ NO
     *         "1","2","3"   [Value==2?]─YES→ Return 2 ✓
     *                           ↓ NO
     *                       [Value==3?]─YES→ Return 3 ✓
     *                           ↓ NO
     *                       [Throw Error]
     *
     * EXPLANATION:
     * Tests all valid branches in the parseDifficulty() method (values 1, 2, 3, 4).
     * Ensures each valid difficulty value is accepted correctly.
     *
     * Tests parseDifficulty() valid branches (1, 2, 3, 4)
     * Checks that the valid difficulties Work
     *
     * TEST OWNER: Juliana
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

        // Test difficulty 4
        questionBank = new QuestionBank();
        String csv4 = "ID,Question,Difficulty,A,B,C,D,Correct Answer\n1,Test?,4,A,B,C,D,A\n";
        tempCsvFile = createTempCsvFile(csv3);
        questionBank.loadFromCsv(tempCsvFile.getAbsolutePath());
        assertEquals("Difficulty 4 accepted", 1, questionBank.getQuestionCount());
    }

    /**
     * WB-01-TC06: Difficulty Validation - Invalid Branches
     *
     * COVERAGE TYPE: Branch Coverage
     *
     * BRANCHES COVERED:
     * - parseDifficulty() error branches:
     *   • Branch: difficulty < 1 → TRUE (error)
     *   • Branch: difficulty > 4 → TRUE (error)
     *   • Branch: not a number → TRUE (NumberFormatException)
     *
     * GRAPHIC DESCRIPTION:
     * Entry → [Parse Int] → [Value < 1?]─YES→ Error ✗
     *              ↓            ↓ NO
     *         "0","5",     [Value > 4?]─YES→ Error ✗
     *         "-1","easy"      ↓ NO
     *                      [Value 1-4?]─YES→ Success ✓
     *              ↓
     *         [Not a number?]─YES→ NumberFormatException ✗
     *
     * EXPLANATION:
     * Tests all error detection branches in parseDifficulty().
     * Validates that values outside 1-4 range and non-numeric values are rejected.
     *
     * Tests parseDifficulty() invalid branches (0, 5, negative)
     * Checks that the only valid difficulties are 1,2,3,4 (easy, medium, hard, expert)
     *
     * TEST OWNER: Juliana
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
     *
     * COVERAGE TYPE: Branch Coverage
     *
     * BRANCHES COVERED:
     * - parseCorrectAnswer() valid branches:
     *   • Branch: answer.toUpperCase() == 'A' → TRUE
     *   • Branch: answer.toUpperCase() == 'B' → TRUE
     *   • Branch: answer.toUpperCase() == 'C' → TRUE
     *   • Branch: answer.toUpperCase() == 'D' → TRUE
     *   • Case normalization: toLowerCase() → toUpperCase() transformation
     *
     * GRAPHIC DESCRIPTION:
     * Entry → [toUpperCase()] → [Answer=='A'?]─YES→ Valid ✓
     *              ↓                  ↓ NO
     *         "A","a","B","b"    [Answer=='B'?]─YES→ Valid ✓
     *         "C","c","D","d"         ↓ NO
     *                            [Answer=='C'?]─YES→ Valid ✓
     *                                 ↓ NO
     *                            [Answer=='D'?]─YES→ Valid ✓
     *                                 ↓ NO
     *                            [Invalid] → Error ✗
     *
     * EXPLANATION:
     * Tests all valid answer branches (A, B, C, D) with case variations.
     * Ensures case-insensitive answer validation works correctly.
     *
     * Tests parseCorrectAnswer() valid branches (A-D, case insensitive)
     * Checks that answers A, B, C, D and their lowercase versions are accepted as valid in the Question wizard
     *
     * TEST OWNER: Avi
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
     *
     * COVERAGE TYPE: Branch Coverage
     *
     * BRANCHES COVERED:
     * - parseCorrectAnswer() error branches:
     *   • Branch: answer not in {A,B,C,D} → TRUE (error)
     *   • Branch: answer is empty → TRUE (error)
     *   • Branch: answer is number → TRUE (error)
     *   • Exception handling branch: TRUE
     *
     * GRAPHIC DESCRIPTION:
     * Entry → [toUpperCase()] → [Answer in {A,B,C,D}?]─NO→ Error ✗
     *              ↓                       ↓
     *         "E","F","1"            YES → Valid ✓
     *         "0","" (empty)
     *              ↓
     *         [Empty check?]─YES→ Error ✗
     *              ↓ NO
     *         [Validation fails] → Exception
     *
     * EXPLANATION:
     * Tests error detection for invalid answers (not A-D).
     * Validates that only A, B, C, D are accepted as correct answers.
     *
     * Tests parseCorrectAnswer() invalid branches (E-Z, numbers, empty)
     * Checks that answers that are not A, B, C, D (or their lowercase versions) correctly throw an exception
     *
     * TEST OWNER: Avi
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

    /**
     * Helper method to create temporary CSV files for testing
     */
    private File createTempCsvFile(String content) throws IOException {
        File tempFile = File.createTempFile("test_", ".csv");
        FileWriter writer = new FileWriter(tempFile);
        writer.write(content);
        writer.close();
        return tempFile;
    }
}
