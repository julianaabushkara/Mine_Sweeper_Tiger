# Question Wizard - Iteration 1

## Overview

The Question Wizard is a component of the Minesweeper Quiz Game that allows content editors to manage the question bank. This iteration (US-05) implements read-only CSV loading and display functionality.

## User Story

**US-05: Question Wizard (Read-Only)**

> As a content editor, I want to open the Question Wizard and view the questions loaded from a CSV, so that I can verify the question bank early.

> As a content editor, I want to be able to upload a CSV to the question bank.

## Features Implemented

### T1. CSV Schema Definition ✓
- **ID**: Unique positive integer identifier
- **Question**: The quiz question text (required)
- **Difficulty**: Integer value (1=Easy, 2=Medium, 3=Hard)
- **A, B, C, D**: Four answer options (all required)
- **Correct Answer**: Letter indicating correct option (A/B/C/D)

### T2. CSV Loading Function ✓
- Robust CSV parsing with comprehensive error handling
- Supports quoted fields with commas
- Handles UTF-8 BOM (Byte Order Mark)
- Validates header format
- Reports malformed rows while continuing to parse valid data

### T3. Read-Only Data Table ✓
- Displays all loaded questions in a sortable table
- Column sorting (click headers)
- Proper column widths for readability
- Read-only - no cell editing allowed

### T4. User Interface Feedback ✓
- **Empty State**: Clear message when no file is loaded with upload button
- **Error Banner**: Visible error message for invalid/malformed files
- **Status Label**: Shows loaded question count or error state
- **Parse Warnings**: Dialog showing details of skipped rows

### T5. Unit Tests ✓
- Tests for valid CSV parsing
- Tests for empty files
- Tests for invalid headers
- Tests for malformed rows
- Tests for invalid data types (ID, Difficulty, Correct Answer)
- Tests for edge cases (BOM, quoted fields)

### T6. Smoke JunitTest ✓
- Sample CSV file provided
- JunitTest file with intentional errors provided
- "Back to Start" navigation functional

## Project Structure

```
question-wizard/
├── src/
│   ├── model/
│   │   ├── QuestionDifficulty.java      # Enum for question difficulty
│   │   ├── Question.java        # Question data class
│   │   └── QuestionBank.java    # CSV parser and question storage
│   ├── view/
│   │   └── QuestionWizardView.java  # GUI component
│   ├── controller/
│   │   └── QuestionWizardController.java  # MVC controller
│   ├── test/
│   │   └── QuestionBankTest.java    # Unit tests
│   └── main/
│       └── QuestionWizardApp.java   # Application entry point
├── resources/
│   ├── sample_questions.csv     # Valid sample data (20 questions)
│   └── test_with_errors.csv     # JunitTest file with intentional errors
├── build.sh                     # Build and run script
└── README.md                    # This file
```

## Architecture

The application follows the **MVC (Model-View-Controller)** pattern as specified in the class diagram:

### Model Layer
- **QuestionDifficulty**: Enum with EASY, MEDIUM, HARD values
- **Question**: Immutable data class representing a single question
- **QuestionBank**: Manages question collection and CSV parsing

### View Layer
- **QuestionWizardView**: Swing-based GUI with table, buttons, and error display

### Controller Layer
- **QuestionWizardController**: Coordinates between model and view, handles user actions

## CSV Format

```csv
ID,Question,Difficulty,A,B,C,D,Correct Answer
1,What is the capital of France?,1,London,Paris,Berlin,Madrid,B
2,Which planet is the Red Planet?,2,Venus,Earth,Mars,Jupiter,C
```

### Field Requirements

| Field | Type | Constraints |
|-------|------|-------------|
| ID | Integer | Positive (> 0) |
| Question | String | Non-empty |
| Difficulty | Integer | 1, 2, or 3 |
| A, B, C, D | String | Non-empty |
| Correct Answer | Character | A, B, C, or D |

## Building and Running

### Prerequisites
- Java JDK 8 or higher
- Swing library (included in JDK)

### Using the Build Script

