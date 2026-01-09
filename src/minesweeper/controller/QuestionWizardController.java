package minesweeper.controller;

import minesweeper.model.Question;
import minesweeper.model.MutableQuestion;
import minesweeper.model.QuestionBank;
import minesweeper.model.QuestionBank.CSVParseException;
import minesweeper.view.QuestionWizardView;
import minesweeper.view.QuestionEditorDialog;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import minesweeper.model.QuestionDifficulty;
import minesweeper.model.QuestionDifficulty;
import minesweeper.model.QuestionDifficulty;
import minesweeper.model.QuestionDifficulty;
import minesweeper.model.QuestionDifficulty;
import minesweeper.model.QuestionDifficulty;
import minesweeper.model.QuestionDifficulty;
import minesweeper.model.QuestionDifficulty;

/**
 * Controller for the Question Wizard screen.
 * Manages the interaction between the QuestionBank model and QuestionWizardView.
 *
 * Responsibilities:
 * - Loading CSV files into the question bank
 * - Updating the view with question data
 * - Handling user actions (upload, reload, back)
 * - Coordinating error display
 */
public class QuestionWizardController {

    private static final String PREF_LAST_CSV_PATH = "lastLoadedCsvPath";
    private static final Preferences prefs = Preferences.userNodeForPackage(QuestionWizardController.class);

    private QuestionBank questionBank;
    private QuestionWizardView view;
    private String lastLoadedFilePath;

    // Callback for navigation back to start
    private Runnable onBackToStart;

    /**
     * Constructs the controller with the given model and view.
     *
     * @param questionBank The question bank model
     * @param view The question wizard view
     */
    public QuestionWizardController(QuestionBank questionBank, QuestionWizardView view) {
        this.questionBank = questionBank;
        this.view = view;
        this.lastLoadedFilePath = null;

        setupEventHandlers();
    }

    private void applySearchAndFilter(String text, QuestionDifficulty difficulty) {
        String query = (text == null) ? "" : text.trim().toLowerCase();

        List<Question> filtered = questionBank.getAllQuestions().stream()
                .filter(q -> query.isEmpty() || q.getText().toLowerCase().contains(query))
                .filter(q -> difficulty == null || q.getDifficulty() == difficulty)
                .toList();

        view.bindQuestions(filtered);    }
    /**
     * Sets up the event handlers for view actions.
     */
    private void setupEventHandlers() {
        // Handle CSV upload
        view.setUploadListener(e -> handleUpload());

        // Handle reload
        view.setReloadListener(e -> reloadQuestions());

        // Handle back to start
        view.setBackListener(e -> handleBackToStart());
        view.setSearchListener(e -> {
            applySearchAndFilter(view.getSearchText(), view.getSelectedDifficulty());
        });

        // Handle CRUD operations
        view.setCreateListener(e -> handleCreate());
        view.setEditListener(e -> handleEdit());
        view.setDeleteListener(e -> handleDelete());
    }

    /**
     * Opens the Question Wizard view.
     * If a CSV path was provided to the QuestionBank, attempts to load it automatically.
     * Otherwise, tries to load from the last saved CSV path in preferences.
     */
    public void open() {
        // Priority 1: Check for saved preferences path (user's last uploaded CSV)
        String savedPath = loadLastCsvPath();

        if (savedPath != null) {
            File savedFile = new File(savedPath);
            if (savedFile.exists()) {
                loadFromFile(savedPath);
            } else if (questionBank.getCsvPath() != null && !questionBank.isLoaded()) {
                // Saved file no longer exists, fall back to default
                loadFromFile(questionBank.getCsvPath());
            }
        } else if (questionBank.getCsvPath() != null && !questionBank.isLoaded()) {
            // No saved preferences, use default path if available
            loadFromFile(questionBank.getCsvPath());
        }

        // Show the view
        SwingUtilities.invokeLater(() -> view.showWindow());
    }

    /**
     * Reloads questions from the last loaded file.
     */
    public void reloadQuestions() {
        if (lastLoadedFilePath != null) {
            loadFromFile(lastLoadedFilePath);
        } else if (questionBank.getCsvPath() != null) {
            loadFromFile(questionBank.getCsvPath());
        }
    }

