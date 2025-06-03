package elements;

import circuit.Element;
import circuit.Node;

public class Ground extends Element {

    public Ground() {
        super();
    }

    public Ground(Node... nodes) {
        super(nodes);
    }

    @Override
    public int numberOfTerminals() {
        return 1;
    }
}
