package minesweeper.controller;

import minesweeper.model.Question;
import minesweeper.model.QuestionBank;
import minesweeper.model.QuestionBank.CSVParseException;
import minesweeper.view.QuestionWizardView;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
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
    }

    /**
     * Opens the Question Wizard view.
     * If a CSV path was provided to the QuestionBank, attempts to load it automatically.
     */
    public void open() {
        // Try to auto-load if a path was provided
        if (questionBank.getCsvPath() != null && !questionBank.isLoaded()) {
            loadFromFile(questionBank.getCsvPath());
        }

        // Show the view
        SwingUtilities.invokeLater(() -> view.showWindow());    }

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

}