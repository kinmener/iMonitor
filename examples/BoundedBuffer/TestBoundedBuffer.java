
/**
 * <p>Title: Monitor package and examples</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Memorial University of Newfoundland</p>
 * @author Theodore S. Norvell
 * @version 1.0
 */

/** Test a bounded buffer with multiple producers and consumers.
*/

package examples.BoundedBuffer;

import examples.util.DoneCounter;

public class TestBoundedBuffer {

    public static void main(String[] args) {
        int CONSUMERS = 10;
        int PRODUCERS = 10;
        int bufSize = 10;
        int totalNumActions = 10; 
        int delay = 0;
        ObjectBoundedBuffer rw_controller = null;

        try {
            bufSize = Integer.parseInt(args[0]);
            switch(args[1].charAt(0)) {
                case 'e':
                    rw_controller = new ExplicitBoundedBuffer(bufSize);
                    break;
                default:
                    rw_controller = 
                        new iMonitorBoundedBuffer(bufSize, args[1].charAt(0));
            }
            CONSUMERS = PRODUCERS = Integer.parseInt(args[2]);
            totalNumActions = Integer.parseInt(args[3]);
            delay = Integer.parseInt(args[4]);
        } catch (Exception e) { /* use defaults */ 
            e.printStackTrace();
            if(rw_controller == null) {
                rw_controller = new ExplicitBoundedBuffer(bufSize);
            }
        }

        DoneCounter doneCounter = new DoneCounter() ;




        doneCounter.set( CONSUMERS + PRODUCERS ) ;
        TestThread[] threads = new TestThread[CONSUMERS + PRODUCERS];
        long startTime = System.currentTimeMillis();
        //System.out.println("Please wait. This takes a while");
        for( int k=0 ; k < CONSUMERS ; ++k ) {
            TestThread w = new ObjectConsumer( rw_controller, doneCounter, totalNumActions/CONSUMERS, delay) ;
            threads[k] = w;
            w.start(); }
            for( int k=0 ; k < PRODUCERS ; ++k ) {
                TestThread r = new ObjectProducer( rw_controller, doneCounter, totalNumActions/PRODUCERS, delay) ;
                threads[k + CONSUMERS] = r;
                r.start(); }
                doneCounter.waitForDone() ;
                long execTime = System.currentTimeMillis() - startTime;

                System.out.println( execTime );
    }
}

class ObjectProducer extends TestThread {

    private ObjectBoundedBuffer boundedBuffer ;
    private DoneCounter doneCounter ;
    private int numActions;
    private int delayT;

    ObjectProducer( ObjectBoundedBuffer bb, DoneCounter d, int n, int dt) {
        boundedBuffer = bb ; doneCounter = d ; numActions = n; delayT = dt;}

    public void run() {
        for(int i=0 ; i < numActions ; ++i ) {
            if (delayT != 0) {
               delay(delayT);
            } 

            try {
                boundedBuffer.put(new Object()) ; }
            catch(InterruptedException e ) { }
        }
        doneCounter.increment() ;
    }
}

class ObjectConsumer extends TestThread {

    private ObjectBoundedBuffer boundedBuffer ;
    private DoneCounter doneCounter ;
    private int numActions;
    private int delayT;

    ObjectConsumer( ObjectBoundedBuffer bb, DoneCounter d, int n, int dt) {
        boundedBuffer = bb ; doneCounter = d ; numActions = n; delayT = dt;}

    public void run() {

        for(int i=0 ; i < numActions ; ++i ) {
            if (delayT != 0) {
               delay(delayT);
            }

            try {
                boundedBuffer.take() ; }
            catch(InterruptedException e ) { }

        }
        doneCounter.increment() ;
    }
}
