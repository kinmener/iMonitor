
package examples.RoundRobinMonitor;

class TestThread extends Thread {
    private RoundRobinMonitor monitor;
    private int numAccess; 
    private int myId;
    private int delay;
    private long responseTime; 
    public TestThread(RoundRobinMonitor monitor, int numAccess, int myId, 
            int delay) {
        this.monitor = monitor;
        this.numAccess = numAccess;
        this.myId = myId;
        this.delay = delay;
        responseTime = 0;
    }
    public void run() {
        for(int i = 0; i < numAccess; ++i) {
            if (delay != 0) {
                try {
                    if (delay >= 1000000) {
                        Thread.sleep(delay / 1000000, delay % 1000000);  
                    } else {
                        Thread.sleep(0, delay);  
                    }
                } catch(InterruptedException e) {
                }
            }
            long startTime = System.nanoTime();
            monitor.access(myId);
            responseTime += (System.nanoTime() - startTime);
        }
    }

    public double getAvgResponseTime() {
        return (double) responseTime / (double) numAccess;
    }
}

public class Test{
    public static void main (String [] args)
    {
        int numProc = 16;
        int totalNumAccess = 1000;
        int delay = 0;
        RoundRobinMonitor monitor = null;
        try {
            numProc = Integer.parseInt(args[0]); 
            totalNumAccess = Integer.parseInt(args[1]); 

            switch (args[2].charAt(0)) {
                case 'e':
                    monitor = new ExplicitRoundRobinMonitor(numProc);
                    break;
                case 'h':
                    monitor = new MapExplicitRoundRobinMonitor(numProc);
                    break;
                default: 
                    monitor = 
                        new iMonitorRoundRobin(numProc, args[2].charAt(0));
            }
            delay = Integer.parseInt(args[3]) * 1000; // microsecond 
        } catch(Exception e) {
            if(monitor == null) {
                monitor = new ExplicitRoundRobinMonitor(numProc);
            }
        }

        long startTime = System.currentTimeMillis();
        TestThread[] testThreads = new TestThread[numProc];
        for (int i = 0; i < numProc; ++i) {
            testThreads[i] = new TestThread(monitor, totalNumAccess/numProc, i, delay);
            testThreads[i].start();
        }
        double totalResponseTime = 0.0f;
        for (int i = 0; i < numProc; ++i) {
            try {
                testThreads[i].join();
                totalResponseTime += testThreads[i].getAvgResponseTime();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long execTime = System.currentTimeMillis() - startTime;
//        System.out.println( execTime );
        System.out.println(totalResponseTime / numProc);
    }
}
