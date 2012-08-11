package examples.RoundRobinMonitor;

import monitor.AbstractCondition;
import monitor.Assertion;
import monitor.MapMonitor;

import examples.util.Common;

public class MapRoundRobinMonitor extends RoundRobinMonitor {
    private MapMonitor __monitor__628 = new MapMonitor(); //auto-gen
    private int numProc;
    private int numAccess;

    public MapRoundRobinMonitor(int numProc_) {
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
                Common.println("myId: " + myId_dummy + " numAccess: " + numAccess);
                __monitor__628.removeCondition(cond_1);
                ++numAccess;
            }} ) ;
    }
}
