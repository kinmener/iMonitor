package examples.TicketReadersWriters;


import examples.util.DoneCounter;
class Reader extends Thread {
    private DoneCounter doneCounter ;
    ReadersWritersMonitor monitor;
    int numRead;
    int maxReadTime;
    int maxDelay;
    long responseTime;

    public Reader(ReadersWritersMonitor monitor, int numRead, int maxReadTime,
            DoneCounter doneCounter, int maxDelay) {
        this.monitor = monitor;
        this.numRead = numRead;
        this.maxReadTime = maxReadTime;
        this.doneCounter = doneCounter;
        this.maxDelay = maxDelay;
        responseTime = 0;
    }

    public void run() {
        for(int i = 0; i < numRead; ++i) {
            long startTime = System.nanoTime();
            monitor.startRead();
            // read
            try {
                if (maxReadTime != 0) {
                    Thread.sleep((long) (Math.random() * maxReadTime) + 1);
                    //Thread.sleep(0, maxReadTime * 1000);
                }
            }
            catch(InterruptedException e) {
                
            }
            monitor.endRead();
            responseTime += (System.nanoTime() - startTime);
            
            int delay = (int) (maxDelay * Math.random());
            if (delay != 0) {
                try {
                    if (delay >= 1000000) {
                        Thread.sleep(delay / 1000000, delay % 1000000);
                    } else {
                        Thread.sleep(0, delay);
                    }
                } catch(InterruptedException e) {
                }

                /*
                 *long startmaxDelay = System.nanoTime();
                 *while (true) {
                 *    int tmp = 0;
                 *    tmp += 1;
                 *    tmp *= 2;
                 *    if ((System.nanoTime() - startmaxDelay > maxDelay)) {
                 *        break; 
                 *    }
                 *}
                 */
            }
        }
        doneCounter.increment() ;
    }
    public double getAvgResponseTime() {
        return (double) responseTime / (double) numRead;
    }
}

class Writer extends Thread {
    private DoneCounter doneCounter ;
    ReadersWritersMonitor monitor; 
    int numWrite;
    int maxWriteTime;
    int maxDelay;
    long responseTime;

    public Writer(ReadersWritersMonitor monitor, int numWrite, 
            int maxWriteTime, DoneCounter doneCounter, int maxDelay) {
        this.monitor = monitor;
        this.numWrite = numWrite;
        this.maxWriteTime = maxWriteTime;
        this.doneCounter = doneCounter;
        this.maxDelay = maxDelay;
        responseTime = 0;
    }
    public void run() {
        for (int i = 0; i < numWrite; ++i) {
            long startTime = System.nanoTime();
            monitor.startWrite();
            // write
            try {
                if (maxWriteTime != 0) {
                    //Thread.sleep(0, maxWriteTime * 1000);
                    Thread.sleep((long) (Math.random() * maxWriteTime) + 1);
                }
            }
            catch(InterruptedException e) {
                
            }
            monitor.endWrite();
            responseTime += (System.nanoTime() - startTime);
            int delay = (int) (maxDelay * Math.random());
            if (delay != 0) {
                try {
                    if (delay >= 1000000) {
                        Thread.sleep(delay / 1000000, delay % 1000000);
                    } else {
                        Thread.sleep(0, delay );
                    }
                } catch(InterruptedException e) {
                }
                /*
                 *long startmaxDelay = System.nanoTime();
                 *while (true) {
                 *    int tmp = 0;
                 *    tmp += 1;
                 *    tmp *= 2;
                 *    if ((System.nanoTime() - startmaxDelay > maxDelay)) {
                 *        break; 
                 *    }
                 *}   
                 */
            }
        }
        doneCounter.increment() ;
    }
    public double getAvgResponseTime() {
        return (double) responseTime / (double) numWrite;
    }
}

public class TestTicketReadersWriters {
    public static void main (String [] args)
    {
        int READERS = 10;
        int WRITERS = 10;
        int totalNumRead = 10; 
        int totalNumWrite = 10; 
        int maxReadTime = 100;
        int maxWriteTime = 100;
        int maxDelay = 0;

        ReadersWritersMonitor monitor = null; 
        try {
            switch(args[0].charAt(0)) {
            case 'e':
                monitor = new ExplicitReadersWritersMonitor();
                break;
            default: 
                monitor = new iMonitorReadersWriters(args[0].charAt(0));
                break;
            }
            WRITERS = Integer.parseInt(args[1]);
            READERS = 5 * WRITERS;
            totalNumWrite = totalNumRead = Integer.parseInt(args[2]);
            maxReadTime = Integer.parseInt(args[3]);
            maxWriteTime = Integer.parseInt(args[4]);
            maxDelay = Integer.parseInt(args[5]) * 1000;
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
            r[k] = new Reader(monitor, totalNumRead/READERS, maxReadTime, 
                    doneCounter, maxDelay) ;
            r[k].start(); 
        }

        Writer[] w = new Writer[WRITERS];
        for( int k = 0 ; k < WRITERS; ++k ) {
            w[k] = new Writer(monitor, totalNumWrite/WRITERS, maxWriteTime, 
                    doneCounter, maxDelay) ;
            w[k].start(); 
        }

        doneCounter.waitForDone() ;
       
        double totalResponseTime = 0.0f;
        for (int i = 0; i < READERS; i++) {
            totalResponseTime += r[i].getAvgResponseTime();
        }
        for (int i = 0; i < WRITERS; i++) {
            totalResponseTime += w[i].getAvgResponseTime();
        }
        long execTime = System.currentTimeMillis() - startTime;
        //System.out.println( execTime );
        System.out.println( totalResponseTime / (WRITERS * 1000));
    }
}
