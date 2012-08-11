
package examples.RoundRobinMonitor;

class TestThread extends Thread {
    private RoundRobinMonitor monitor;
    private int numAccess; 
    private int myId;
    public TestThread(RoundRobinMonitor monitor_, int numAccess_, int myId_) {
        monitor = monitor_;
        numAccess = numAccess_;
        myId = myId_;
    }
    public void run() {
        for(int i = 0; i < numAccess; ++i) {
            monitor.access(myId);
        }
    }
}
    
public class Test{
    public static void main (String [] args)
    {
        int numProc = 16;
        int totalNumAccess = 1000;
        RoundRobinMonitor monitor = null;
        try {
            numProc = Integer.parseInt(args[0]); 
            totalNumAccess = Integer.parseInt(args[1]); 
            switch(args[2].charAt(0)) {
                case 'n':
                    monitor = new NaiveRoundRobinMonitor(numProc);
                    break;
                case 's':
                    monitor = new SetRoundRobinMonitor(numProc);
                    break;
                case 'm':
                    monitor = new MapRoundRobinMonitor(numProc);
                    break;
                default:
                    monitor = new ExplicitRoundRobinMonitor(numProc);

            }
        } catch(Exception e) {
            if(monitor == null) {
                    monitor = new ExplicitRoundRobinMonitor(numProc);
            }
        }

        long startTime = System.currentTimeMillis();
        TestThread[] testThreads = new TestThread[numProc];
        for(int i = 0; i < numProc; ++i) {
            testThreads[i] = new TestThread(monitor, totalNumAccess/numProc, i);
            testThreads[i].start();
        }
        float totalCpuTime = 0.0f;
        for(int i = 0; i < numProc; ++i) {
            try {
                testThreads[i].join();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        long execTime = System.currentTimeMillis() - startTime;
        System.out.println( execTime );
    }
}
