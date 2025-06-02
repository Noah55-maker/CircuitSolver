package circuit;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {
    private final List<Node> terminals;

    public Element() {
//        int n = numberOfTerminals();
        int n = 2;
        terminals = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            terminals.add(new Node());
        }
    }

//    public abstract int numberOfTerminals();

    public void connectTerminals(int thisTerminal, Element otherElement, int otherTerminal) {
        // assert 0 <= terminal < n
        Node n1 = this.terminals.get(thisTerminal);
        Node n2 = otherElement.terminals.get(otherTerminal);

        Node combined = Node.combine(n1, n2);
        Node old = n1.equals(combined) ? n2 : n1;

        this.terminals.set(thisTerminal, combined);
        otherElement.terminals.set(otherTerminal, combined);
    }

    public List<Node> getTerminals() {
        return terminals;
    }
}
