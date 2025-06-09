package circuit;

import algebra.SystemOfEquationSolver;
import elements.*;

import java.util.Set;

public class NodalAnalysis {

    private static int nodeIndex(Node n) {
        return Integer.parseInt(n.getLabel().split(" ")[1]) - 1;
    }

    public static void nodalAnalysis(Circuit c) {
        // The convention used in the equations will be I_in - I_out = 0
        Set<Node> nodes = c.getNodes();
        int numNodes = nodes.size();
        double[][] lhs = new double[numNodes][numNodes];
        double[] rhs = new double[numNodes];

        int counter = 0;
        for (Node n : nodes) {
            System.out.println(n);

            if (n.isGrounded()) {
                System.out.println("Node voltage is 0");
                lhs[counter][nodeIndex(n)] = 1;
                rhs[counter] = 0;

                System.out.println();
                counter++;
                continue;
            }

            for (Element e : n.getConnections()) {
                System.out.println(e);
                System.out.print('\t');

                if (e instanceof Resistor r) {
                    char direction = n.equals(e.terminal(Terminal.Passive_Negative)) ? '+' : '-';
                    int multiplier = n.equals(e.terminal(Terminal.Passive_Negative)) ? 1 : -1;
                    System.out.printf("Current = %c(%s - %s)/%f\n", direction,
                            e.terminal(Terminal.Passive_Positive), e.terminal(Terminal.Passive_Negative), r.getResistance());

                    lhs[counter][nodeIndex(e.terminal(Terminal.Passive_Positive))] += multiplier / r.getResistance();
                    lhs[counter][nodeIndex(e.terminal(Terminal.Passive_Negative))] -= multiplier / r.getResistance();
                }
                else if (e instanceof CurrentSource cs) {
                    int direction = n.equals(e.terminal(Terminal.Active_Positive)) ? 1 : -1;
                    System.out.printf("Current = %+f\n", direction * cs.getCurrent());

                    rhs[counter] -= direction * cs.getCurrent();
                }
                else if (e instanceof VoltageSource vs) {
                    System.out.print("Create supernode: ");
                    System.out.printf("%s - %s = %f\n", e.terminal(Terminal.Active_Positive), e.terminal(Terminal.Active_Negative), vs.getVoltage());

                    lhs[counter] = new double[numNodes];
                    lhs[counter][nodeIndex(e.terminal(Terminal.Active_Positive))] = 1;
                    lhs[counter][nodeIndex(e.terminal(Terminal.Active_Negative))] = -1;
                    rhs[counter] = vs.getVoltage();
                    break;
                }
                else if (e instanceof VoltageDependentCurrentSource vdcs) {
                    char direction = n.equals(e.terminal(Terminal.Active_Positive)) ? '+' : '-';
                    int multiplier = n.equals(e.terminal(Terminal.Active_Positive)) ? 1 : -1;
                    System.out.printf("Current = %c(%s - %s)*%f\n", direction, vdcs.getV_high(), vdcs.getV_low(), vdcs.getCoefficient());

                    lhs[counter][nodeIndex(vdcs.getV_high())] += multiplier * vdcs.getCoefficient();
                    lhs[counter][nodeIndex(vdcs.getV_low())] -= multiplier * vdcs.getCoefficient();
                }
                else if (e instanceof VoltageDependentVoltageSource vdvs) {
                    System.out.print("Create supernode: ");
                    System.out.printf("%s - %s = %f*(%s - %s)\n", e.terminal(Terminal.Active_Positive), e.terminal(Terminal.Active_Negative), vdvs.getCoefficient(),
                            vdvs.getV_high(), vdvs.getV_low());

                    // (V+ - V-) + c*(V_low - V_high) = 0
                    lhs[counter] = new double[numNodes];
                    lhs[counter][nodeIndex(e.terminal(Terminal.Active_Positive))] = 1;
                    lhs[counter][nodeIndex(e.terminal(Terminal.Active_Negative))] = -1;
                    lhs[counter][nodeIndex(vdvs.getV_low())] += vdvs.getCoefficient();
                    lhs[counter][nodeIndex(vdvs.getV_high())] -= vdvs.getCoefficient();
                    rhs[counter] = 0;
                    break;
                }
                else {
                    System.out.printf("Element of type %s is ignored\n",  e.getClass().getSimpleName());
                }
            }
            System.out.println();
            counter++;
        }

        SystemOfEquationSolver.solve(lhs, rhs);
    }
}
