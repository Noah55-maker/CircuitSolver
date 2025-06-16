package circuit;

import algebra.SystemOfEquationSolver;
import elements.*;

import java.util.ArrayList;
import java.util.List;

public class NodalAnalysis {
    private final List<Node> nodes;
    private final List<Element> elements;
    private final int numNodes;
    private final double[][] lhs;
    private final double[] rhs;
    private final boolean[] visited;

    private static int nodeIndex(Node n) {
        return Integer.parseInt(n.getLabel().split(" ")[1]) - 1;
    }

    private NodalAnalysis(Circuit c) {
        nodes = new ArrayList<>(c.getNodes());
        elements = new ArrayList<>(c.getElements());
        numNodes = nodes.size();
        lhs = new double[numNodes][numNodes];
        rhs = new double[numNodes];
        visited = new boolean[numNodes];
    }

    public static void solve(Circuit c) {
        NodalAnalysis nodal = new NodalAnalysis(c);
        nodal.solve();
    }

    private void findSupernodes(int i) {
        if (visited[i])
            return;

        visited[i] = true;

        Node n = nodes.get(i);

        for (Element e : n.getConnections()) {
            if (e instanceof VoltageSource || e instanceof VoltageDependentVoltageSource || e instanceof VoltageDependentCurrentSource) {
                Node otherNode = e.getTerminals().get(0).equals(n) ? e.getTerminals().get(1) : e.getTerminals().get(0);
                int otherIndex = nodes.indexOf(otherNode);

                if (visited[otherIndex])
                    continue;

                System.out.println("Supernode over element " + e);
                findSupernodes(otherIndex);
            }
        }
    }

    private void solve() {

        for (int i = 0; i < nodes.size(); i++) {
            findSupernodes(i);
        }
        System.out.println();

        int counter = 0;
        for (Node n : nodes) {
            createNodeEquation(n, counter);
            counter++;
        }

        SystemOfEquationSolver.solve(lhs, rhs);
    }

    private void createNodeEquation(Node n, int eqIndex) {
        System.out.println(n);

        if (n.isGrounded()) {
            System.out.println("Node voltage is 0");
            lhs[eqIndex][nodeIndex(n)] = 1;
            rhs[eqIndex] = 0;

            System.out.println();
            return;
        }

        for (Element e : n.getConnections()) {
            System.out.println(e);
            System.out.print('\t');

            if (e instanceof Resistor r) {
                char direction = n.equals(e.terminal(Terminal.Passive_Negative)) ? '+' : '-';
                int multiplier = n.equals(e.terminal(Terminal.Passive_Negative)) ? 1 : -1;
                System.out.printf("Current = %c(%s - %s)/%f\n", direction,
                        e.terminal(Terminal.Passive_Positive), e.terminal(Terminal.Passive_Negative), r.getResistance());

                lhs[eqIndex][nodeIndex(e.terminal(Terminal.Passive_Positive))] += multiplier / r.getResistance();
                lhs[eqIndex][nodeIndex(e.terminal(Terminal.Passive_Negative))] -= multiplier / r.getResistance();
            }
            else if (e instanceof CurrentSource cs) {
                int direction = n.equals(e.terminal(Terminal.Active_Positive)) ? 1 : -1;
                System.out.printf("Current = %+f\n", direction * cs.getCurrent());

                rhs[eqIndex] -= direction * cs.getCurrent();
            }
            else if (e instanceof VoltageSource vs) {
                System.out.print("Create supernode: ");
                System.out.printf("%s - %s = %f\n", e.terminal(Terminal.Active_Positive), e.terminal(Terminal.Active_Negative), vs.getVoltage());

                lhs[eqIndex] = new double[numNodes];
                lhs[eqIndex][nodeIndex(e.terminal(Terminal.Active_Positive))] = 1;
                lhs[eqIndex][nodeIndex(e.terminal(Terminal.Active_Negative))] = -1;
                rhs[eqIndex] = vs.getVoltage();
                break;
            }
            else if (e instanceof VoltageDependentCurrentSource vdcs) {
                char direction = n.equals(e.terminal(Terminal.Active_Positive)) ? '+' : '-';
                int multiplier = n.equals(e.terminal(Terminal.Active_Positive)) ? 1 : -1;
                System.out.printf("Current = %c(%s - %s)*%f\n", direction, vdcs.getV_high(), vdcs.getV_low(), vdcs.getCoefficient());

                lhs[eqIndex][nodeIndex(vdcs.getV_high())] += multiplier * vdcs.getCoefficient();
                lhs[eqIndex][nodeIndex(vdcs.getV_low())] -= multiplier * vdcs.getCoefficient();
            }
            else if (e instanceof VoltageDependentVoltageSource vdvs) {
                System.out.print("Create supernode: ");
                System.out.printf("%s - %s = %f*(%s - %s)\n", e.terminal(Terminal.Active_Positive), e.terminal(Terminal.Active_Negative), vdvs.getCoefficient(),
                        vdvs.getV_high(), vdvs.getV_low());

                // (V+ - V-) + c*(V_low - V_high) = 0
                lhs[eqIndex] = new double[numNodes];
                lhs[eqIndex][nodeIndex(e.terminal(Terminal.Active_Positive))] = 1;
                lhs[eqIndex][nodeIndex(e.terminal(Terminal.Active_Negative))] = -1;
                lhs[eqIndex][nodeIndex(vdvs.getV_low())] += vdvs.getCoefficient();
                lhs[eqIndex][nodeIndex(vdvs.getV_high())] -= vdvs.getCoefficient();
                rhs[eqIndex] = 0;
                break;
            }
            else {
                System.out.printf("Element of type %s is ignored\n",  e.getClass().getSimpleName());
            }
        }
        System.out.println();
    }
}
