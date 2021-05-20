import java.util.Random;

/** An AI which plays random moves. */
public class RandomMovesAI {
    /** Board which AI plays on. */
    private Board b;

    /** Default constructor: plays game on 4x4 board. */
    public RandomMovesAI() {
        b = new Board();
    }

    /** AI plays a random move. */
    public void playRandom() {
        while (!b.checkGameOver()) {
            int size = 0;
            char[] moves = new char[4];

            if (b.leftBoard != null) {
                moves[size] = 'L';
                size++;
            }
            if (b.rightBoard != null) {
                moves[size] = 'R';
                size++;
            }
            if (b.downBoard != null) {
                moves[size] = 'D';
                size++;
            }
            if (b.upBoard != null) {
                moves[size] = 'U';
                size++;
            }

            b.play(moves[(int)(Math.random() * size)]);
        }

        b.printBoard();
    }

    public static void main(String[] args) {
        RandomMovesAI r = new RandomMovesAI();
        r.playRandom();
    }
}
