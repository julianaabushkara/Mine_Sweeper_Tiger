
package minesweeper.view;

import minesweeper.model.*;
import minesweeper.controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Queue;
import java.util.LinkedList;


public class MinesweeperGame extends JFrame {
    private GameSession gameSession;
    private JPanel topPanel;
    private JPanel boardsPanel;
    private BoardPanel playerABoard;
    private BoardPanel playerBBoard;
    private JLabel turnLabel;
    private JLabel livesLabel;
    private JLabel scoreLabel;
    private JLabel minesLabel;
    private JLabel timerLabel;
    private javax.swing.Timer gameTimer;
    private int elapsedSeconds;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MinesweeperGame game = new MinesweeperGame();
            game.setVisible(true);
        });
    }

    public MinesweeperGame() {
        setTitle("Minesweeper Boards");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(15, 20, 25));

        // Initialize game session with EASY difficulty
        gameSession = new GameSession(GameSession.Difficulty.EASY);
        elapsedSeconds = 0;

        // Create UI components
        createTopPanel();
        createBoardsPanel();
        createBottomPanel();

        pack();
        setLocationRelativeTo(null);
        startTimer();
    }

    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setBackground(new Color(15, 20, 25));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 200), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Left: Turn info
        turnLabel = createStyledLabel("CURRENT\nTURN: PLAYER A", 18, Color.WHITE);
        turnLabel.setPreferredSize(new Dimension(200, 80));

        // Center: Lives and Score
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        centerPanel.setBackground(new Color(15, 20, 25));
        livesLabel = createStyledLabel("LIVES: " + getHeartsString(gameSession.getSharedLives()), 16, Color.WHITE);
        scoreLabel = createStyledLabel("SCORE: 0000 (EASY)", 16, new Color(0, 255, 100));
        centerPanel.add(livesLabel);
        centerPanel.add(scoreLabel);

        // Right: Mines count and timer
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        rightPanel.setBackground(new Color(15, 20, 25));
        int totalMines = gameSession.getDifficulty().mines * 2;
        minesLabel = createStyledLabel("MINES:\n" + totalMines, 16, new Color(255, 80, 80));
        timerLabel = createStyledLabel("00:00", 20, new Color(0, 200, 255));
        rightPanel.add(minesLabel);
        rightPanel.add(timerLabel);

        topPanel.add(turnLabel, BorderLayout.WEST);
        topPanel.add(centerPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private String getHeartsString(int lives) {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < lives; i++) {
            hearts.append("💖 ");
        }
        return hearts.toString().trim();
    }

    private void createBoardsPanel() {
        boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBackground(new Color(15, 20, 25));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        playerABoard = new BoardPanel("PLAYER A BOARD", gameSession.getPlayerABoard(), 
                                     new Color(100, 50, 150), true);
        playerBBoard = new BoardPanel("PLAYER B BOARD", gameSession.getPlayerBBoard(), 
                                     new Color(0, 150, 200), false);

        boardsPanel.add(playerABoard);
        boardsPanel.add(playerBBoard);

        add(boardsPanel, BorderLayout.CENTER);
    }

    private void switchTurn() {
        gameSession.switchTurn();
        updateGameDisplay();
        checkGameEnd();
    }

    private void checkGameEnd() {
        if (gameSession.getSharedLives() <= 0) {
            gameTimer.stop();
            endGame(false);
            return;
        }

        if (gameSession.getPlayerABoard().getRevealedMines() >= 
            gameSession.getPlayerABoard().getTotalMines()) {
            gameTimer.stop();
            endGame(true);
            return;
        }

        if (gameSession.getPlayerBBoard().getRevealedMines() >= 
            gameSession.getPlayerBBoard().getTotalMines()) {
            gameTimer.stop();
            endGame(true);
            return;
        }
    }

    private void endGame(boolean victory) {
        int lifeBonus = gameSession.getSharedLives() * gameSession.getDifficulty().activationCost;
        gameSession.addScore(lifeBonus);

        revealAllTiles(gameSession.getPlayerABoard(), playerABoard);
        revealAllTiles(gameSession.getPlayerBBoard(), playerBBoard);

        updateGameDisplay();

        String message = victory ? 
            "🎉 VICTORY! 🎉\n\n" +
            "All mines discovered!\n" +
            "Remaining Lives Bonus: " + gameSession.getSharedLives() + " × " + 
            gameSession.getDifficulty().activationCost + " = +" + lifeBonus + " points\n\n" +
            "FINAL SCORE: " + gameSession.getSharedScore() :
            "💔 DEFEAT 💔\n\n" +
            "Lives reached 0!\n\n" +
            "FINAL SCORE: " + gameSession.getSharedScore();

        JOptionPane.showMessageDialog(this, message, 
            victory ? "You Win!" : "Game Over",
            victory ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void revealAllTiles(Board board, BoardPanel boardPanel) {
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Cell cell = board.getCell(row, col);
                if (!cell.isRevealed()) {
                    cell.setRevealed(true);
                    boardPanel.getButton(row, col).updateDisplay();
                }
            }
        }
    }

    private void addScore(int points) {
        gameSession.addScore(points);
        updateGameDisplay();
    }

    private void addLives(int lives) {
        gameSession.addLives(lives);
        if (gameSession.getSharedLives() > 10) {
            int extraLives = gameSession.getSharedLives() - 10;
            gameSession.setSharedLives(10);
            // FIX: Calculate bonus for EACH extra live
            int bonusPoints = extraLives * gameSession.getDifficulty().activationCost;
            addScore(bonusPoints);
            JOptionPane.showMessageDialog(this, 
                "Maximum lives reached!\n" + extraLives + " extra lives × " + 
                gameSession.getDifficulty().activationCost + " = +" + bonusPoints + " bonus points.",
                "Bonus", JOptionPane.INFORMATION_MESSAGE);
        }
        updateGameDisplay();
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(15, 20, 25));

        JButton backButton = createGameButton("BACK TO MENU", new Color(120, 60, 180));
        JButton helpButton = createGameButton("HELP", new Color(0, 150, 200));

        backButton.addActionListener(e -> showMenu());
        helpButton.addActionListener(e -> showHelp());

        bottomPanel.add(backButton);
        bottomPanel.add(helpButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createGameButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Monospaced", Font.BOLD, 16));
        button.setForeground(color);
        button.setBackground(new Color(20, 25, 30));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel createStyledLabel(String text, int fontSize, Color color) {
        JLabel label = new JLabel("<html>" + text.replace("\n", "<br>") + "</html>");
        label.setFont(new Font("Monospaced", Font.BOLD, fontSize));
        label.setForeground(color);
        return label;
    }

    private void startTimer() {
        gameTimer = new javax.swing.Timer(1000, e -> {
            elapsedSeconds++;
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });
        gameTimer.start();
    }

    private void showMenu() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Return to main menu?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void showHelp() {
        String helpText = "MINESWEEPER BOARDS - COOPERATIVE GAME\n\n" +
            "• Two players share lives and score\n" +
            "• Left click: Reveal tile\n" +
            "• Right click: Flag tile\n" +
            "• Question tiles: Answer questions for bonuses\n" +
            "• Surprise tiles: Random rewards/penalties\n" +
            "• Goal: Reveal all mines without losing all lives";

        JOptionPane.showMessageDialog(this, helpText, "Help", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateGameDisplay() {
        String currentPlayer = gameSession.isPlayerATurn() ? "PLAYER A" : "PLAYER B";
        turnLabel.setText("<html>CURRENT<br>TURN: " + currentPlayer + "</html>");
        livesLabel.setText("LIVES: " + getHeartsString(gameSession.getSharedLives()));
        scoreLabel.setText(String.format("SCORE: %04d (%s)", 
            gameSession.getSharedScore(), gameSession.getDifficulty().name()));

        int totalMines = gameSession.getPlayerABoard().getTotalMines() + 
                        gameSession.getPlayerBBoard().getTotalMines();
        int revealedMines = gameSession.getPlayerABoard().getRevealedMines() + 
                           gameSession.getPlayerBBoard().getRevealedMines();
        minesLabel.setText(String.format("<html>MINES:<br>%d</html>", 
                                        totalMines - revealedMines));
    }

    // Inner class for board panel
    class BoardPanel extends JPanel {
        private String title;
        private Board board;
        private Color themeColor;
        private CellButton[][] buttons;
        private boolean isPlayerA;

        public BoardPanel(String title, Board board, Color themeColor, boolean isPlayerA) {
            this.title = title;
            this.board = board;
            this.themeColor = themeColor;
            this.isPlayerA = isPlayerA;

            setLayout(new BorderLayout(5, 5));
            setBackground(new Color(15, 20, 25));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeColor, 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            createHeader();
            createGrid();
        }

        private void createHeader() {
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            headerPanel.setBackground(new Color(20, 25, 30));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
            titleLabel.setForeground(themeColor);

            JLabel[] legends = {
                createLegend("1", "Number", new Color(100, 150, 255)),
                createLegend("🚩", "Flag", new Color(255, 80, 80)),
                createLegend("?", "Question", new Color(255, 200, 50)),
                createLegend("🎁", "Surprise", new Color(200, 100, 255))
            };

            headerPanel.add(titleLabel);
            for (JLabel legend : legends) {
                headerPanel.add(legend);
            }

            add(headerPanel, BorderLayout.NORTH);
        }

        private JLabel createLegend(String symbol, String text, Color color) {
            JLabel label = new JLabel(symbol + " " + text);
            label.setFont(new Font("Monospaced", Font.PLAIN, 12));
            label.setForeground(color);
            return label;
        }

        private void createGrid() {
            JPanel gridPanel = new JPanel(new GridLayout(board.getSize(), 
                                                         board.getSize(), 2, 2));
            gridPanel.setBackground(new Color(10, 15, 20));
            gridPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            buttons = new CellButton[board.getSize()][board.getSize()];
            for (int row = 0; row < board.getSize(); row++) {
                for (int col = 0; col < board.getSize(); col++) {
                    Cell cell = board.getCell(row, col);
                    CellButton button = new CellButton(cell, row, col, board, isPlayerA);
                    buttons[row][col] = button;
                    gridPanel.add(button);
                }
            }

            add(gridPanel, BorderLayout.CENTER);
        }

        public CellButton getButton(int row, int col) {
            return buttons[row][col];
        }
    }

    // Inner class for cell buttons
    class CellButton extends JButton {
        private Cell cell;
        private int row, col;
        private Board board;
        private boolean isPlayerA;

        public CellButton(Cell cell, int row, int col, Board board, boolean isPlayerA) {
            this.cell = cell;
            this.row = row;
            this.col = col;
            this.board = board;
            this.isPlayerA = isPlayerA;

            setPreferredSize(new Dimension(45, 45));
            setFont(new Font("Monospaced", Font.BOLD, 16));
            setFocusPainted(false);
            setBorder(BorderFactory.createLineBorder(new Color(50, 70, 90), 1));
            updateDisplay();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (gameSession.isPlayerATurn() != isPlayerA) {
                        JOptionPane.showMessageDialog(MinesweeperGame.this, 
                            "It's not your turn!", "Invalid Action", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        handleLeftClick();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        handleRightClick();
                    }
                }
            });
        }

        private void handleLeftClick() {
            if (!cell.isRevealed() && !cell.isFlagged()) {
                cell.setRevealed(true);

                switch (cell.getType()) {
                    case MINE:
                        addLives(-1);
                        board.incrementRevealedMines();
                        JOptionPane.showMessageDialog(MinesweeperGame.this, 
                            "💣 Mine discovered! -1 life\nMines discovered: " + 
                            board.getRevealedMines() + "/" + board.getTotalMines(),
                            "Mine!", JOptionPane.WARNING_MESSAGE);
                        break;
                    case NUMBER:
                        addScore(1);
                        break;
                    case EMPTY:
                        addScore(1);
                        performCascade(row, col);
                        break;
                    case QUESTION:
                    case SURPRISE:
                        addScore(1);
                        // FIX: Cascade for Question and Surprise tiles
                        performSpecialCascade(row, col);
                        break;
                }

                updateDisplay();
                updateGameDisplay();
                switchTurn();
            } else if (cell.isRevealed() && !cell.isUsed() && !cell.isFlagged()) {
                if (cell.getType() == Cell.CellType.QUESTION) {
                    activateQuestionTile();
                } else if (cell.getType() == Cell.CellType.SURPRISE) {
                    activateSurpriseTile();
                }
            }
        }

        private void handleRightClick() {
            if (!cell.isRevealed() && !cell.isFlagged()) {
                cell.setFlagged(true);
                cell.setRevealed(true);

                if (cell.getType() == Cell.CellType.MINE) {
                    addScore(1);
                    board.incrementRevealedMines();
                    JOptionPane.showMessageDialog(MinesweeperGame.this, 
                        "✓ Correct flag! +1 point\nMines discovered: " + 
                        board.getRevealedMines() + "/" + board.getTotalMines(),
                        "Good Job!", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    addScore(-3);
                    JOptionPane.showMessageDialog(MinesweeperGame.this, 
                        "✗ Wrong flag! -3 points", "Penalty", 
                        JOptionPane.ERROR_MESSAGE);
                }

                updateDisplay();
                updateGameDisplay();
                switchTurn();
            }
        }

        private void performCascade(int startRow, int startCol) {
            Queue<int[]> queue = new LinkedList<>();
            boolean[][] visited = new boolean[board.getSize()][board.getSize()];

            queue.offer(new int[]{startRow, startCol});
            visited[startRow][startCol] = true;

            while (!queue.isEmpty()) {
                int[] pos = queue.poll();
                int r = pos[0];
                int c = pos[1];

                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue;

                        int newRow = r + dr;
                        int newCol = c + dc;

                        if (board.isValid(newRow, newCol) && !visited[newRow][newCol]) {
                            visited[newRow][newCol] = true;
                            Cell neighbor = board.getCell(newRow, newCol);

                            if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                                neighbor.setRevealed(true);
                                addScore(1);

                                // Continue cascade for Empty, Question, and Surprise tiles
                                // Stop ONLY at Number tiles (and Mines)
                                if (neighbor.getType() == Cell.CellType.EMPTY ||
                                    neighbor.getType() == Cell.CellType.QUESTION ||
                                    neighbor.getType() == Cell.CellType.SURPRISE) {
                                    queue.offer(new int[]{newRow, newCol});
                                }
                            }
                        }
                    }
                }
            }

            BoardPanel boardPanel = isPlayerA ? playerABoard : playerBBoard;
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    boardPanel.getButton(i, j).updateDisplay();
                }
            }
        }

        // Cascade for Question and Surprise tiles - continues through Question/Surprise, stops at Numbers
        private void performSpecialCascade(int startRow, int startCol) {
            Queue<int[]> queue = new LinkedList<>();
            boolean[][] visited = new boolean[board.getSize()][board.getSize()];

            queue.offer(new int[]{startRow, startCol});
            visited[startRow][startCol] = true;

            while (!queue.isEmpty()) {
                int[] pos = queue.poll();
                int r = pos[0];
                int c = pos[1];

                // Reveal all 8 neighbors
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue;

                        int newRow = r + dr;
                        int newCol = c + dc;

                        if (board.isValid(newRow, newCol) && !visited[newRow][newCol]) {
                            visited[newRow][newCol] = true;
                            Cell neighbor = board.getCell(newRow, newCol);

                            if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                                neighbor.setRevealed(true);
                                addScore(1);

                                // Continue cascade for Empty, Question, and Surprise tiles
                                // Stop ONLY at Number tiles (and Mines)
                                if (neighbor.getType() == Cell.CellType.EMPTY ||
                                    neighbor.getType() == Cell.CellType.QUESTION ||
                                    neighbor.getType() == Cell.CellType.SURPRISE) {
                                    queue.offer(new int[]{newRow, newCol});
                                }
                            }
                        }
                    }
                }
            }

            BoardPanel boardPanel = isPlayerA ? playerABoard : playerBBoard;
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    boardPanel.getButton(i, j).updateDisplay();
                }
            }
        }

        private void activateQuestionTile() {
            if (gameSession.getSharedScore() < gameSession.getDifficulty().activationCost) {
                JOptionPane.showMessageDialog(MinesweeperGame.this, 
                    "Not enough points! Need " + 
                    gameSession.getDifficulty().activationCost + " points.",
                    "Cannot Activate", JOptionPane.WARNING_MESSAGE);
                return;
            }

            addScore(-gameSession.getDifficulty().activationCost);
            cell.setUsed(true);

            Question.QuestionDifficulty qDiff = Question.getRandomDifficulty();

            String[] options = {"Answer A", "Answer B", "Answer C", "Answer D"};
            int answer = JOptionPane.showOptionDialog(MinesweeperGame.this, 
                "Question Type: " + qDiff.name() + 
                "\n\nSample Question: What is 2 + 2?",
                "Question Tile - " + gameSession.getDifficulty().name() + " Game Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

            boolean correct = (answer == 0);
            applyQuestionOutcome(gameSession.getDifficulty(), qDiff, correct);

            updateDisplay();
            switchTurn();
        }

        private void applyQuestionOutcome(GameSession.Difficulty gameDiff, 
                                         Question.QuestionDifficulty qDiff, 
                                         boolean correct) {
            Random rand = new Random();
            int points = 0;
            int lives = 0;
            String message = "";

            // Easy Game Mode
            if (gameDiff == GameSession.Difficulty.EASY) {
                if (qDiff == Question.QuestionDifficulty.EASY) {
                    if (correct) {
                        points = 3;
                        lives = 1;
                    } else {
                        if (rand.nextBoolean()) points = -3;
                    }
                } else if (qDiff == Question.QuestionDifficulty.MEDIUM) {
                    if (correct) {
                        points = 6;
                        // FIX: Actually reveal a mine tile
                        revealRandomMineTile();
                        message = "Revealed mine tile & +6pts";
                    } else {
                        if (rand.nextBoolean()) points = -6;
                    }
                } else if (qDiff == Question.QuestionDifficulty.HARD) {
                    if (correct) {
                        points = 10;
                        // FIX: Actually reveal 3x3 tiles
                        reveal3x3Area();
                        message = "Revealed 3x3 area & +10pts";
                    } else {
                        points = -10;
                    }
                } else { // EXPERT
                    if (correct) {
                        points = 15;
                        lives = 2;
                    } else {
                        points = -15;
                        lives = -1;
                    }
                }
            }
            // Medium Game Mode
            else if (gameDiff == GameSession.Difficulty.MEDIUM) {
                if (qDiff == Question.QuestionDifficulty.EASY) {
                    if (correct) {
                        points = 8;
                        lives = 1;
                    } else {
                        points = -8;
                    }
                } else if (qDiff == Question.QuestionDifficulty.MEDIUM) {
                    if (correct) {
                        points = 10;
                        lives = 1;
                    } else {
                        if (rand.nextBoolean()) {
                            points = -10;
                            lives = -1;
                        }
                    }
                } else if (qDiff == Question.QuestionDifficulty.HARD) {
                    if (correct) {
                        points = 15;
                        lives = 1;
                    } else {
                        points = -15;
                        lives = -1;
                    }
                } else { // EXPERT
                    if (correct) {
                        points = 20;
                        lives = 2;
                    } else {
                        points = -20;
                        lives = rand.nextBoolean() ? -1 : -2;
                    }
                }
            }
            // Hard Game Mode
            else if (gameDiff == GameSession.Difficulty.HARD) {
                if (qDiff == Question.QuestionDifficulty.EASY) {
                    if (correct) {
                        points = 10;
                        lives = 1;
                    } else {
                        points = -10;
                        lives = -1;
                    }
                } else if (qDiff == Question.QuestionDifficulty.MEDIUM) {
                    if (correct) {
                        points = 15;
                        lives = rand.nextBoolean() ? 1 : 2;
                    } else {
                        points = -15;
                        lives = rand.nextBoolean() ? -1 : -2;
                    }
                } else if (qDiff == Question.QuestionDifficulty.HARD) {
                    if (correct) {
                        points = 20;
                        lives = 2;
                    } else {
                        points = -20;
                        lives = -2;
                    }
                } else { // EXPERT
                    if (correct) {
                        points = 40;
                        lives = 3;
                    } else {
                        points = -40;
                        lives = -3;
                    }
                }
            }

            addScore(points);
            addLives(lives);

            String result = correct ? "✓ Correct!" : "✗ Incorrect!";
            String details = message.isEmpty() ? 
                String.format("%s\nPoints: %+d | Lives: %+d", result, points, lives) :
                String.format("%s\nPoints: %+d | Lives: %+d", result, points, lives);

            JOptionPane.showMessageDialog(MinesweeperGame.this, details, 
                "Question Result - " + qDiff.name(),
                correct ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        }

        // NEW: Reveal a random mine tile
        private void revealRandomMineTile() {
            java.util.List<int[]> hiddenMines = new ArrayList<>();
            
            for (int r = 0; r < board.getSize(); r++) {
                for (int c = 0; c < board.getSize(); c++) {
                    Cell cell = board.getCell(r, c);
                    if (cell.getType() == Cell.CellType.MINE && !cell.isRevealed()) {
                        hiddenMines.add(new int[]{r, c});
                    }
                }
            }
            
            if (!hiddenMines.isEmpty()) {
                Random rand = new Random();
                int[] minePos = hiddenMines.get(rand.nextInt(hiddenMines.size()));
                Cell mineCell = board.getCell(minePos[0], minePos[1]);
                mineCell.setRevealed(true);
                board.incrementRevealedMines();
                
                BoardPanel boardPanel = isPlayerA ? playerABoard : playerBBoard;
                boardPanel.getButton(minePos[0], minePos[1]).updateDisplay();
            }
        }

        // NEW: Reveal 3x3 area (NOT around the question tile)
        private void reveal3x3Area() {
            Random rand = new Random();
            java.util.List<int[]> fullyUnrevealedCenters = new ArrayList<>();
            java.util.List<int[]> partiallyUnrevealedCenters = new ArrayList<>();
            
            // Find all possible 3x3 blocks
            for (int centerRow = 1; centerRow < board.getSize() - 1; centerRow++) {
                for (int centerCol = 1; centerCol < board.getSize() - 1; centerCol++) {
                    
                    // Check if this 3x3 block has NO overlap with question tile's 3x3 area
                    boolean hasOverlap = false;
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int checkRow = centerRow + dr;
                            int checkCol = centerCol + dc;
                            
                            // Check if overlaps with question tile's 3x3 area
                            if (checkRow >= row - 1 && checkRow <= row + 1 && 
                                checkCol >= col - 1 && checkCol <= col + 1) {
                                hasOverlap = true;
                                break;
                            }
                        }
                        if (hasOverlap) break;
                    }
                    
                    if (hasOverlap) continue;
                    
                    // Check status of this 3x3 block
                    boolean allUnrevealed = true;
                    boolean hasUnrevealed = false;
                    
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int checkRow = centerRow + dr;
                            int checkCol = centerCol + dc;
                            Cell checkCell = board.getCell(checkRow, checkCol);
                            
                            if (!checkCell.isRevealed() && !checkCell.isFlagged()) {
                                hasUnrevealed = true;
                            } else {
                                allUnrevealed = false;
                            }
                        }
                    }
                    
                    // Categorize the block
                    if (allUnrevealed) {
                        fullyUnrevealedCenters.add(new int[]{centerRow, centerCol});
                    } else if (hasUnrevealed) {
                        partiallyUnrevealedCenters.add(new int[]{centerRow, centerCol});
                    }
                }
            }
            
            // Choose block: prefer fully unrevealed, fallback to partially unrevealed
            java.util.List<int[]> validCenters = !fullyUnrevealedCenters.isEmpty() ? 
                                                  fullyUnrevealedCenters : 
                                                  partiallyUnrevealedCenters;
            
            // If we found valid 3x3 blocks, pick one randomly and reveal it
            if (!validCenters.isEmpty()) {
                int[] chosenCenter = validCenters.get(rand.nextInt(validCenters.size()));
                int centerRow = chosenCenter[0];
                int centerCol = chosenCenter[1];
                
                int revealedCount = 0;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int newRow = centerRow + dr;
                        int newCol = centerCol + dc;
                        
                        Cell neighborCell = board.getCell(newRow, newCol);
                        if (!neighborCell.isRevealed() && !neighborCell.isFlagged()) {
                            neighborCell.setRevealed(true);
                            addScore(1);
                            revealedCount++;
                            
                            if (neighborCell.getType() == Cell.CellType.MINE) {
                                board.incrementRevealedMines();
                            }
                            
                            BoardPanel boardPanel = isPlayerA ? playerABoard : playerBBoard;
                            boardPanel.getButton(newRow, newCol).updateDisplay();
                        }
                    }
                }
                
                String blockType = !fullyUnrevealedCenters.isEmpty() ? "fully unrevealed" : "partially unrevealed";
                System.out.println("Revealed " + revealedCount + " tiles in " + blockType + 
                                 " 3x3 block at (" + centerRow + ", " + centerCol + ")");
            } else {
                System.out.println("No valid 3x3 block found (without overlap with question tile)");
            }
        }

        private void activateSurpriseTile() {
            if (gameSession.getSharedScore() < gameSession.getDifficulty().activationCost) {
                JOptionPane.showMessageDialog(MinesweeperGame.this, 
                    "Not enough points! Need " + 
                    gameSession.getDifficulty().activationCost + " points.",
                    "Cannot Activate", JOptionPane.WARNING_MESSAGE);
                return;
            }

            addScore(-gameSession.getDifficulty().activationCost);
            cell.setUsed(true);

            Random rand = new Random();
            boolean isGood = rand.nextBoolean();
            int pointChange = 0;
            int lifeChange = 0;

            switch (gameSession.getDifficulty()) {
                case EASY:
                    pointChange = isGood ? 8 : -8;
                    lifeChange = isGood ? 1 : -1;
                    break;
                case MEDIUM:
                    pointChange = isGood ? 12 : -12;
                    lifeChange = isGood ? 1 : -1;
                    break;
                case HARD:
                    pointChange = isGood ? 16 : -16;
                    lifeChange = isGood ? 1 : -1;
                    break;
            }

            addScore(pointChange);
            addLives(lifeChange);

            String message = isGood ? 
                "🎉 Good surprise! +" + pointChange + " points, +1 life" :
                "💔 Bad surprise! " + pointChange + " points, -1 life";

            JOptionPane.showMessageDialog(MinesweeperGame.this, message, "Surprise!",
                isGood ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

            updateDisplay();
            switchTurn();
        }

        public void updateDisplay() {
            if (cell.isFlagged()) {
                setBackground(new Color(50, 30, 30));
                setText("🚩");
                setForeground(new Color(255, 80, 80));
            } else if (cell.isRevealed()) {
                setBackground(new Color(30, 40, 50));

                switch (cell.getType()) {
                    case MINE:
                        setText("💣");
                        setForeground(Color.RED);
                        break;
                    case NUMBER:
                        setText(String.valueOf(cell.getAdjacentMines()));
                        setForeground(getNumberColor(cell.getAdjacentMines()));
                        break;
                    case EMPTY:
                        setText("");
                        setBackground(new Color(40, 50, 60));
                        break;
                    case QUESTION:
                        setText(cell.isUsed() ? "✓" : "?");
                        setForeground(new Color(255, 200, 50));
                        setBackground(cell.isUsed() ? 
                            new Color(50, 60, 40) : new Color(80, 70, 30));
                        break;
                    case SURPRISE:
                        setText(cell.isUsed() ? "✓" : "🎁");
                        setBackground(cell.isUsed() ? 
                            new Color(50, 40, 60) : new Color(70, 40, 80));
                        break;
                }
            } else {
                setBackground(new Color(20, 30, 40));
                setText("");
            }
        }

        private Color getNumberColor(int num) {
            Color[] colors = {
                null,
                new Color(100, 150, 255), // 1: Blue
                new Color(100, 200, 100), // 2: Green
                new Color(255, 100, 100), // 3: Red
                new Color(150, 100, 255), // 4: Purple
                new Color(255, 150, 50),  // 5: Orange
                new Color(0, 200, 200),   // 6: Cyan
                new Color(255, 100, 200), // 7: Pink
                new Color(255, 255, 100)  // 8: Yellow
            };
            return (num > 0 && num < colors.length) ? colors[num] : Color.WHITE;
        }
    }
}