
/**
 * A thread with delays.
 */
package examples.BoundedBuffer;

public class TestThread extends Thread {
    protected void delay(int maxMilisec ) {
        int x = (int)(Math.random() * maxMilisec ) ;
   //     try { Thread.sleep(x); }
   //     catch( InterruptedException e ) { } 
    }
}
