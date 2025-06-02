package elements;

import circuit.Element;

public class VoltageSource extends Element {
    private final double voltage;

    public VoltageSource(double voltage) {
        this.voltage = voltage;
    }

    // polarity may have to be factored into this - ie take a param with terminal #
    public double getVoltage() {
        return voltage;
    }
}
