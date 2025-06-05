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

    public double getVoltage() {
        return voltage;
    }

    @Override
    public int numberOfTerminals() {
        return 2;
    }
}
