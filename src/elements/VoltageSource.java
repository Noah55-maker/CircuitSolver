package elements;

import circuit.Element;
import circuit.Node;

public class VoltageSource extends Element {
    private final double voltage;

    public VoltageSource(double voltage) {
        super();
        this.voltage = voltage;
    }

    public VoltageSource(double voltage, Node... nodes) {
        super(nodes);
        this.voltage = voltage;
    }

    // polarity may have to be factored into this - ie take a param with terminal #
    public double getVoltage() {
        return voltage;
    }
}
