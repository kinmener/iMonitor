
/**
 * A thread with delays.
 */

public class TestThread extends Thread {
    protected void delay(int maxMilisec ) {
        int x = maxMilisec;
        //int x = (int)(Math.random() * maxMilisec ) ;
        try { Thread.sleep(x); }
        catch( InterruptedException e ) { } }
}
