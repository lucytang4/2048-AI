/**
 * A differentiable mathematical function. May be used in any application where evaluation of the function itself and
 * its derivative are pertinent. Does not guarantee calculation of derivative is correct.
 */
public interface ActivationFunction {
    /** Returns the function applied on <tt> x </tt>. */
    public double activation(double x);
    /** Returns the function derivative applied on <tt> x </tt>. */
    public double derivative(double x);
}
