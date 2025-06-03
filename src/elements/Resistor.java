package elements;

import circuit.Element;
import circuit.Node;

public class Resistor extends Element {
    private final double resistance;

    public Resistor(double resistance) {
        super();
        this.resistance = resistance;
    }

    public Resistor(double resistance, Node... nodes) {
        super(nodes);
        this.resistance = resistance;
    }

    public double getResistance() {
        return resistance;
    }
}
