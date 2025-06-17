package circuit;

import algebra.SystemOfEquationSolver;
import elements.*;

import java.util.*;

public class NodalAnalysis {
    private final List<Node> nodes;
    private final int numNodes;

    private final ArrayList<Set<Node>> supernodes;
    private final boolean[] visited;
    private int eqIndex;

    private final double[][] lhs;
    private final double[] rhs;

    private NodalAnalysis(Circuit c) {
        nodes = new ArrayList<>(c.getNodes());
        numNodes = nodes.size();
        lhs = new double[numNodes][numNodes];
        rhs = new double[numNodes];
        visited = new boolean[numNodes];

        supernodes = new ArrayList<>();
    }

    private int nodeIndex(Node n) {
        return Integer.parseInt(n.getLabel().split(" ")[1]) - 1;
    }

    public static void solve(Circuit c) {
        NodalAnalysis nodal = new NodalAnalysis(c);
        nodal.solve();
    }

    private void solve() {
        eqIndex = 0;
        for (int i = 0; i < nodes.size(); i++) {
            if (visited[i])
                continue;

            supernodes.add(new HashSet<>());
            findSupernodes(i);
        }
        System.out.println();

        for (int i = 0; i < supernodes.size(); i++) {
            System.out.print("Supernode group " + (i+1) + ": ");
            for (Node n : supernodes.get(i))
                System.out.print(n + ", ");
            System.out.println();
        }

        for (Set<Node> eq : supernodes) {
            System.out.println("Equation " + eqIndex);
            Node groundedNode = null;
            for (Node n : eq)
                if (n.isGrounded()) groundedNode = n;
            if (groundedNode != null) {
                System.out.println(groundedNode + " is grounded");
                lhs[eqIndex][nodeIndex(groundedNode)] = 1;
                rhs[eqIndex] = 0;
                eqIndex++;

                System.out.println();
                continue;
            }

            for (Node n : eq) {
                createNodeEquation(n, eqIndex);
            }
            eqIndex++;
        }

        SystemOfEquationSolver.solve(lhs, rhs);
    }

    private void findSupernodes(int i) {
        if (visited[i])
            return;

        Node n = nodes.get(i);

        visited[i] = true;
        supernodes.get(supernodes.size()-1).add(n);

        for (Element e : n.getConnections()) {
            if (e instanceof VoltageSource || e instanceof VoltageDependentVoltageSource) {
                Node otherNode = e.getTerminals().get(0).equals(n) ? e.getTerminals().get(1) : e.getTerminals().get(0);
                int otherIndex = nodes.indexOf(otherNode);

                if (visited[otherIndex])
                    continue;

                System.out.println("Supernode over element " + e);
                if (e instanceof VoltageSource vs) {
                    System.out.printf("\t%s - %s = %f\n", e.terminal(Terminal.Active_Positive), e.terminal(Terminal.Active_Negative), vs.getVoltage());

                    lhs[eqIndex][nodeIndex(e.terminal(Terminal.Active_Positive))] = 1;
                    lhs[eqIndex][nodeIndex(e.terminal(Terminal.Active_Negative))] = -1;
                    rhs[eqIndex] = vs.getVoltage();
                }
                else if (e instanceof VoltageDependentVoltageSource vdvs) {
                    System.out.printf("\t%s - %s = %f*(%s - %s)\n", e.terminal(Terminal.Active_Positive), e.terminal(Terminal.Active_Negative),
                            vdvs.getCoefficient(), vdvs.getV_high(), vdvs.getV_low());

                    // (V+ - V-) + c*(V_low - V_high) = 0
                    lhs[eqIndex][nodeIndex(e.terminal(Terminal.Active_Positive))] = 1;
                    lhs[eqIndex][nodeIndex(e.terminal(Terminal.Active_Negative))] = -1;
                    lhs[eqIndex][nodeIndex(vdvs.getV_low())] += vdvs.getCoefficient();
                    lhs[eqIndex][nodeIndex(vdvs.getV_high())] -= vdvs.getCoefficient();
                }
                eqIndex++;

                findSupernodes(otherIndex);
            }
        }
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
            else if (e instanceof VoltageDependentCurrentSource vdcs) {
                char direction = n.equals(e.terminal(Terminal.Active_Positive)) ? '+' : '-';
                int multiplier = n.equals(e.terminal(Terminal.Active_Positive)) ? 1 : -1;
                System.out.printf("Current = %c(%s - %s)*%f\n", direction, vdcs.getV_high(), vdcs.getV_low(), vdcs.getCoefficient());

                lhs[eqIndex][nodeIndex(vdcs.getV_high())] += multiplier * vdcs.getCoefficient();
                lhs[eqIndex][nodeIndex(vdcs.getV_low())] -= multiplier * vdcs.getCoefficient();
            }
            else if (e instanceof VoltageSource) {
                System.out.println("Element of type VoltageSource, skipping");
            }
            else if (e instanceof VoltageDependentVoltageSource) {
                System.out.println("Element of type VDVS, skipping");
            }
            else {
                System.out.printf("Element of type %s is ignored\n",  e.getClass().getSimpleName());
            }
        }
        System.out.println();
    }
}
