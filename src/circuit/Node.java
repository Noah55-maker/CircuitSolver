package circuit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Node {
    private final Set<Element> connections;
    private final String label;
    private static int count;

    public Node(Element... elements) {
        connections = new HashSet<>();
        Collections.addAll(connections, elements);
        label = "Node " + ++count;
    }

    public void addConnection(Element e) {
        connections.add(e);
    }

    public static Node combine(Node n1, Node n2) {
        if (n1.connections.size() >= n2.connections.size()) {
            n1.connections.addAll(n2.connections);
            return n1;
        }
        n2.connections.addAll(n1.connections);
        return n2;
    }

    public Set<Element> getConnections() {
        return Set.copyOf(connections);
    }

    public String getLabel() {
        return label;
    }
}
