
/**
 * A thread with delays.
 */
package examples.BoundedBuffer;

public class TestThread extends Thread {
    long  cpuTime;
    public long getCpuTime() {
        return cpuTime; 
    }
    protected void delay(int maxMilisec ) {
        int x = (int)(Math.random() * maxMilisec ) ;
        try { Thread.sleep(x); }
        catch( InterruptedException e ) { } 
    }
}
