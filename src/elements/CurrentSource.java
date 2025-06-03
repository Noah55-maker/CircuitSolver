package elements;

import circuit.Element;
import circuit.Node;

public class CurrentSource extends Element {
    private final double current;

    public CurrentSource(double current) {
        super();
        this.current = current;
    }

    public CurrentSource(double current, Node... nodes) {
        super(nodes);
        this.current = current;
    }

    // polarity may have to be factored into this - ie take a param with terminal #
    public double getCurrent() {
        return current;
    }

    @Override
    public int numberOfTerminals() {
        return 2;
    }
}