    /**
     * Handles the upload button action.
     * Opens a file chooser and loads the selected CSV file.
     */
    private void handleUpload() {
        File selectedFile = view.showFileChooser();

        if (selectedFile != null) {
            loadFromFile(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Loads questions from a specified file path.
     *
     * @param filePath Path to the CSV file
     */
    private void loadFromFile(String filePath) {
        try {
            view.hideError();

            File f = new File(filePath);

            if (f.exists() && f.isFile()) {
                // File system file (user uploaded)
                try (FileInputStream fis = new FileInputStream(f)) {
                    questionBank.loadFromInputStream(fis);
                }
            } else {
                // Classpath resource (bundled with app)
                questionBank.loadFromCsv(filePath);
            }

            List<Question> questions = questionBank.getAllQuestions();
            view.bindQuestions(questions);

            lastLoadedFilePath = filePath;

            // Persist the file path so it's remembered on next launch
            saveLastCsvPath(filePath);

            if (questionBank.hasParseErrors()) {
                view.showParseWarnings(questionBank.getParseErrors());
            }

        } catch (CSVParseException | IOException e) {
            view.showLoadError(e.getMessage());
            if (!questionBank.isLoaded()) {
                view.showEmptyState();
            }
        }
    }


    /**
     * Handles the back button action.
     */
    private void handleBackToStart() {
        // Hide the view
        view.hideWindow();


        // Notify the navigation controller if callback is set
        if (onBackToStart != null) {
            onBackToStart.run();
        }
    }

    /**
     * Sets the callback for when user wants to return to start.
     *
     * @param callback The callback to run
     */
    public void setOnBackToStart(Runnable callback) {
        this.onBackToStart = callback;
    }

    /**
     * Gets the question bank managed by this controller.
     *
     * @return The question bank
     */
    public QuestionBank getQuestionBank() {
        return questionBank;
    }
    // --- US-14: Search & Filter ---

    /**
     * Gets the view managed by this controller.
     *
     * @return The view
     */
    public QuestionWizardView getView() {
        return view;
    }

    /**
     * Closes the Question Wizard.
     */
    public void close() {
        view.hideWindow();
        view.dispose();
    }

    // ==================== CRUD Handlers ====================

    /**
     * Handles the create question action.
     * Opens a dialog for creating a new question.
     */
    private void handleCreate() {
        // Create a new mutable question with next available ID
        MutableQuestion newQuestion = new MutableQuestion();
        newQuestion.setId(questionBank.getNextAvailableId());

        // Show editor dialog
        QuestionEditorDialog dialog = new QuestionEditorDialog(
                (java.awt.Frame) view, newQuestion);
        dialog.setVisible(true);

        // If user confirmed, add the question
        if (dialog.isConfirmed()) {
            try {
                Question question = newQuestion.toQuestion();
                questionBank.addQuestion(question);

                // Save to file if we have a file path
                if (lastLoadedFilePath != null) {
                    saveQuestionBank();
                }

                // Refresh the view
                refreshView();

                JOptionPane.showMessageDialog(view,
                        "Question created successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(view,
                        "Error creating question: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the edit question action.
     * Opens a dialog for editing the selected question.
     */
    private void handleEdit() {
        Question selected = view.getSelectedQuestion();
        if (selected == null) {
            JOptionPane.showMessageDialog(view,
                    "Please select a question to edit.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create mutable copy for editing
        MutableQuestion mutableQuestion = new MutableQuestion(selected);

        // Show editor dialog
        QuestionEditorDialog dialog = new QuestionEditorDialog(
                (java.awt.Frame) view, mutableQuestion);
        dialog.setVisible(true);

        // If user confirmed, update the question
        if (dialog.isConfirmed()) {
            try {
                Question updated = mutableQuestion.toQuestion();
                questionBank.updateQuestion(updated);

                // Save to file if we have a file path
                if (lastLoadedFilePath != null) {
                    saveQuestionBank();
                }

                // Refresh the view
                refreshView();

                JOptionPane.showMessageDialog(view,
                        "Question updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(view,
                        "Error updating question: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the delete question action.
     * Deletes the selected question after confirmation.
     */
    private void handleDelete() {
        Question selected = view.getSelectedQuestion();
        if (selected == null) {
            JOptionPane.showMessageDialog(view,
                    "Please select a question to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(view,
                String.format("Are you sure you want to delete question #%d?\n\n%s",
                        selected.getId(),
                        selected.getText()),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                questionBank.deleteQuestion(selected.getId());

                // Save to file if we have a file path
                if (lastLoadedFilePath != null) {
                    saveQuestionBank();
                }

                // Refresh the view
                refreshView();

                JOptionPane.showMessageDialog(view,
                        "Question deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(view,
                        "Error deleting question: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Saves the question bank to the last loaded file.
     */
    private void saveQuestionBank() {
        if (lastLoadedFilePath == null) {
            JOptionPane.showMessageDialog(view,
                    "No file path available for saving.",
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            questionBank.saveToFile(lastLoadedFilePath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view,
                    "Error saving to file: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes the view with current question bank data.
     * Reapplies any active search/filter.
     */
    private void refreshView() {
        String searchText = view.getSearchText();
        QuestionDifficulty difficulty = view.getSelectedDifficulty();

        if (searchText != null && !searchText.trim().isEmpty() || difficulty != null) {
            // Reapply search/filter
            applySearchAndFilter(searchText, difficulty);
        } else {
            // Show all questions
            view.bindQuestions(questionBank.getAllQuestions());
        }
    }

    // ==================== Preferences Persistence ====================

    /**
     * Saves the last loaded CSV file path to preferences.
     * This allows the app to remember which file was loaded on next launch.
     *
     * @param filePath The file path to save
     */
    private void saveLastCsvPath(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            prefs.put(PREF_LAST_CSV_PATH, filePath);
            try {
                prefs.flush();
            } catch (BackingStoreException e) {
                // Silently fail - preferences will be lost on restart
            }
        }
    }

    /**
     * Loads the last saved CSV file path from preferences.
     *
     * @return The saved file path, or null if none was saved
     */
    private String loadLastCsvPath() {
        return prefs.get(PREF_LAST_CSV_PATH, null);
    }

}