```bash
# Make script executable
chmod +x build.sh

# Compile the project
./build.sh compile

# Run unit tests
./build.sh test

# Run the application
./build.sh run

# Run with sample data
./build.sh demo

# Run with specific CSV file
./build.sh run /path/to/questions.csv

# Clean build artifacts
./build.sh clean
```

### Manual Compilation

```bash
# Create output directory
mkdir -p out

# Compile all Java files
javac -d out -sourcepath src $(find src -name "*.java")

# Run tests
cd out && java test.QuestionBankTest && cd ..

# Run application
cd out && java main.QuestionWizardApp && cd ..

# Run with CSV file
cd out && java main.QuestionWizardApp ../resources/sample_questions.csv && cd ..
```

## Usage

1. **Launch the application** - Opens with empty state
2. **Click "Upload CSV"** - Opens file chooser dialog
3. **Select a CSV file** - Questions are loaded and displayed
4. **Sort columns** - Click column headers to sort
5. **View errors** - Error banner shows for invalid files
6. **Reload** - Click "Reload" to refresh from same file
7. **Back to Start** - Returns to main menu (exits in standalone mode)

## Error Handling

The parser provides detailed error messages for:
- File not found
- Empty files
- Invalid header format
- Wrong column count
- Invalid ID (non-numeric, negative, zero)
- Invalid difficulty (not 1/2/3)
- Invalid correct answer (not A/B/C/D)
- Empty required fields

**Graceful Degradation**: When some rows are invalid, valid rows are still loaded and displayed. A warning dialog shows which rows were skipped.

## Integration with Main Application

To integrate with the full Minesweeper Quiz Game:

```java
// In NavigationController.java
public void openQuestionWizard() {
    QuestionBank questionBank = new QuestionBank();
    QuestionWizardView view = new QuestionWizardView();
    QuestionWizardController controller = new QuestionWizardController(questionBank, view);
    
    controller.setOnBackToStart(() -> {
        view.dispose();
        showStartMenu();  // Your navigation method
    });
    
    controller.open();
}
```

## Testing

### Unit JunitTest Coverage

| JunitTest | Description | Status |
|------|-------------|--------|
| testValidCSVParsing | Parse well-formed CSV | ✓ |
| testEmptyFile | Handle empty file | ✓ |
| testMissingHeaders | Detect missing columns | ✓ |
| testInvalidHeaders | Detect wrong header names | ✓ |
| testMalformedRows | Handle wrong column count | ✓ |
| testInvalidID | Detect bad ID values | ✓ |
| testInvalidDifficulty | Detect bad difficulty | ✓ |
| testInvalidCorrectAnswer | Detect bad answers | ✓ |
| testEmptyRequiredFields | Detect empty fields | ✓ |
| testMixedValidAndInvalidRows | Graceful degradation | ✓ |
| testBOMHandling | Handle UTF-8 BOM | ✓ |
| testQuotedFieldsWithCommas | Parse quoted commas | ✓ |
| testFileNotFound | Handle missing file | ✓ |
| testDifficultyEnum | Enum conversion | ✓ |
| testQuestionClass | Question methods | ✓ |

### Running Tests

```bash
./build.sh test
```

Expected output:
```
========================================
  QuestionBank Unit Tests
========================================

✓ PASS: Valid CSV Parsing
✓ PASS: Empty File Handling
✓ PASS: Missing Headers
...

========================================
  JunitTest Summary
========================================
Total tests:  15
Passed:       15
Failed:       0
========================================
```

## Risk Mitigation (R-05)

As identified in the SDP, the following risks are mitigated:

| Risk | Mitigation |
|------|------------|
| CSV parsing errors crash wizard | Strong validation with try-catch; graceful degradation |
| Invalid data corrupts system | Validation on all fields before creating Question objects |
| User doesn't understand errors | Clear error messages with row numbers and field names |
| Large files cause performance issues | Efficient parsing; table virtualization ready |

## Team

- **Owner**: Yotam
- **Iteration**: 1
- **Sprint**: US-05 Question Wizard (Read-Only)

## Definition of Done

- [x] Logic implemented
- [x] Code follows MVC structure
- [x] Unit tests exist and pass
- [x] UI verified (empty state, error banner, table)
- [x] Documentation updated
- [x] Ready for sprint review demonstration
