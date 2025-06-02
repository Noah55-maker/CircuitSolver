package elements;

import circuit.Element;

public class CurrentSource extends Element {
    private final double current;

    public CurrentSource(double current) {
        this.current = current;
    }

    // polarity may have to be factored into this - ie take a param with terminal #
    public double getCurrent() {
        return current;
    }
}
