
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
        int totalNumActions = 10; 
        ObjectBoundedBufferInterface rw_controller = null;

        try {
            switch(args[0].charAt(0)) {
            case 'n':
                rw_controller = new NaiveImplicitBoundedBuffer(4);
                break;
            case 'l':
                rw_controller = new HashSetBoundedBuffer(4);
                break;
            case 'h':
                rw_controller = new HashBoundedBuffer(4);
                break;
            default:
                rw_controller = new NaiveExplicitBoundedBuffer(4);

            }
            CONSUMERS = PRODUCERS = Integer.parseInt(args[1]);
            totalNumActions = Integer.parseInt(args[2]);
        } catch (Exception e) { /* use defaults */ 
            e.printStackTrace();
            if(rw_controller == null) {
                rw_controller = new NaiveExplicitBoundedBuffer(4);
            }
        }

        DoneCounter doneCounter = new DoneCounter() ;




        doneCounter.set( CONSUMERS + PRODUCERS ) ;
        long startTime = System.currentTimeMillis();
        System.out.println("Please wait. This takes a while");
        for( int k=0 ; k < CONSUMERS ; ++k ) {
            Thread w = new ObjectConsumer( rw_controller, doneCounter, totalNumActions/CONSUMERS ) ;
            w.start(); }
        for( int k=0 ; k < PRODUCERS ; ++k ) {
            Thread r = new ObjectProducer( rw_controller, doneCounter, totalNumActions/PRODUCERS) ;
            r.start(); }
        doneCounter.waitForDone() ;
        long execTime = System.currentTimeMillis() - startTime;
        System.out.println( execTime );
    }
}

class ObjectProducer extends TestThread {

    private ObjectBoundedBufferInterface boundedBuffer ;
    private DoneCounter doneCounter ;
    private int numActions = 10;

    ObjectProducer( ObjectBoundedBufferInterface bb, DoneCounter d, int n) {
        boundedBuffer = bb ; doneCounter = d ; numActions = n;}

    public void run() {
        for(int i=0 ; i < numActions ; ++i ) {
            delay(10) ;

            try {
                boundedBuffer.put( new Object() ) ; }
            catch(InterruptedException e ) { }
            delay(10) ;
        }
        System.out.println("ObjectProducer " +Thread.currentThread() +" Done ") ; 
        doneCounter.increment() ;
    }
}

class ObjectConsumer extends TestThread {

    private ObjectBoundedBufferInterface boundedBuffer ;
    private DoneCounter doneCounter ;
    private int numActions = 10;

    ObjectConsumer( ObjectBoundedBufferInterface bb, DoneCounter d, int n) {
        boundedBuffer = bb ; doneCounter = d ; numActions = n; }

    public void run() {
        

        for(int i=0 ; i < numActions ; ++i ) {
            delay(10) ;

            try {
                boundedBuffer.take() ; }
            catch(InterruptedException e ) { }

            delay(10) ;
        }
        System.out.println("ObjectConsumer " +Thread.currentThread() + " Done ") ; 
        doneCounter.increment() ;
    }
}