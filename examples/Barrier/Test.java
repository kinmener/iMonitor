package examples.BarrierMonitor;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.lang.management.*;

import examples.util.DoneCounter;

class TestThread extends Thread {
    long  cpuTime;
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    public long getCpuTime() {
        return cpuTime; 
    }
    private BarrierMonitor monitor1;
    private BarrierMonitor monitor2;
    private int numAccess; 
    private int myId;
    private DoneCounter counter;
    private int numProc;
    public TestThread(BarrierMonitor monitor1_, BarrierMonitor monitor2_, int numAccess_, int myId_, int numProc_) {
        monitor1 = monitor1_;
        monitor2 = monitor2_;
        numAccess = numAccess_;
        myId = myId_;
        numProc = numProc_;
    }
    public void run() {
        long startTime = threadMXBean.getCurrentThreadCpuTime();
        for(int i = 0; i < numAccess; ++i) {
            monitor1.access(myId);
            monitor2.access(myId);
        }
        long endTime = threadMXBean.getCurrentThreadCpuTime();
        cpuTime = endTime - startTime;
    }
}
    
public class Test{
    public static void main (String [] args)
    {
        int numProc = 16;
        int totalNumAccess = 1000;
        BarrierMonitor monitor1 = null;
        BarrierMonitor monitor2 = null;
        try {
            numProc = Integer.parseInt(args[0]); 
            totalNumAccess = Integer.parseInt(args[1]); 
            switch(args[2].charAt(0)) {
                //case 'd':
                //    monitor = new DependantBarrierMonitor(numProc);
                //    break;
                case 'n':
                    monitor1 = new NaiveImplicitBarrierMonitor(numProc);
                    monitor2 = new NaiveImplicitBarrierMonitor(numProc);
                    break;
                //case 'l':
                //    monitor = new HashSetBarrierMonitor(numProc);
                //    break;
                case 'h':
                    monitor1 = new HashBarrierMonitor(numProc);
                    monitor2 = new HashBarrierMonitor(numProc);
                    break;
                case 'p':
                    monitor1 = new PDSLLockBarrierMonitor(numProc);
                    monitor2 = new PDSLLockBarrierMonitor(numProc);
                    break;
                case 'c':
                    monitor1 = new NConditionLockBarrierMonitor(numProc);
                    monitor2 = new NConditionLockBarrierMonitor(numProc);
                    break;
                default:
                    monitor1 = new ExplicitBarrierMonitor(numProc);
                    monitor2 = new ExplicitBarrierMonitor(numProc);

            }
        } catch(Exception e) {
            if(monitor1 == null) {
                    monitor1 = new ExplicitBarrierMonitor(numProc);
            }
            if(monitor2 == null) {
                    monitor2 = new ExplicitBarrierMonitor(numProc);
            }
        }

        long startTime = System.currentTimeMillis();
        DoneCounter counter = new DoneCounter();
        counter.set(numProc);
        TestThread[] testThreads = new TestThread[numProc];
        int numAccess = totalNumAccess/numProc;
        for(int i = 0; i < numProc; ++i) {
            testThreads[i] = new TestThread(monitor1, monitor2, numAccess, i, numProc);
            testThreads[i].start();
        }
        
        //for (int i = 0; i < numAccess; i++) {
        //    counter.waitForDone();
        //    counter.set(numProc);
        //    synchronized(counter) {
        //        counter.notifyAll();
        //    }
        //}

        float totalCpuTime = 0.0f;
        for(int i = 0; i < numProc; ++i) {
            try {
                testThreads[i].join();
                totalCpuTime += testThreads[i].getCpuTime();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        long execTime = System.currentTimeMillis() - startTime;
        System.out.println( execTime );
        System.out.println( String.format("%.3f", totalCpuTime/1e6));
        System.out.println( String.format("%.3f", (monitor1.getSyncTime() + monitor2.getSyncTime())/1e6));
    }
}
