package minesweeper.model;

public enum Difficulty {

    EASY(
            9,      // grid size
            10,     // mines
            6,      // question cells (FIXED: was 8)
            2,      // surprise cells (FIXED: was 3)
            1,      // safe-cell points
            -5,     // mine hit penalty
            3,      // correct question bonus
            -2      // wrong question penalty
    ),

    MEDIUM(
            13,     // grid size
            26,     // mines (FIXED: was 25)
            7,      // question cells (FIXED: was 15)
            3,      // surprise cells (FIXED: was 6)
            2,      // safe-cell points
            -10,    // mine hit penalty
            5,      // correct question bonus
            -3      // wrong question penalty
    ),

    HARD(
            16,     // grid size
            44,     // mines (FIXED: was 40)
            11,     // question cells (FIXED: was 20)
            4,      // surprise cells (FIXED: was 10)
            3,      // safe-cell points
            -15,    // mine hit penalty
            7,      // correct question bonus
            -4      // wrong question penalty
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