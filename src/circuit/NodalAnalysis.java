package circuit;

import algebra.SystemOfEquationSolver;
import elements.*;

import java.util.Set;

public class NodalAnalysis {

    private static int nodeNum(Node n) {
        return Integer.parseInt(n.getLabel().split(" ")[1]);
    }

    public static void nodalAnalysis(Circuit c) {
        // The convention used in the equations will be I_in - I_out = 0
        Set<Node> nodes = c.getNodes();
        int numNodes = nodes.size();
        double[][] lhs = new double[numNodes][numNodes];
        double[] rhs = new double[numNodes];

        int counter = 0;
        for (Node n : nodes) {
            int nodeNum = nodeNum(n);
            System.out.println(n);

            if (n.isGrounded()) {
                System.out.println("Node voltage is 0");
                lhs[counter][nodeNum-1] = 1;
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

                    lhs[counter][nodeNum(e.terminal(Terminal.Passive_Positive))-1] += multiplier / r.getResistance();
                    lhs[counter][nodeNum(e.terminal(Terminal.Passive_Negative))-1] -= multiplier / r.getResistance();
                }
                else if (e instanceof CurrentSource cs) {
                    int direction = n.equals(e.terminal(Terminal.Active_Positive)) ? 1 : -1;
                    System.out.printf("Current = %+f\n", direction * cs.getCurrent());

                    rhs[counter] -= direction * cs.getCurrent();
                }
                else if (e instanceof VoltageSource vs) {
                    System.out.print("Create supernode: ");
                    System.out.printf("%s = %s + %f\n", e.terminal(Terminal.Active_Positive), e.terminal(Terminal.Active_Negative), vs.getVoltage());

                    lhs[counter] = new double[numNodes];
                    lhs[counter][nodeNum(e.terminal(Terminal.Active_Positive))-1] = 1;
                    lhs[counter][nodeNum(e.terminal(Terminal.Active_Negative))-1] = -1;
                    rhs[counter] = vs.getVoltage();
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
