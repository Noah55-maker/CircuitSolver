package circuit;

import elements.*;

public class NodalAnalysis {

    public static void nodalAnalysis(Circuit c) {
        // Nodal analysis
        // The convention used in the 'equations' will be I_in - I_out = 0
        for (Node n : c.getNodes()) {
            System.out.println(n);
            for (Element e : n.getConnections()) {
                System.out.println(e);

                if (e instanceof Ground) {
                    System.out.print("Node voltage is 0");
                }
                else if (e instanceof Resistor r) {
                    char direction = n.equals(e.terminal(Terminal.Passive_Negative)) ? '+' : '-';
                    System.out.printf("Current = %c(%s - %s)/%f", direction,
                            e.terminal(Terminal.Passive_Positive), e.terminal(Terminal.Passive_Negative), r.getResistance());
                }
                else if (e instanceof CurrentSource cs) {
                    // current flows from - to +
                    int direction = n.equals(e.terminal(Terminal.Active_Positive)) ? 1 : -1;
                    System.out.printf("Current = %+f", direction * cs.getCurrent());
                }
                else if (e instanceof VoltageSource vs) {
                    System.out.print("Create supernode: ");
                    System.out.printf("%s = %s + %f", e.terminal(Terminal.Active_Positive), e.terminal(Terminal.Active_Negative), vs.getVoltage());
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
