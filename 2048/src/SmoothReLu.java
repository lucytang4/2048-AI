/** Soft-plus smooth approximation to ReLU activation function. */
public class SmoothReLu implements ActivationFunction {
    @Override
    public double activation(double x) {
        return Math.log(1+Math.exp(x));
    }
    @Override
    public double derivative(double x) {
        return 1.0/(1+Math.exp(-x));
    }
}
