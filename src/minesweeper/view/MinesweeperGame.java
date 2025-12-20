package minesweeper.view;

import minesweeper.model.*;
import minesweeper.controller.GameController;
import minesweeper.model.QuestionDifficulty;
import minesweeper.model.Question;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Queue;
import java.util.LinkedList;


public class MinesweeperGame extends JFrame {
    private GameController controller;
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
    private boolean gameEnded = false;
    GameSession session;

    private int elapsedSeconds;
    private QuestionBank questionBank;
    /*
    revealAllTiles(session.getPlayerABoard(), playerABoard);
    revealAllTiles(session.getPlayerBBoard(), playerBBoard);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.startNewGame(,,GameSession.Difficulty.EASY);
            MinesweeperGame game = new MinesweeperGame(controller);
            game.setVisible(true);
        });
    }*/

    public MinesweeperGame(GameController controller, QuestionBank questionBank) {
        this.controller = controller;
        controller.setView(this);

        // Use the QuestionBank passed as parameter (contains the user's uploaded CSV)
        this.questionBank = questionBank;

        // Verify the question bank is loaded
        if (questionBank != null && questionBank.isLoaded()) {
            System.out.println("✓ Using loaded question bank with " +
                    questionBank.getQuestionCount() + " questions.");
        } else {
            System.out.println("⚠ Warning: Question bank is empty or not loaded!");
            // Keep the empty/unloaded questionBank - don't create a new one
            // The game will show appropriate warnings when trying to activate question tiles
        }

        setTitle("Minesweeper Boards");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(15, 20, 25));

        elapsedSeconds = 0;

        createTopPanel();
        createBoardsPanel();
        createBottomPanel();
        startTimer();
        // Fit window to screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int width = (int) (screenSize.width * 0.85);
        int height = (int) (screenSize.height * 0.85);

        setSize(width, height);
        setLocationRelativeTo(null); // center
    }


    private void createTopPanel() {
        topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setBackground(new Color(15, 20, 25));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 200), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        session = controller.getGameSession();

        // Left: Turn info
        //turnLabel = createStyledLabel("CURRENT\nTURN: PLAYER A", 18, Color.WHITE);

        String currentPlayer = session.isPlayerATurn()
                ? session.getPlayerAName()
                : session.getPlayerBName();

        turnLabel = createStyledLabel(
                "<html>CURRENT<br>TURN: " + currentPlayer + "</html>",
                18,
                Color.WHITE
        );


        turnLabel.setPreferredSize(new Dimension(200, 80));

        // Center: Lives and Score
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        centerPanel.setBackground(new Color(15, 20, 25));
        livesLabel = createStyledLabel("LIVES: " + getHeartsString(session.getSharedLives()), 16, Color.WHITE);
        scoreLabel = createStyledLabel("SCORE: 0000 (EASY)", 16, new Color(0, 255, 100));
        centerPanel.add(livesLabel);
        centerPanel.add(scoreLabel);

        // Right: Mines count 
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        rightPanel.setBackground(new Color(15, 20, 25));
        int totalMines = session.getDifficulty().mines * 2;
        minesLabel = createStyledLabel("MINES:\n" + totalMines, 16, new Color(255, 80, 80));

        // Timer with red rectangular background
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(new Color(100, 150, 255));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)

        ));



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

        /*session = controller.getGameSession();
        playerABoard = new BoardPanel("PLAYER A BOARD", session.getPlayerABoard(),
                new Color(100, 50, 150), true);
        playerBBoard = new BoardPanel("PLAYER B BOARD", session.getPlayerBBoard(),
                new Color(0, 150, 200), false);*/

        playerABoard = new BoardPanel(
                session.getPlayerAName() + " BOARD",
                session.getPlayerABoard(),
                new Color(100, 50, 150),
                true
        );

        playerBBoard = new BoardPanel(
                session.getPlayerBName() + " BOARD",
                session.getPlayerBBoard(),
                new Color(0, 150, 200),
                false
        );


        boardsPanel.add(playerABoard);
        boardsPanel.add(playerBBoard);

        add(boardsPanel, BorderLayout.CENTER);
    }

    private void switchTurn() {
        controller.switchTurn();
        updateGameDisplay();
        checkGameEnd();
    }
