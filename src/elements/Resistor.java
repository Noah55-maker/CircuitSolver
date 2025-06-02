package elements;

import circuit.Element;

public class Resistor extends Element {
    private final double resistance;

    public Resistor(double resistance) {
        this.resistance = resistance;
    }

    public double getResistance() {
        return resistance;
    }
}
