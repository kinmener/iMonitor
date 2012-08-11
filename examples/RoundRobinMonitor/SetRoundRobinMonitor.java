
package examples.RoundRobinMonitor;
import monitor.*;	//auto-gen iMonitor

import examples.util.Common;

public class SetRoundRobinMonitor extends RoundRobinMonitor {
    private AbstractImplicitMonitor __monitor__628 = new SetMonitor(); //auto-gen
    private int numProc;
    private int numAccess;

    public SetRoundRobinMonitor(int numProc_) {
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
                    } ) ;
                cond_1.await();
                Common.println("myId: " + myId_dummy + " numAccess: " + numAccess);
                __monitor__628.removeCondition(cond_1);
                ++numAccess;
            }} ) ;
    }
}
