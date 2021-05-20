/** Rectified linear unit activation function. */
public class ReLU implements ActivationFunction {
    @Override
    public double activation(double x) {
        return (x < 0) ? 0 : x;
    }
    @Override
    public double derivative(double x) {
        return (x < 0) ? 0 : 1;
    }
}
