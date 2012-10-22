package examples.ReadersWriters;

import java.util.Random;

import examples.util.DoneCounter;
class Reader extends Thread {
    private static final Random rnd = new Random();
    private DoneCounter doneCounter ;
    ReadersWritersMonitor monitor;
    int numRead;
    int maxReadTime;

    public Reader(ReadersWritersMonitor monitor_, int numRead_, int maxReadTime_, DoneCounter doneCounter_) {
        monitor = monitor_;
        numRead = numRead_;
        maxReadTime = maxReadTime_;

        doneCounter = doneCounter_;
    }

    public void run() {
        for(int i = 0; i < numRead; ++i) {
            monitor.startRead();
            // read
            try {
                Thread.sleep((long)(rnd.nextDouble() * maxReadTime) + 1);
            }
            catch(InterruptedException e) {
                
            }
            monitor.endRead();
        }
        doneCounter.increment() ;
    }
}

class Writer extends Thread {
    private static final Random rnd = new Random();
    private DoneCounter doneCounter ;
    ReadersWritersMonitor monitor; 
    int numWrite;
    int maxWriteTime;

    public Writer(ReadersWritersMonitor monitor_, int numWrite_, int maxWriteTime_, DoneCounter doneCounter_) {
        monitor = monitor_;
        numWrite = numWrite_;
        maxWriteTime = maxWriteTime_;
        doneCounter = doneCounter_;
    }
    public void run() {
        for (int i = 0; i < numWrite; ++i) {
            monitor.startWrite();
            // write
            try {
                Thread.sleep((long)(rnd.nextDouble() * maxWriteTime) + 1);
            }
            catch(InterruptedException e) {
                
            }
            monitor.endWrite();
        }
        doneCounter.increment() ;
    }
}

public class TestReadersWriters {
    public static void main (String [] args)
    {
        int READERS = 10;
        int WRITERS = 10;
        int totalNumRead = 10; 
        int totalNumWrite = 10; 
        int maxReadTime = 100;
        int maxWriteTime = 100;

        ReadersWritersMonitor monitor = null; 
        try {
            switch(args[0].charAt(0)) {
            case 'n':
                monitor = new NaiveImplicitReadersWritersMonitor();
                break;
            case 'e':
                monitor = new ExplicitReadersWritersMonitor();
                break;
            case 'm':
                monitor = new MapReadersWritersMonitor();
                break;
            case 'h':
                monitor = new HashReadersWritersMonitor();
                break;
            case 's':
                monitor = new SetReadersWritersMonitor();
                break;
            }
            WRITERS = Integer.parseInt(args[1]);
            READERS = 5 * WRITERS;
            totalNumWrite = totalNumRead = Integer.parseInt(args[2]);
            maxReadTime = Integer.parseInt(args[3]);
            maxWriteTime = Integer.parseInt(args[4]);

        } catch (Exception e) { /* use defaults */ 
            e.printStackTrace();
            if(monitor == null) {
                monitor = new ExplicitReadersWritersMonitor();
            }
        }
        DoneCounter doneCounter = new DoneCounter() ;
        doneCounter.set( READERS + WRITERS ) ;
        long startTime = System.currentTimeMillis();


        Reader[] r = new Reader[READERS];
        for( int k = 0 ; k < READERS; ++k ) {
            r[k] = new Reader(monitor, totalNumRead/READERS, maxReadTime, doneCounter) ;
            r[k].start(); 
        }

        Writer[] w = new Writer[WRITERS];
        for( int k = 0 ; k < WRITERS; ++k ) {
            w[k] = new Writer(monitor, totalNumWrite/WRITERS, maxWriteTime, doneCounter) ;
            w[k].start(); 
        }

        doneCounter.waitForDone() ;
        
        long execTime = System.currentTimeMillis() - startTime;
        System.out.println( execTime );
    }
}
