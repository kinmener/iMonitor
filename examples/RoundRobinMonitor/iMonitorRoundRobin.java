package examples.RoundRobinMonitor;

import monitor.AbstractCondition;
import monitor.AbstractImplicitMonitor;
import monitor.MapMonitor;
import monitor.NaiveImplicitMonitor;
import monitor.iMonitor;
import monitor.SimpleCondition;
import monitor.Assertion;
import monitor.GlobalVariable;
import monitor.HashMonitor;

import util.Common;

public class iMonitorRoundRobin extends RoundRobinMonitor {
    private final AbstractImplicitMonitor monitor;
    private final int numProc;
    private int numAccess;
    private final char type;

    public iMonitorRoundRobin(int numProc, char type) {
        this.numProc = numProc;
        this.type = type;
        numAccess = 0;

        switch (type) {
            case 'n':
                monitor = new NaiveImplicitMonitor();
                break;
            case 'm':
                monitor = new MapMonitor();
                break;
            default:
                monitor = new iMonitor(); //auto-gen
                ((iMonitor) monitor).registerGlobalVariable(
                    new GlobalVariable("numAccess") {
                        public int getValue() {
                            return numAccess;
                        }
                    } 
                    );
                break;
        }
    }

    public void access(int myId) {
        final int myId_dummy = myId;
        monitor.DoWithin( new Runnable() {
            public void run() {
                AbstractCondition cond;
                switch (type) {
                    case 'n':
                        cond = monitor.makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() { 
                                    return numAccess == myId_dummy; } 
                            } 
                            ) ;
                        break;
                    case 'm':
                        cond = ((MapMonitor) monitor).makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() { 
                                    return numAccess == myId_dummy; } 
                            },
                            "numAccess == myId_dummy" + "_" + myId_dummy) ;
                        break;
                    default:
                        cond = ((iMonitor) monitor).makeCondition("numAccess", 
                            myId_dummy, SimpleCondition.OperationType.EQ, false);
                        break;
                }
                cond.await();
                Common.println("myId: " + myId_dummy + " numAccess: " + numAccess);
                ++numAccess;
                numAccess %= numProc;
            }
        });
    }
}
