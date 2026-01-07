package minesweeper;

import minesweeper.model.*;
import minesweeper.model.factory.BoardFactory;
import minesweeper.model.scoring.*;
import minesweeper.observer.BoardObserver;

/**
 * ===================================================================
 * DESIGN PATTERNS DEMONSTRATION
 * ===================================================================
 * This class demonstrates the three design patterns implemented in
 * the Minesweeper game:
 *
 * 1. STRATEGY PATTERN - Scoring rules vary by difficulty
 * 2. FACTORY PATTERN  - Board and strategy creation
 * 3. OBSERVER PATTERN - Model notifies UI of changes
 * ===================================================================
 */
public class DesignPatternsDemo {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           MINESWEEPER DESIGN PATTERNS DEMO                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        demonstrateFactoryPattern();
        demonstrateStrategyPattern();
        demonstrateObserverPattern();
    }

    /**
     * FACTORY PATTERN DEMONSTRATION
     * ─────────────────────────────────────────────────────────────
     * The BoardFactory creates game objects based on difficulty:
     * - Boards are created with difficulty-specific parameters
     * - Scoring strategies are matched to the difficulty level
     */
    private static void demonstrateFactoryPattern() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  1. FACTORY PATTERN - BoardFactory                          │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println("  Purpose: Centralize creation of boards and scoring strategies\n");

        // Factory creates boards based on difficulty
        Board easyBoard = BoardFactory.createBoard(GameSession.Difficulty.EASY);
        Board hardBoard = BoardFactory.createBoard(GameSession.Difficulty.HARD);

        System.out.println("  BoardFactory.createBoard(EASY):");
        System.out.println("    → Grid: " + easyBoard.getSize() + "x" + easyBoard.getSize());
        System.out.println("    → Mines: " + easyBoard.getTotalMines());

        System.out.println("\n  BoardFactory.createBoard(HARD):");
        System.out.println("    → Grid: " + hardBoard.getSize() + "x" + hardBoard.getSize());
        System.out.println("    → Mines: " + hardBoard.getTotalMines());

        // Factory also creates appropriate scoring strategies
        ScoringStrategy easyStrategy = BoardFactory.createScoringStrategy(GameSession.Difficulty.EASY);
        ScoringStrategy hardStrategy = BoardFactory.createScoringStrategy(GameSession.Difficulty.HARD);

        System.out.println("\n  BoardFactory.createScoringStrategy(EASY):");
        System.out.println("    → Type: " + easyStrategy.getClass().getSimpleName());
        System.out.println("    → Activation Cost: " + easyStrategy.getActivationCost() + " points");

        System.out.println("\n  BoardFactory.createScoringStrategy(HARD):");
        System.out.println("    → Type: " + hardStrategy.getClass().getSimpleName());
        System.out.println("    → Activation Cost: " + hardStrategy.getActivationCost() + " points");

        System.out.println("\n");
    }

    /**
     * STRATEGY PATTERN DEMONSTRATION
     * ─────────────────────────────────────────────────────────────
     * Different ScoringStrategy implementations provide different
     * scoring rules. The game doesn't need to know which strategy
     * is being used - it just calls the interface methods.
     */
    private static void demonstrateStrategyPattern() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  2. STRATEGY PATTERN - ScoringStrategy                      │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println("  Purpose: Encapsulate scoring algorithms that vary by difficulty\n");

        // Create strategies for each difficulty
        ScoringStrategy[] strategies = {
            new EasyScoringStrategy(),
            new MediumScoringStrategy(),
            new HardScoringStrategy()
        };

        System.out.println("  Comparing EXPERT question reward across difficulties:");
        System.out.println("  ┌─────────────┬─────────────────┬─────────────────┐");
        System.out.println("  │  Difficulty │ Correct Answer  │  Wrong Answer   │");
        System.out.println("  ├─────────────┼─────────────────┼─────────────────┤");

        for (ScoringStrategy strategy : strategies) {
            ScoreResult correct = strategy.calculateQuestionReward(QuestionDifficulty.EXPERT, true);
            ScoreResult wrong = strategy.calculateQuestionReward(QuestionDifficulty.EXPERT, false);

            String name = strategy.getClass().getSimpleName().replace("ScoringStrategy", "");
            System.out.printf("  │  %-9s │ %+3d pts, %+d life │ %+3d pts, %d life │%n",
                name.toUpperCase(),
                correct.getPoints(), correct.getLives(),
                wrong.getPoints(), wrong.getLives());
        }
        System.out.println("  └─────────────┴─────────────────┴─────────────────┘");

        System.out.println("\n  Surprise tile rewards (random good outcome):");
        for (ScoringStrategy strategy : strategies) {
            ScoreResult result = strategy.calculateSurpriseReward(true);
            String name = strategy.getClass().getSimpleName().replace("ScoringStrategy", "");
            System.out.printf("    %s: %+d points, %+d life%n", name, result.getPoints(), result.getLives());
        }

        System.out.println("\n");
    }

    /**
     * OBSERVER PATTERN DEMONSTRATION
     * ─────────────────────────────────────────────────────────────
     * The Board maintains a list of observers (BoardObserver).
     * When a cell is revealed, all observers are automatically
     * notified so they can update their views.
     */
    private static void demonstrateObserverPattern() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│  3. OBSERVER PATTERN - BoardObserver                        │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        System.out.println("  Purpose: Decouple model changes from UI updates\n");

        // Create a board using the factory
        Board board = BoardFactory.createBoard(GameSession.Difficulty.EASY);

        // Create a demo observer
        BoardObserver demoObserver = new BoardObserver() {
            @Override
            public void onCellRevealed(int row, int col, Cell cell) {
                System.out.println("    [Observer] Cell revealed at (" + row + "," + col +
                    ") - Type: " + cell.getType());
            }

            @Override
            public void onGameOver(boolean won) {
                System.out.println("    [Observer] Game Over! Won: " + won);
            }

            @Override
            public void onScoreChanged(int newScore) {
                System.out.println("    [Observer] Score updated to: " + newScore);
            }
        };

        // Register the observer with the board
        board.addObserver(demoObserver);
        System.out.println("  1. Observer registered with board.addObserver(observer)\n");

        // Reveal some cells - observers will be automatically notified
        System.out.println("  2. Calling board.revealCell(0, 0):");
        board.revealCell(0, 0);

        System.out.println("\n  3. Calling board.revealCell(1, 1):");
        board.revealCell(1, 1);

        System.out.println("\n  4. Calling board.notifyScoreChanged(50):");
        board.notifyScoreChanged(50);

        System.out.println("\n  5. Calling board.notifyGameOver(true):");
        board.notifyGameOver(true);

        System.out.println("\n  The observer receives all notifications automatically!");
        System.out.println("  In the real game, GameUIObserver updates the Swing UI.\n");

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    DEMO COMPLETE                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}
