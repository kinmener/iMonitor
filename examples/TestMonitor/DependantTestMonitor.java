package examples.TestMonitor;

import monitor.AbstractCondition;
import monitor.Assertion;
import monitor.DependantMonitor;


public class DependantTestMonitor implements TestMonitor {
    private DependantMonitor __monitor__628 = new DependantMonitor(); //auto-gen
    private int numProc;
    private int numAccess;

    public DependantTestMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;
    }

    public void access(int myId) {
        final int myId_dummy = myId;
        __monitor__628.DoWithin( new Runnable() {
            public void run() {
                AbstractCondition cond_1 = __monitor__628.makeCondition( //auto-gen
                    new Assertion() {
                        public boolean isTrue() { return (numAccess % numProc) == myId_dummy; } 
                    },
                    "(numAccess % numProc) == myId_dummy" + "_" + myId_dummy) ;
                cond_1.await();
                //System.out.println("myId: " + myId_dummy + " numAccess: " + numAccess);
                ++numAccess;
            }}, "access_" + myId_dummy) ;
    }
}
