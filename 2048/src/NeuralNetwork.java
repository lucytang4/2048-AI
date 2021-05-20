import java.util.*;
import java.io.*;

/**
 * A forward pass fully-connected neural network. Can be initialized to use any number of layers with any number of
 * neurons in each layer. Can be initialized to use pre-calculated weights from file. Default activation function is
 * ReLU if not specified. All neurons use the same activation function.
 */
public class NeuralNetwork {
    /** Layers of the neural network. */
    List<List<Neuron>> network = new ArrayList<>();
    /** Activation function of all neurons within the network. */
    ActivationFunction f = new ReLU();

    /**
     * Initializes the neural network using pre-calculated weights from <tt> pathName </tt>. Use <tt> f </tt> as
     * activation function.
     */
    public NeuralNetwork(String pathName, ActivationFunction f) {
        this.f = f;
        try {
            // read file
            FileReader fr = new FileReader(pathName);
            BufferedReader br = new BufferedReader(fr);

            // read architecture and initialize neurons
            String[] s = br.readLine().split("\\s+");
            for (String l : s) {
                Integer size = Integer.parseInt(l);
                List<Neuron> layer = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    Neuron n = new Neuron();
                    layer.add(n);
                }
                network.add(layer);
            }

            // initialize weights
            for (int i = 1; i < network.size(); i++) {
                for (int j = 0; j < network.get(i-1).size(); j++) {
                    Neuron n = network.get(i-1).get(j);
                    for (int k = 0; k < network.get(i).size(); k++) {
                        Neuron m = network.get(i).get(k);
                        double weight = Double.parseDouble(br.readLine());
                        n.weights.put(m,weight);
                        m.inputWeights.put(n,weight);
                    }
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the neural network using <tt> arch </tt> to specify how many neurons to use in each layer. Uses
     * <tt> f </tt> as activation function. Adds bias term of <tt> dummyValue </tt> connected to each neuron.
     */
    public NeuralNetwork(int[] arch, int dummyValue, ActivationFunction f) {
        this.f = f;
        // bias
        Neuron dummy = new Neuron();
        dummy.val = dummyValue;
        dummy.output = dummyValue;


        for (Integer i : arch) {
            List<Neuron> layer = new ArrayList<>(); // create layer

            for (int num = 0; num < i; num++) {
                Neuron n = new Neuron();
                layer.add(n);

                n.inputWeights.put(dummy, Math.random()-0.5); // connect with bias
                if (network.size() != 0) {
                    for (Neuron m : network.get(network.size()-1)) { // connect with all neurons in previous layer
                        Double weight = Math.random()-0.5; // random number between -0.5 and 0.5 chosen uniformly
                        m.weights.put(n, weight);
                        n.inputWeights.put(m, weight);
                    }
                }
            }

            network.add(layer); // append layer
        }
    }

    /**
     * Calculates the output of <tt> x </tt> when inputted into the neural network. Side-effect: alters the <tt> val
     * </tt> and <tt> output </tt> field for each neuron.
     */
    public double forwardProp(double[] x) {
        int count = 0;

        for (Neuron n : network.get(0)) { // input layer
            n.val = x[count];
            n.output = x[count];
            count++;
        }

        for (int i = 1; i < network.size(); i++) { // propagate forward
            for (Neuron n : network.get(i)) {
                n.val = 0;

                for (Neuron m : n.inputWeights.keySet()) {
                    n.val += m.output * n.inputWeights.get(m);
                }

                n.output = f.activation(n.val);
            }
        }

        for (Neuron n : network.get(network.size()-1)) { // return value at output node
            return n.val;
        }

        return -1; // faulty output
    }

    /**
     * Gradient descent algorithm using back-propagation to minimize squared error of training data <tt> x </tt>.
     * Executes <tt> epoch </tt> number of updates and scales each update by <tt> lr </tt>.
     */
    public void backProp(List<double[]> x, List<Double> expected, int epoch, double lr) {
        Map<Neuron, Double> delta = new HashMap<>();

        while (epoch > 0) {
            for (int i = 0; i < x.size(); i++) {
                double output = forwardProp(x.get(i)); // output of neural network
                lastLayerGrad(output, expected.get(i), delta); // calculate delta of output layer

                for (int j = network.size()-2; j >= 0; j--) { // propagate backwards
                    hiddenLayerGrad(j,delta);
                }

                updateWeights(delta,lr); // update
            }

            System.out.println(epoch); // used to keep track of progress when running
            epoch--;
        }
    }

    private void lastLayerGrad(double output, double expected, Map<Neuron, Double> delta) {
        for (Neuron n : network.get(network.size()-1)) {
            delta.put(n,f.derivative(n.val)*(expected-output));
        }
    }

    private void hiddenLayerGrad(int j, Map<Neuron, Double> delta) {
        for (Neuron n : network.get(j)) {
            double tmp = 0;
            for (Neuron m : n.weights.keySet()) {
                tmp += delta.get(m) * n.weights.get(m);
            }

            delta.put(n,f.derivative(n.val)*tmp);
        }
    }

    private void updateWeights(Map<Neuron, Double> delta, double lr) {
        for (int i = 0; i < network.size()-1; i++) {
            for (Neuron n : network.get(i)) {
                for (Neuron m : n.weights.keySet()) {
                    double grad = n.output * delta.get(m);
                    double original = n.weights.get(m);
                    n.weights.put(m,original + grad*lr);
                    m.inputWeights.put(n, original + grad*lr);
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] arch = new int[]{2,2,2,1}; // testing on XOR function
        NeuralNetwork net = new NeuralNetwork(arch, 1, new Logistic());
        double[] x1 = new double[]{0,0};
        double[] x2 = new double[]{1,0};
        double[] x3 = new double[]{0,1};
        double[] x4 = new double[]{1,1};
        List<double[]> x = new ArrayList();
        x.add(x1);
        x.add(x2);
        x.add(x3);
        x.add(x4);
        List<Double> y = new ArrayList<>();
        y.add(0.0);
        y.add(1.0);
        y.add(1.0);
        y.add(0.0);
        net.backProp(x,y,10000, 1.5);

        for (double[] d : x) {
            System.out.println(net.forwardProp(d));
        }
    }
}


/**
 * A single neuron within the neural network. <tt> inputWeights </tt> keeps track of neurons in previous layers which
 * are connected. <tt> weights </tt> keeps track of neurons in future layers which are connected. <tt> val </tt>
 * retains the un-activated input the neuron while <tt> output </tt> is the final activated value passed on to future
 * neurons.
 */
class Neuron {
    double val, output;
    Map<Neuron, Double> weights = new HashMap<>();
    Map<Neuron, Double> inputWeights = new HashMap<>();
}
