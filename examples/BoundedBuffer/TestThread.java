
/**
 * A thread with delays.
 */
package examples.BoundedBuffer;

public class TestThread extends Thread {
    protected void delay(int dt) {
        try { 
            if (dt > 1000000) {
                Thread.sleep(dt / 1000000, dt % 1000000);
            } else {
                Thread.sleep(0, dt); 
            }
        }
        catch( InterruptedException e ) { } 
    }
}
