package circuit;

import elements.*;
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
        /*
        Circuit c = new Circuit();
        Element vs = new VoltageSource(12);
        Element r = new Resistor(6e3);
        c.addElement(vs);
        c.addElement(r);

        Node old = vs.connectTerminals(0, r, 0);
        c.nodes.remove(old);
        old = vs.connectTerminals(1, r, 1);
        c.nodes.remove(old);
        */

        Node A = new Node();
        Node B = new Node();
        Node C = new Node();
        Node G = new Node();

        Element cs1 = new CurrentSource(8e-3, G, A);
        Element cs2 = new CurrentSource(2e-3, A, C);
        Element g = new Ground(G);

        Element r1 = new Resistor(3e3, G, A);
        Element r2 = new Resistor(6e3, A, B);
        Element r3 = new Resistor(6e3, G, B);
        Element r4 = new Resistor(2e3, B, C);
        Element r5 = new Resistor(1e3, G, C);
    }
}
