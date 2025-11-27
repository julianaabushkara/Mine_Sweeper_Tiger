package minesweeper.model;

public enum Difficulty {

    EASY(
            9,      // grid size
            10,     // mines
            8,      // question cells
            3,      // surprise cells
            1,      // safe-cell points
            -5,     // mine hit penalty
            3,      // correct question bonus
            -2      // wrong question penalty
    ),

    MEDIUM(
            13,
            25,
            15,
            6,
            2,
            -10,
            5,
            -3
    ),

    HARD(
            16,
            40,
            20,
            10,
            3,
            -15,
            7,
            -4
    );

    private final int gridSize;
    private final int mines;
    private final int questionCells;
    private final int surpriseCells;
    private final int safeCellPoints;
    private final int mineHitPenalty;
    private final int correctQuestionPoints;
    private final int wrongQuestionPenalty;

    Difficulty(int gridSize,
               int mines,
               int questionCells,
               int surpriseCells,
               int safeCellPoints,
               int mineHitPenalty,
               int correctQuestionPoints,
               int wrongQuestionPenalty) {

        this.gridSize = gridSize;
        this.mines = mines;
        this.questionCells = questionCells;
        this.surpriseCells = surpriseCells;
        this.safeCellPoints = safeCellPoints;
        this.mineHitPenalty = mineHitPenalty;
        this.correctQuestionPoints = correctQuestionPoints;
        this.wrongQuestionPenalty = wrongQuestionPenalty;
    }

    // GETTERS
    public int getGridSize() { return gridSize; }
    public int getMines() { return mines; }
    public int getQuestionCells() { return questionCells; }
    public int getSurpriseCells() { return surpriseCells; }
    public int getSafeCellPoints() { return safeCellPoints; }
    public int getMineHitPenalty() { return mineHitPenalty; }
    public int getCorrectQuestionPoints() { return correctQuestionPoints; }
    public int getWrongQuestionPenalty() { return wrongQuestionPenalty; }
}
