package minesweeper;

import minesweeper.controller.QuestionWizardController;
import minesweeper.controller.QuestionBankController;
import minesweeper.view.QuestionWizardView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main entry point for the Question Wizard application.
 * This class demonstrates the standalone Question Wizard functionality
 * for Iteration 1 of the Minesweeper Quiz Game project.
 *gfgfgfgfg
 * Usage:
 *   java main.QuestionWizardApp [csv_file_path]
 *
 * If a CSV file path is provided, it will be loaded automatically.
 * Otherwise, the user can upload a file through the UI.
 */
public class QuestionWizardApp {

    public static void main(String[] args) {
        // Set system look and feel for better UI appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
            System.out.println("Using default look and feel");
        }

        // Get optional CSV file path from command line
        String csvPath = null;
        if (args.length > 0) {
            csvPath = args[0];
            System.out.println("Loading CSV file: " + csvPath);
        }

        // Launch the application on the Event Dispatch Thread
        final String finalCsvPath = csvPath;
        SwingUtilities.invokeLater(() -> {
            launchQuestionWizard(finalCsvPath);
        });
    }

    /**
     * Launches the Question Wizard with MVC architecture.
     *
     * @param csvPath Optional path to a CSV file to load automatically
     */
    private static void launchQuestionWizard(String csvPath) {
        // Create Model
        QuestionBankController questionBank;
        if (csvPath != null) {
            questionBank = new QuestionBankController(csvPath);
        } else {
            questionBank = new QuestionBankController();
        }

        // Create View
        QuestionWizardView view = new QuestionWizardView();

        // Create Controller (connects Model and View)
        QuestionWizardController controller = new QuestionWizardController(questionBank, view);

        // Set up "Back to Start" callback
        // In the full application, this would navigate to the StartMenuView
        // For this standalone demo, we exit the application
        controller.setOnBackToStart(() -> {
            System.out.println("Returning to Start Menu...");
            System.out.println("(In full app, this navigates to StartMenuView)");
            view.dispose();
            System.exit(0);
        });

        // Open the Question Wizard
        controller.open();

        System.out.println("Question Wizard launched successfully!");
        System.out.println("- Click 'Upload CSV' to load a questions file");
        System.out.println("- Click column headers to sort");
        System.out.println("- Click 'Back to Start' to exit");
    }
}
