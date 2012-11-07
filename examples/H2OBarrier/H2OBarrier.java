
package examples.H2OBarrier;


public abstract class H2OBarrier {
    protected int wO;
    protected int wH;
    protected int aH;
    protected int aO;
    protected int numWater;

    public abstract void OReady();
    public abstract void HReady();

}
