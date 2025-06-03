package circuit;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {
    private final List<Node> terminals;

    public Element() {
        int n = numberOfTerminals();
        terminals = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            terminals.add(new Node(this));
        }
    }

    public Element(Node... nodes) {
        int n = numberOfTerminals();
        if (nodes.length != n) {
            String msg = String.format("Element requires %d nodes, %d %s provided", n, nodes.length, nodes.length == 1 ? "was" : "were");
            throw new IllegalArgumentException(msg);
        }

        terminals = new ArrayList<>(n);

        for (Node node : nodes) {
            terminals.add(node);
            node.addConnection(this);
        }
    }

    public abstract int numberOfTerminals();

    /**
     * Connect two elements together at their specified terminals.
     * This method will combine the two terminal nodes.
     *
     * @param thisTerminal The node on this element
     * @param otherElement The target element to connect
     * @param otherTerminal The node on the other element
     * @return The Node that was removed
     */
    public Node connectTerminals(int thisTerminal, Element otherElement, int otherTerminal) {
        // assert 0 <= terminal < n
        Node n1 = this.terminals.get(thisTerminal);
        Node n2 = otherElement.terminals.get(otherTerminal);

        Node combined = Node.combine(n1, n2);
        Node old = n1.equals(combined) ? n2 : n1;

        this.terminals.set(thisTerminal, combined);
        otherElement.terminals.set(otherTerminal, combined);

        return old;
    }

    public List<Node> getTerminals() {
        return terminals;
    }
}
