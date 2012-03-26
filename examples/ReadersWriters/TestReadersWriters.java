package examples.ReadersWriters;

import examples.util.DoneCounter;
class Reader extends Thread {
    private DoneCounter doneCounter ;
    ReadersWritersMonitor monitor;
    int numRead;

    public Reader(ReadersWritersMonitor monitor_, int numRead_, DoneCounter doneCounter_) {
        monitor = monitor_;
        numRead = numRead_;
        doneCounter = doneCounter_;
    }

    public void run() {
        for(int i = 0; i < numRead; ++i) {
            monitor.startRead();
            // read
            try {
                Thread.sleep(300);
            }
            catch(InterruptedException e) {
                
            }
            monitor.endRead();
        }
        doneCounter.increment() ;
    }
}

class Writer extends Thread {
    private DoneCounter doneCounter ;
    ReadersWritersMonitor monitor; 
    int numWrite;

    public Writer(ReadersWritersMonitor monitor_, int numWrite_, DoneCounter doneCounter_) {
        monitor = monitor_;
        numWrite = numWrite_;
        doneCounter = doneCounter_;
    }
    public void run() {
        for (int i = 0; i < numWrite; ++i) {
            monitor.startWrite();
            // write
            try {
                Thread.sleep(300);
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

        ReadersWritersMonitor monitor = null; 
        try {
            switch(args[0].charAt(0)) {
            case 'n':
                monitor = new NaiveImplicitReadersWritersMonitor();
                break;
            case 'e':
                monitor = new ExplicitReadersWritersMonitor();
                break;
            }
            READERS = Integer.parseInt(args[1]);
            WRITERS = Integer.parseInt(args[2]);
            totalNumRead = Integer.parseInt(args[3]);
            totalNumWrite = Integer.parseInt(args[4]);

        } catch (Exception e) { /* use defaults */ 
            e.printStackTrace();
            if(monitor == null) {
                monitor = new ExplicitReadersWritersMonitor();
            }
        }
        DoneCounter doneCounter = new DoneCounter() ;
        doneCounter.set( READERS + WRITERS ) ;
        long startTime = System.currentTimeMillis();


        for( int k = 0 ; k < READERS; ++k ) {
            Thread w = new Writer(monitor, totalNumWrite/WRITERS, doneCounter) ;
            w.start(); 
        }

        for( int k = 0 ; k < WRITERS; ++k ) {
            Thread r = new Reader(monitor, totalNumRead/WRITERS, doneCounter) ;
            r.start(); 
        }

        doneCounter.waitForDone() ;
        long execTime = System.currentTimeMillis() - startTime;
        System.out.println( execTime );

    }
}
