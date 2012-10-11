
/**
 * A thread with delays.
 */
package examples.BoundedBuffer;

public class TestThread extends Thread {
    protected void delay(int dt) {
        try { Thread.sleep(0, dt * 1000); }
        catch( InterruptedException e ) { } 
    }
}
