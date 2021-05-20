/** Logistic activation function. */
public class Logistic implements ActivationFunction {
    @Override
    public double activation(double x) {
        return 1.0/(1+Math.exp(-x));
    }
    @Override
    public double derivative(double x) {
        double tmp = activation(x);
        return tmp*(1-tmp);
    }
}
