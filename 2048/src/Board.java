import java.util.*;

/**
 * An instance of a 2048 game. Can have alternative board dimensions by specifying <tt> BOARD_SIZE </tt> but board
 * shape must be a square. Keeps track of game score with <tt> score </tt> and tiles achieved with <tt> board </tt>.
 */
public class Board {
    /** Length (and width) of the board. */
    public int BOARD_SIZE;
    /** Values of the tiles on the board. */
    public int[][] board;
    /** Sum of all merged tiles. */
    public int score = 0;

    /** Number of squares without tiles. */
    public int numFreeTiles;

    /** Future board state after shifting leftwards. Null if shifting left leaves board unchanged. */
    public Board leftBoard;
    /** Future board state after shifting rightwards. Null if shifting right leaves board unchanged. */
    public Board rightBoard;
    /** Future board state after shifting downwards. Null if shifting down leaves board unchanged. */
    public Board downBoard;
    /** Future board state after shifting upwards. Null if shifting up leaves board unchanged. */
    public Board upBoard;


    /** Default constructor: initializes 4x4 board and generates two starter tiles randomly. */
    public Board() {
        BOARD_SIZE = 4;
        board = new int[BOARD_SIZE][BOARD_SIZE];
        numFreeTiles = BOARD_SIZE * BOARD_SIZE;
        generateNewTile();
        generateNewTile();
    }

    /** Constructor: initializes <tt> size </tt> x <tt> size </tt> board with <tt> f </tt> freeTiles and score <tt> s
     * </tt>. */
    private Board(int size, int f, int s) {
        BOARD_SIZE = size;
        board = new int[BOARD_SIZE][BOARD_SIZE];
        numFreeTiles = f;
        score = s;
    }

