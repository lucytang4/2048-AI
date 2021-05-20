import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** An AI which plays 2048. */
public class MinimaxAI {
    /** Board which AI plays on. */
    public Board b;

    private int DEPTH = 8;
    private NeuralNetwork nn = new NeuralNetwork("Data/NeuralNetwork1", new Logistic());

    /** Default constructor: plays game on 4x4 board. */
    public MinimaxAI() {
        b = new Board();
    }

    /** Evaluation function using neural network. */
    private double evalNN(Board b) {
        double[] x = new double[b.BOARD_SIZE * b.BOARD_SIZE];
        int counter = 0;
        for(int i = 0; i < b.BOARD_SIZE; i++) {
            for (int j = 0; j < b.BOARD_SIZE; j++) {
                x[counter] = b.board[i][j];
                counter++;
            }
        }

        return nn.forwardProp(x);
    }

    /** Evaluation function using hand-crafted logic. */
    private double eval(Board b) {
        int adjScore = 0;

        for (int i = 0; i < b.BOARD_SIZE-1; i++) {
            for (int j = 0; j < b.BOARD_SIZE-1; j++) {
                int rightDiff = Math.abs(b.board[i][j]-b.board[i][j+1]);
                int downDiff = Math.abs(b.board[i][j]-b.board[i+1][j]);
                adjScore = adjScore + rightDiff + downDiff;
            }
        }

        for (int i = 0; i < b.BOARD_SIZE-1; i++) { // for right-most column
            int downDiff = Math.abs(b.board[i][b.BOARD_SIZE-1]-b.board[i+1][b.BOARD_SIZE-1]);
            adjScore += downDiff;
        }

        for (int j = 0; j < b.BOARD_SIZE-1; j++) { // for down-most row
            int rightDiff = Math.abs(b.board[b.BOARD_SIZE-1][j]-b.board[b.BOARD_SIZE-1][j+1]);
            adjScore += rightDiff;
        }

        return b.score - adjScore;
    }

    /** AI plays a move. */
    public void play() {
        char bestMove = 'A';
        double max = Double.MIN_VALUE;
        // alpha-beta pruning for efficiency
        double alpha = Double.MIN_VALUE;
        double beta = Double.MAX_VALUE;

        if (b.leftBoard != null) { // check shifting left
            double val = maximin(b.leftBoard, DEPTH-1, alpha, beta);
            if (val >= max) {
                bestMove = 'L';
                max = val;
            }
        }
        if (b.rightBoard != null) { // check shifting right
            double val = maximin(b.rightBoard, DEPTH-1, alpha, beta);
            if (val >= max) {
                bestMove = 'R';
                max = val;
            }
        }
        if (b.downBoard != null) { // check shifting down
            double val = maximin(b.downBoard, DEPTH-1, alpha, beta);
            if (val >= max) {
                bestMove = 'D';
                max = val;
            }
        }
        if (b.upBoard != null) { // check shifting up
            double val = maximin(b.upBoard, DEPTH-1, alpha, beta);
            if (val >= max) {
                bestMove = 'U';
                max = val;
            }
        }

        b.play(bestMove); // play the best move
    }

    private double minimax(Board b, int depth, double alpha, double beta) {
        if (depth == 0) { // maximum depth reached
            return eval(b);
        }
        else if (b.checkGameOver()) { // game is lost
            return Double.MIN_VALUE;
        }
        else { // check all moves to see which is best
            double val = Double.MIN_VALUE;

            if (b.leftBoard != null) { // left
                double tmp = maximin(b.leftBoard, depth-1, alpha, beta);
                if (tmp > val) {
                    val = tmp;
                }
                if (val >= beta) {
                    return val;
                }
                alpha = Double.max(alpha, val);
            }
            if (b.rightBoard != null) { // right
                double tmp = maximin(b.rightBoard, depth-1, alpha, beta);
                if (tmp > val) {
                    val = tmp;
                }
                if (val >= beta) {
                    return val;
                }
                alpha = Double.max(alpha, val);
            }
            if (b.downBoard != null) { // down
                double tmp = maximin(b.downBoard, depth-1, alpha, beta);
                if (tmp > val) {
                    val = tmp;
                }
                if (val >= beta) {
                    return val;
                }
                alpha = Double.max(alpha, val);
            }
            if (b.upBoard != null) { // up
                double tmp = maximin(b.upBoard, depth-1, alpha, beta);
                if (tmp > val) {
                    val = tmp;
                }
                if (val >= beta) {
                    return val;
                }
                alpha = Double.max(alpha, val);
            }

            return val; // return value of best move
        }
    }

    private double maximin(Board b, int depth, double alpha, double beta) {
        if (depth == 0) { // maximum depth is reached
            return eval(b);
        }
        else { // check all tile placements to see which is worst
            double val = Double.MAX_VALUE;
            for (int i = 0; i < b.BOARD_SIZE; i++) {
                for (int j = 0; j < b.BOARD_SIZE; j++) {
                    if (b.board[i][j] == 0) {
                        b.board[i][j] = 2; // place tile
                        double tmp = minimax(b, depth-1, alpha, beta);

                        if (tmp < val) {
                            val = tmp;
                        }

                        b.board[i][j] = 0; // remove tile to test next tile square
                        if (val <= alpha) {
                            return val;
                        }
                        beta = Double.min(beta, val);
                    }
                }
            }
            return val; // return value of worst move
        }
    }

    private int[][] deepCopy() { // creates a deep copy of the board
        int[][] ret = new int[b.BOARD_SIZE][b.BOARD_SIZE];

        for (int i = 0; i < b.BOARD_SIZE; i++) {
            for (int j = 0; j < b.BOARD_SIZE; j++) {
                ret[i][j] = b.board[i][j];
            }
        }

        return ret;
    }

    private static void demo() { // plays the game from start to finish
        MinimaxAI m = new MinimaxAI();

        while (!m.b.checkGameOver()) {
            m.play();
            m.b.printBoard();
        }
    }

    /**
     * Plays <tt> n </tt> games. In each game, selects a board state from the second half of the game randomly and
     * records into file specified by <tt> pathName </tt>. Also records final score each game, and the number of moves
     * from the recorded gamestate until the end of the game. Throws <tt> IOException </tt> if unable to successfully
     * write to the file.
     */
    private static void writeData(int n, String pathName) throws IOException {
        FileWriter fw = new FileWriter(pathName, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        for (int num = 0; num < n; num++) {
            MinimaxAI m = new MinimaxAI();
            List<int[][]> savedBoards = new ArrayList<>(1500);

            while (!m.b.checkGameOver()) {
                m.play();
                savedBoards.add(m.deepCopy());
            }

            int boardIndex = (int) ((Math.random() * 0.5 * savedBoards.size()) + (0.5 * savedBoards.size()));
            int[][] board = savedBoards.get(boardIndex);
            int y1 = m.b.score;
            int y2 = savedBoards.size() - boardIndex;

            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    pw.print(board[i][j] + " ");
                }
            }

            pw.println(y1 + " " + y2);
        }

        pw.close();
    }

    public static void main(String[] args) {
        demo();
    }

}
