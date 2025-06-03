package circuit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Node {
    private final Set<Element> connections;

    public Node(Element... elements) {
        connections = new HashSet<>();
        Collections.addAll(connections, elements);
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
}
