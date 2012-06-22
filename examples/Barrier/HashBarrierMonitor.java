package examples.BarrierMonitor;

import monitor.AbstractCondition;
import monitor.Assertion;
import monitor.HashMonitor;


public class HashBarrierMonitor extends BarrierMonitor {
    private HashMonitor __monitor__628 = new HashMonitor(); //auto-gen
    private int numProc;
    private int numAccess;

    public HashBarrierMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;
    }

    public void access(int myId) {
        final int myId_dummy = myId;
        __monitor__628.DoWithin( new Runnable() {
            public void run() {
                ++numAccess;
                AbstractCondition cond_1 = __monitor__628.makeCondition( //auto-gen
                    new Assertion() {
                        public boolean isTrue() { return (numAccess % numProc) == 0; } 
                    },
                        "(numAccess % numProc) == 0") ;
                setCurrentCpuTime();
                cond_1.await();
                addSyncTime();
                //System.out.println("myId: " + myId_dummy + " numAccess: " + numAccess);
            }} ) ;
    }
}
