package circuit;

import elements.Resistor;
import elements.VoltageSource;

import java.util.HashSet;
import java.util.Set;

public class Circuit {
    private final Set<Element> elements;
    private final Set<Node> nodes;

    public Circuit() {
        elements = new HashSet<>();
        nodes = new HashSet<>();
    }

    public boolean addElement(Element e) {
        nodes.addAll(e.getTerminals());
        return elements.add(e);
    }

    public static void main(String[] args) {
        Circuit c = new Circuit();
        Element vs = new VoltageSource(12);
        Element r = new Resistor(6e3);
        c.addElement(vs);
        c.addElement(r);

        Node old = vs.connectTerminals(0, r, 0);
        c.nodes.remove(old);
        old = vs.connectTerminals(1, r, 1);
        c.nodes.remove(old);
    }
}
