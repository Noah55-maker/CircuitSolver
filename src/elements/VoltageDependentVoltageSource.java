package elements;

import circuit.Element;
import circuit.Node;

public class VoltageDependentVoltageSource extends Element {
    private final Node v_high;
    private final Node v_low;
    private final double coefficient;

    public VoltageDependentVoltageSource(Node v_high, Node v_low, double coefficient, Node... nodes) {
        super(nodes);
        this.v_high = v_high;
        this.v_low = v_low;
        this.coefficient = coefficient;
    }

    public Node getV_high() {
        return v_high;
    }

    public Node getV_low() {
        return v_low;
    }

    public double getCoefficient() {
        return coefficient;
    }

    @Override
    public int numberOfTerminals() {
        return 2;
    }
}
