package examples.ReadersWriters;

import java.util.Random;

import examples.util.DoneCounter;

class TimeReader extends Thread {
    private static final Random rnd = new Random();

    private DoneCounter doneCounter ;
    ReadersWritersMonitor monitor;
    int numRead;
    int maxReadTime;
    long readTime;
    long totalTime;

    public TimeReader(ReadersWritersMonitor monitor_, int numRead_, int maxReadTime_, DoneCounter doneCounter_) {
        monitor = monitor_;
        numRead = numRead_;
        maxReadTime = maxReadTime_;

        doneCounter = doneCounter_;

        readTime = 0;
        totalTime = 0;
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        for(int i = 0; i < numRead; ++i) {
            monitor.startRead();
            // read
            long readStartTime = System.currentTimeMillis();
            try {
                Thread.sleep((long)(rnd.nextDouble() * maxReadTime) + 1);
            }
            catch(InterruptedException e) {
                
            }
            readTime += (System.currentTimeMillis() - readStartTime);
            monitor.endRead();
        }
        totalTime = System.currentTimeMillis() - startTime;
        doneCounter.increment() ;
    }

    public long getReadTime() {
        return readTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public long getSyncTime() {
        return totalTime - readTime;
    }
}

class TimeWriter extends Thread {
    private static final Random rnd = new Random();
    private DoneCounter doneCounter ;
    ReadersWritersMonitor monitor; 
    int numWrite;
    int maxWriteTime;
    long writeTime;
    long totalTime;

    public TimeWriter(ReadersWritersMonitor monitor_, int numWrite_, int maxWriteTime_, DoneCounter doneCounter_) {
        monitor = monitor_;
        numWrite = numWrite_;
        maxWriteTime = maxWriteTime_;
        doneCounter = doneCounter_;

        writeTime = 0;
        totalTime = 0;
    }
    public void run() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numWrite; ++i) {
            monitor.startWrite();
            // write
            long writeStartTime = System.currentTimeMillis();
            try {
                Thread.sleep((long)(rnd.nextDouble() * maxWriteTime) + 1);
            }
            catch(InterruptedException e) {
                
            }
            writeTime += (System.currentTimeMillis() - writeStartTime);
            monitor.endWrite();
        }
        totalTime = System.currentTimeMillis() - startTime;
        doneCounter.increment() ;
    }

    public long getWriteTime() {
        return writeTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public long getSyncTime() {
        return totalTime - writeTime;
    }
}

public class TestTimeReadersWriters {
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
            case 's':
                monitor = new SetReadersWritersMonitor();
                break;
            }
            WRITERS = READERS = Integer.parseInt(args[1]);
            totalNumRead = Integer.parseInt(args[2]);
            totalNumWrite = Integer.parseInt(args[3]);
            maxReadTime = Integer.parseInt(args[4]);
            maxWriteTime = Integer.parseInt(args[5]);

        } catch (Exception e) { /* use defaults */ 
            e.printStackTrace();
            if(monitor == null) {
                monitor = new ExplicitReadersWritersMonitor();
            }
        }
        DoneCounter doneCounter = new DoneCounter() ;
        doneCounter.set( READERS + WRITERS ) ;
        long startTime = System.currentTimeMillis();

        TimeReader[] readers = new TimeReader[READERS];
        TimeWriter[] writers = new TimeWriter[WRITERS];

        for( int k = 0 ; k < WRITERS; ++k ) {
            readers[k] = new TimeReader(monitor, totalNumRead/WRITERS, maxReadTime, doneCounter) ;
            writers[k] = new TimeWriter(monitor, totalNumWrite/WRITERS, maxWriteTime, doneCounter) ;
            readers[k].start();
            writers[k].start();
        }

        doneCounter.waitForDone() ;
        long execTime = System.currentTimeMillis() - startTime;
        System.out.println( execTime );

        long totalReadTime = 0;
        long totalWriteTime = 0;
        long totalSyncTime = 0;
        long totalTime = 0;
        for( int k = 0 ; k < WRITERS; ++k ) {
            totalWriteTime += writers[k].getWriteTime();
            totalReadTime += readers[k].getReadTime();
            totalSyncTime += writers[k].getSyncTime();
            totalSyncTime += readers[k].getSyncTime();
            totalTime += readers[k].getTotalTime();
            totalTime += writers[k].getTotalTime();
        }
        System.out.println("r: " + totalReadTime + " w: " + totalWriteTime + " s: " + totalSyncTime + " t: " + totalTime);
    }
}
