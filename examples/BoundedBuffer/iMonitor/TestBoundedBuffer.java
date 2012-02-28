
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

public class TestBoundedBuffer {

    public static void main(String[] args) {
        IdealImplicitBoundedBuffer rw_controller = new IdealImplicitBoundedBuffer(4);
        DoneCounter doneCounter = new DoneCounter() ;

        final int CONSUMERS = 10 ;
        final int PRODUCERS = 10 ;
        doneCounter.set( CONSUMERS + PRODUCERS ) ;
        System.out.println("Please wait. This takes a while");
        for( int k=0 ; k < CONSUMERS ; ++k ) {
          Thread w = new ObjectConsumer( rw_controller, doneCounter ) ;
          w.start(); }
        for( int k=0 ; k < PRODUCERS ; ++k ) {
          Thread r = new ObjectProducer( rw_controller, doneCounter ) ;
          r.start(); }
        doneCounter.waitForDone() ;
        System.out.println( "Main done" );
    }
}

class ObjectProducer extends TestThread {

    private ObjectBoundedBufferInterface boundedBuffer ;
    private DoneCounter doneCounter ;

    ObjectProducer( ObjectBoundedBufferInterface bb, DoneCounter d ) {
        boundedBuffer = bb ; doneCounter = d ; }

    public void run() {
    	for(int i=0 ; i < 10 ; ++i ) {
    		delay(100) ;
    		
    		try {
    			boundedBuffer.put( new Object() ) ; }
    		catch(InterruptedException e ) { }
    		delay(100) ;
    	}
    	System.out.println("ObjectProducer " +Thread.currentThread() +" Done ") ; 
    	int count = doneCounter.increment() ;
    }
}

class ObjectConsumer extends TestThread {

    private ObjectBoundedBufferInterface boundedBuffer ;
    private DoneCounter doneCounter ;

    ObjectConsumer( ObjectBoundedBufferInterface bb, DoneCounter d ) {
        boundedBuffer = bb ; doneCounter = d ; }

    public void run() {
        //StringBuffer sb = new StringBuffer() ;

        for(int i=0 ; i < 10 ; ++i ) {
          delay(100) ;
          Object x;
          try {
              x= boundedBuffer.take() ; }
          catch(InterruptedException e ) { }
         // sb.append( ch ) ;
          delay(100) ;
        }
        System.out.println("ObjectConsumer " +Thread.currentThread() + " Done ") ; 
        doneCounter.increment() ;
    }
        //System.out.println("ObjectConsumer " +Thread.currentThread() +" <" + sb + ">" ) ; }
}