/*
    private void checkGameEnd() {
        if (controller.isGameOver()) {
            gameEnded = true;  // Set flag to prevent further interactions
             gameTimer.stop();
            endGame(controller.isVictory());
            
        }
    }*/


    private void checkGameEnd() {
        if (gameEnded) return;

        if (controller.isGameOver()) {
            gameEnded = true;

            if (gameTimer != null) {
                gameTimer.stop();
            }

            endGame(controller.isVictory());
        }
    }


    private void endGame(boolean victory) {
        GameSession session = controller.getGameSession();
        int finalScore = controller.calculateFinalScore();

        revealAllTiles(session.getPlayerABoard(), playerABoard);
        revealAllTiles(session.getPlayerBBoard(), playerBBoard);

        updateGameDisplay();

        int lifeBonus = session.getSharedLives() * session.getDifficulty().activationCost;
        String message = victory ?
                "🎉 VICTORY! 🎉\n\n" +
                        "All mines discovered!\n" +
                        "Remaining Lives Bonus: " + session.getSharedLives() + " × " +
                        session.getDifficulty().activationCost + " = +" + lifeBonus + " points\n\n" +
                        "FINAL SCORE: " + finalScore :
                "💔 DEFEAT 💔\n\n" +
                        "Lives reached 0!\n\n" +
                        "FINAL SCORE: " + finalScore;

        JOptionPane.showMessageDialog(this, message,
                victory ? "You Win!" : "Game Over",
                victory ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        //  ADD THIS
        controller.handleGameEndIfNeeded();

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
            this.dispose();
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

    public void updateGameDisplay() {
        GameSession session = controller.getGameSession();


        /* String currentPlayer = session.isPlayerATurn() ? "PLAYER A" : "PLAYER B";
        turnLabel.setText("<html>CURRENT<br>TURN: " + currentPlayer + "</html>");*/
        String currentPlayer = session.isPlayerATurn()
                ? session.getPlayerAName()
                : session.getPlayerBName();

        boolean isATurn = session.isPlayerATurn();
        // Highlight boards
        playerABoard.setActive(isATurn);
        playerBBoard.setActive(!isATurn);

        turnLabel.setText("<html>CURRENT<br>TURN: " + currentPlayer + "</html>");


        System.out.println(
                "Players: " +
                        controller.getGameSession().getPlayerAName() + " / " +
                        controller.getGameSession().getPlayerBName()
        );


        livesLabel.setText("LIVES: " + getHeartsString(session.getSharedLives()));
        scoreLabel.setText(String.format("SCORE: %04d (%s)",
                session.getSharedScore(), session.getDifficulty().name()));

        int totalMines = session.getPlayerABoard().getTotalMines() +
                session.getPlayerBBoard().getTotalMines();
        int revealedMines = session.getPlayerABoard().getRevealedMines() +
                session.getPlayerBBoard().getRevealedMines();
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


        public void setActive(boolean active) {
            if (active) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(themeColor.brighter(), 5),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                setBackground(new Color(25, 35, 45)); // slightly brighter
            } else {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(themeColor.darker(), 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                setBackground(new Color(15, 20, 25)); // dim
            }
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

                    // Block all interactions if game has ended
                    if (gameEnded) {
                        return;
                    }


                    GameSession session = controller.getGameSession();

                    if (session.isPlayerATurn() != isPlayerA) {
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
                        controller.addLives(-1);
                        board.incrementRevealedMines();
                        JOptionPane.showMessageDialog(MinesweeperGame.this,
                                "💣 Mine discovered! -1 life\nMines discovered: " +
                                        board.getRevealedMines() + "/" + board.getTotalMines(),
                                "Mine!", JOptionPane.WARNING_MESSAGE);
                        break;
                    case NUMBER:
                        controller.addScore(1);
                        break;
                    case EMPTY:
                        controller.addScore(1);
                        performCascade(row, col);
                        break;
                    case QUESTION:
                    case SURPRISE:
                        controller.addScore(1);
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
                    controller.addScore(1);
                    board.incrementRevealedMines();
                    JOptionPane.showMessageDialog(MinesweeperGame.this,
                            "✓ Correct flag! +1 point\nMines discovered: " +
                                    board.getRevealedMines() + "/" + board.getTotalMines(),
                            "Good Job!", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    controller.addScore(-3);
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
                                controller.addScore(1);

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

        private void performSpecialCascade(int startRow, int startCol) {
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
                                controller.addScore(1);

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
            GameSession session = controller.getGameSession();

            if (session.getSharedScore() < session.getDifficulty().activationCost) {
                JOptionPane.showMessageDialog(
                        MinesweeperGame.this,
                        "Not enough points! Need " + session.getDifficulty().activationCost + " points.",
                        "Cannot Activate",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (questionBank == null || !questionBank.isLoaded() || questionBank.getQuestionCount() == 0) {
                JOptionPane.showMessageDialog(
                        MinesweeperGame.this,
                        "No questions loaded – cannot activate question tile.",
                        "Question Bank Empty",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            QuestionDifficulty[] difficulties = QuestionDifficulty.values();
            QuestionDifficulty qDiff = difficulties[new Random().nextInt(difficulties.length)];

            java.util.List<Question> candidates = new java.util.ArrayList<>();
            for (Question q : questionBank.getAllQuestions()) {
                if (q.getDifficulty() == qDiff) {
                    candidates.add(q);
                }
            }

            if (candidates.isEmpty()) {
                JOptionPane.showMessageDialog(
                        MinesweeperGame.this,
                        "No questions available for difficulty: " + qDiff,
                        "Question Bank",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Question question = candidates.get(new Random().nextInt(candidates.size()));

            String[] options = {
                    question.getOptionA(),
                    question.getOptionB(),
                    question.getOptionC(),
                    question.getOptionD()
            };

            int answerIndex = JOptionPane.showOptionDialog(
                    MinesweeperGame.this,
                    question.getText(),
                    "Question (" + qDiff.toString() + ")",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (answerIndex == -1) {
                return;
            }

            char chosenOption = (char) ('A' + answerIndex); // 0->A, 1->B, 2->C, 3->D
            boolean correct = question.isCorrectAnswer(chosenOption);

            String feedback = controller.getQuestionFeedback(qDiff, correct);

            if (session.getDifficulty() == GameSession.Difficulty.EASY) {
                if (qDiff == QuestionDifficulty.MEDIUM && correct) {
                    int[] minePos = controller.revealRandomMineTile(board);
                    if (minePos != null) {
                        BoardPanel boardPanel = isPlayerA ? playerABoard : playerBBoard;
                        boardPanel.getButton(minePos[0], minePos[1]).updateDisplay();
                        feedback += "\n\n🎁 Bonus: Random mine revealed!";
                    }
                } else if (qDiff == QuestionDifficulty.HARD && correct) {
                    java.util.List<int[]> revealedCells = controller.reveal3x3Area(row, col, board);
                    if (!revealedCells.isEmpty()) {
                        BoardPanel boardPanel = isPlayerA ? playerABoard : playerBBoard;
                        for (int[] pos : revealedCells) {
                            boardPanel.getButton(pos[0], pos[1]).updateDisplay();
                        }
                        feedback += "\n\n🎁 Bonus: 3×3 area revealed!";
                    }
                }
            }

            controller.activateQuestionTile(cell, qDiff, correct, row, col, board);

            JOptionPane.showMessageDialog(
                    MinesweeperGame.this,
                    feedback,
                    "Question Result - " + qDiff.name(),
                    correct ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
            );

            updateDisplay();
            updateGameDisplay();
            switchTurn();
        }

        private void activateSurpriseTile() {
            GameSession session = controller.getGameSession();
            if (session.getSharedScore() < session.getDifficulty().activationCost) {
                JOptionPane.showMessageDialog(MinesweeperGame.this,
                        "Not enough points! Need " +
                                session.getDifficulty().activationCost + " points.",
                        "Cannot Activate", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String feedback = controller.activateSurpriseTile(cell);

            // Show feedback
            JOptionPane.showMessageDialog(MinesweeperGame.this, feedback,
                    "Surprise Tile", JOptionPane.INFORMATION_MESSAGE);

            updateDisplay();
            updateGameDisplay();
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