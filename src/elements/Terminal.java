package elements;

public enum Terminal {
    Passive_Positive(0),
    Passive_Negative(1),

    Active_Negative(0),
    Active_Positive(1);

    private final int terminal;
    Terminal(int terminal) {
        this.terminal = terminal;
    }

    public int getValue() {
        return terminal;
    }
}
