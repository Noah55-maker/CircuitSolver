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

    }
}
