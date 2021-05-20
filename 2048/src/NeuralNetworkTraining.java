import java.io.*;
import java.util.*;

/** Trains a neural network. */
public class NeuralNetworkTraining {
    private static List<double[]> xTr, xTe; // training and test x values
    private static List<Double> yTr1, yTe1, yTr2, yTe2; // training and test y values

    /**
     * Returns a score between 1 and 5 based on the value of <tt> y </tt>. The higher the value <tt> y </tt>, the
     * larger the output.
     */
    private static double classifyScore(double y) {
        if (y < 20000) {
            return 1;
        }
        else if (y < 40000) {
            return 2;
        }
        else if (y < 60000) {
            return 3;
        }
        else if (y < 80000) {
            return 4;
        }
        else {
            return 5;
        }
    }

    /**
     * Returns a score between 1 and 5 based on the value of <tt> y </tt>. The higher the value <tt> y </tt>, the
     * larger the output.
     */
    private static double classifyMoveScore(double y) {
        if (y < 300) {
            return 1;
        }
        else if (y < 600) {
            return 2;
        }
        else if (y < 900) {
            return 3;
        }
        else if (y < 1200) {
            return 4;
        }
        else {
            return 5;
        }

    }

    /** Reads sample data. Side-effect: initializes the training and test set fields. */
    private static void parse() {
        xTr = new ArrayList<>();
        xTe = new ArrayList<>();
        yTr1 = new ArrayList<>();
        yTe1 = new ArrayList<>();
        yTr2 = new ArrayList<>();
        yTe2 = new ArrayList<>();

        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader("Data/Results.txt");
            br = new BufferedReader(fr);

            // 500 training examples
            for (int i = 0; i < 500; i++) {
                String[] s = br.readLine().split("\\s+");
                double[] x = new double[s.length-2];
                for (int j = 0; j < x.length; j++) {
                    x[j] = Double.parseDouble(s[j]);
                }
                xTr.add(x);
                yTr1.add(classifyScore(Double.parseDouble(s[s.length-2])));
                yTr2.add(classifyMoveScore(Double.parseDouble(s[s.length-1])));
            }

            // remainder used as test set
            String line = br.readLine();
            while (line != null) {
                String[] s = line.split("\\s+");
                double[] x = new double[s.length-2];
                for (int j = 0; j < x.length; j++) {
                    x[j] = Double.parseDouble(s[j]);
                }
                xTe.add(x);
                yTe1.add(classifyScore(Double.parseDouble(s[s.length-2])));
                yTe2.add(classifyMoveScore(Double.parseDouble(s[s.length-1])));

                line = br.readLine();
            }
            br.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /** Returns true if <tt> output </tt> is within 20% of <tt> expected </tt>. */
    private static boolean within20Percent(double output, double expected) {
        return Math.abs(expected-output) <= 0.2*expected;
    }

    /**
     * Saves the weights and architecture of the neural network <tt> nn </tt> into file specified by <tt> filePath
     * </tt>
     */
    private static void writeNetworkToFile(String filePath, NeuralNetwork nn) {
        try {
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for (int i = 0; i < nn.network.size(); i++) { // print architecture
                pw.print(nn.network.get(i).size() + " ");
            }
            pw.println();

            for (int i = 1; i < nn.network.size(); i++) { // print weights
                for (int j = 0; j < nn.network.get(i-1).size(); j++) {
                    Neuron n = nn.network.get(i-1).get(j);
                    for (int k = 0; k < nn.network.get(i).size(); k++) {
                        Neuron m = nn.network.get(i).get(k);

                        if (Math.abs(n.weights.get(m) - m.inputWeights.get(n)) >= 0.001) { // double check consistency
                            System.out.println("Network is not consistent.");
                        }
                        pw.println(n.weights.get(m));
                    }
                }
            }

            pw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        parse();

        int[] arch = new int[]{16, 100, 50, 1};
        NeuralNetwork nn1 = new NeuralNetwork(arch, 0, new Logistic());
        NeuralNetwork nn2 = new NeuralNetwork(arch, 0, new Logistic());

        nn1.backProp(xTr, yTr1, 5000, 0.75);
        nn2.backProp(xTr, yTr2, 5000, 0.75);

        writeNetworkToFile("Data/NeuralNetwork1", nn1);
        writeNetworkToFile("Data/NeuralNetwork2", nn2);

        int yCorrect1 = 0;
        int yCorrect2 = 0;

        for (int i = 0; i < xTe.size(); i++) {
            double[] x = xTe.get(i);
            double y1 = nn1.forwardProp(x);
            double y2 = nn2.forwardProp(x);
            System.out.println("calculated output: " + y2 + ", true value: " + yTe2.get(i));

            if (Math.round(y1) == yTe1.get(i)) {
                yCorrect1++;
            }
            if (Math.round(y2) == yTe2.get(i)) {
                yCorrect2++;
            }
        }

        System.out.println("Total size: " + xTe.size());
        System.out.println("Y1 correct: " + yCorrect1);
        System.out.println("Y2 correct: " + yCorrect2);
    }
}
