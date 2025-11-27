package minesweeper.controller;
import minesweeper.model.QuestionBank;
import minesweeper.view.QuestionWizardView;
/**
 * Simple launcher class for the Question Wizard.
 * Call QuestionWizardLauncher.launch() to open the Question Wizard from anywhere.
 */
public class QuestionWizardLauncher {

    private static QuestionWizardController currentController = null;

    /**
     * Launches the Question Wizard.
     *
     * @param onClose Optional callback to run when the wizard is closed (can be null)
     */
    public static void launch(Runnable onClose) {
        // Create MVC components
        QuestionBank questionBank = new QuestionBank();
        QuestionWizardView view = new QuestionWizardView();
        QuestionWizardController controller = new QuestionWizardController(questionBank, view);

        // Store reference
        currentController = controller;

        // Set up close callback
        controller.setOnBackToStart(() -> {
            view.dispose();
            currentController = null;
            if (onClose != null) {
                onClose.run();
            }
        });

        // Open the wizard
        controller.open();
    }

    /**
     * Launches the Question Wizard with no callback.
     */
    public static void launch() {
        launch(null);
    }

    /**
     * Checks if the Question Wizard is currently open.
     */
    public static boolean isOpen() {
        return currentController != null;
    }

    /**
     * Closes the Question Wizard if it's open.
     */
    public static void close() {
        if (currentController != null) {
            currentController.close();
            currentController = null;
        }
    }
}