    /** Generates a new tile randomly onto a free square on the board. Does nothing if the board is filled. */
    private void generateNewTile() {
        int index = (int)(Math.random() * numFreeTiles);
        numFreeTiles--;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    if (index == 0) { // new tile location
                        board[i][j] = (Math.random() <= 0.9) ? 2 : 4; // 0.9 probability to set new tile to 2
                        return;
                    }
                    else {
                        index--;
                    }
                }
            }
        }
    }

    /** Calculates leftBoard. Returns true if shifting leftwards changes the board. */
    private boolean shiftLeft() {
        boolean ret = false; // true if move is valid
        leftBoard = new Board(BOARD_SIZE, numFreeTiles, score);

        for (int i = 0; i < BOARD_SIZE; i++) { // per row
            int cat = 0, dog = 0, mouse = 0;
            boolean homeless = true;

            while (mouse < BOARD_SIZE) {
                if (board[i][mouse] == 0) { // tile is empty
                    mouse++;
                }
                else if (board[i][dog] == 0 || homeless) { // save non-empty tile for possible merge
                    dog = mouse++;
                    leftBoard.board[i][cat] = board[i][dog];
                    homeless = false;
                    ret = (ret || (dog != cat));
                }
                else {
                    if (board[i][mouse] == board[i][dog]) { // merge tiles
                        leftBoard.board[i][cat] += board[i][mouse];
                        leftBoard.score += leftBoard.board[i][cat];
                        leftBoard.numFreeTiles++;
                        mouse++;
                        ret = true;

                    }
                    homeless = true;
                    cat++;
                }
            }
        }

        if (!ret) { // no changes made
            leftBoard = null;
        }
        return ret;
    }

    /** Calculates rightBoard. Returns true if shifting rightwards changes the board. */
    private boolean shiftRight() {
        boolean ret = false; // true if move is valid
        rightBoard = new Board(BOARD_SIZE, numFreeTiles, score);

        for (int i = 0; i < BOARD_SIZE; i++) {
            int cat = BOARD_SIZE-1, dog = BOARD_SIZE-1, mouse = BOARD_SIZE-1;
            boolean homeless = true;

            while (mouse >= 0) {
                if (board[i][mouse] == 0) {
                    mouse--;
                }
                else if (board[i][dog] == 0 || homeless) {
                    dog = mouse--;
                    rightBoard.board[i][cat] = board[i][dog];
                    homeless = false;
                    ret = (ret || (dog != cat));
                }
                else {
                    if (board[i][mouse] == board[i][dog]) {
                        rightBoard.board[i][cat] += board[i][mouse];
                        rightBoard.score += rightBoard.board[i][cat];
                        rightBoard.numFreeTiles++;
                        mouse--;
                        ret = true;
                    }
                    homeless = true;
                    cat--;
                }
            }
        }

        if (!ret) {
            rightBoard = null;
        }
        return ret;
    }

    /** Calculates downBoard. Returns true if shifting downwards changes the board. */
    private boolean shiftDown() {
        boolean ret = false; // true if move is valid
        downBoard = new Board(BOARD_SIZE, numFreeTiles, score);

        for (int j = 0; j < BOARD_SIZE; j++) {
            int cat = BOARD_SIZE-1, dog = BOARD_SIZE-1, mouse = BOARD_SIZE-1;
            boolean homeless = true;

            while (mouse >= 0) {
                if (board[mouse][j] == 0) {
                    mouse--;
                }
                else if (board[dog][j] == 0 || homeless) {
                    dog = mouse--;
                    downBoard.board[cat][j] = board[dog][j];
                    homeless = false;
                    ret = (ret || (dog != cat));
                }
                else {
                    if (board[mouse][j] == board[dog][j]) {
                        downBoard.board[cat][j] += board[mouse][j];
                        downBoard.score += downBoard.board[cat][j];
                        downBoard.numFreeTiles++;
                        mouse--;
                        ret = true;
                    }
                    homeless = true;
                    cat--;
                }
            }
        }

        if (!ret) {
            downBoard = null;
        }
        return ret;
    }

    /** Calculates upBoard. Returns true if shifting upwards changes the board. */
    private boolean shiftUp() {
        boolean ret = false; // true if move is valid
        upBoard = new Board(BOARD_SIZE, numFreeTiles, score);

        for (int j = 0; j < BOARD_SIZE; j++) {
            int cat = 0, dog = 0, mouse = 0;
            boolean homeless = true;

            while (mouse < BOARD_SIZE) {
                if (board[mouse][j] == 0) {
                    mouse++;
                }
                else if (board[dog][j] == 0 || homeless) {
                    dog = mouse++;
                    upBoard.board[cat][j] = board[dog][j];
                    homeless = false;
                    ret = (ret || (dog != cat));
                }
                else {
                    if (board[mouse][j] == board[dog][j]) {
                        upBoard.board[cat][j] += board[mouse][j];
                        upBoard.score += upBoard.board[cat][j];
                        upBoard.numFreeTiles++;
                        mouse++;
                        ret = true;
                    }
                    homeless = true;
                    cat++;
                }
            }
        }

        if (!ret) {
            upBoard = null;
        }
        return ret;
    }

    /** Returns true if there are no valid moves remaining. */
    public boolean checkGameOver() {
        boolean l = shiftLeft();
        boolean r = shiftRight();
        boolean d = shiftDown();
        boolean u = shiftUp();
        return !l && !r && !d && !u;
    }

    /**
     * Alters the board based on input:
     *      <tt> input = 'L' </tt>: shiftLeft
     *      <tt> input = 'R' </tt> : shiftRight
     *      <tt> input = 'U' </tt> : shiftUp
     *      <tt> input = 'D' </tt : shiftDown
     * Leaves board unchanged if input is not recognized.
     */
    public void play(char input) {
        if (!checkGameOver()) {
            if (input == 'L' && leftBoard != null) { // left
                board = leftBoard.board;
                score = leftBoard.score;
                numFreeTiles = leftBoard.numFreeTiles;
            }
            else if (input == 'R' && rightBoard != null) { // right
                board = rightBoard.board;
                score = rightBoard.score;
                numFreeTiles = rightBoard.numFreeTiles;
            }
            else if (input == 'D' && downBoard != null) { // down
                board = downBoard.board;
                score = downBoard.score;
                numFreeTiles = downBoard.numFreeTiles;
            }
            else if (input == 'U' && upBoard != null) { // up
                board = upBoard.board;
                score = upBoard.score;
                numFreeTiles = upBoard.numFreeTiles;
            }
            else { // exit before new tile is spawned
                return;
            }
            generateNewTile(); // spawns a new tile
        }
        else { // game over
            System.out.println("Game over! Final score: " + score);
        }
    }

    /** Prints the board state and the current score. */
    public void printBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.println(Arrays.toString(board[i]));
        }
        System.out.println("Score: " + score);
        System.out.println();
    }
}
