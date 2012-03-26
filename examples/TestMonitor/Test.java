
package examples.TestMonitor;
class TestThread extends Thread {
    private TestMonitor monitor;
    private int numAccess; 
    private int myId;
    public TestThread(TestMonitor monitor_, int numAccess_, int myId_) {
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
        TestMonitor monitor = null;
        try {
            numProc = Integer.parseInt(args[0]); 
            totalNumAccess = Integer.parseInt(args[1]); 
            switch(args[2].charAt(0)) {
                case 'd':
                    monitor = new DependantTestMonitor(numProc);
                    break;
                case 'n':
                    monitor = new NaiveImplicitTestMonitor(numProc);
                    break;
                case 'i':
                    monitor = new NaiveImplicitGlobalTestMonitor(numProc);
                    break;
                case 'l':
                    monitor = new HashSetTestMonitor(numProc);
                    break;
                case 'h':
                    monitor = new HashTestMonitor(numProc);
                    break;
                default:
                    monitor = new ExplicitTestMonitor(numProc);

            }
        } catch(Exception e) {
            if(monitor == null) {
                    monitor = new ExplicitTestMonitor(numProc);
            }
        }

        long startTime = System.currentTimeMillis();
        Thread[] testThreads = new TestThread[numProc];
        for(int i = 0; i < numProc; ++i) {
            testThreads[i] = new TestThread(monitor, totalNumAccess/numProc, i);
            testThreads[i].start();
        }
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