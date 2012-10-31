package monitor;

public abstract class AbstractCondition {
    protected static long numContextSwitch = 0;

    public abstract void await();
    public static long getNumContextSwitch() {
        return numContextSwitch;
    }
}
