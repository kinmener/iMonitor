package examples.RoundRobinMonitor;

import monitor.*;

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
            case 's':
                monitor = new SetMonitor();
                break;
            case 'm':
                monitor = new MapMonitor();
                break;
            case 't':
                monitor = new TagMonitor();
                ((TagMonitor) monitor).registerGlobalVariable(
                    new GlobalVariable("numAccess") {
                        public int getValue() {
                            return numAccess;
                        }
                    } 
                    );
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

                if (numAccess != myId_dummy) {
                    AbstractCondition cond;
                    switch (type) {
                        case 'n':
                        case 's':
                            cond = monitor.makeCondition( 
                                new Assertion() {
                                    public boolean isTrue() { 
                                        return numAccess == myId_dummy; } 
                                } 
                            ) ;
                            break;
                        case 'm':
                            cond = ((MapMonitor) monitor).makeCondition( 
                                new Assertion() {
                                    public boolean isTrue() { 
                                        return numAccess == myId_dummy; } 
                                },
                                "numAccess == myId_dummy" + "_" + myId_dummy);
                            break;
                        case 't':
                            PredicateTag[] tags = new PredicateTag[1];
                            tags[0] = ((TagMonitor) monitor).makeTag(
                                    "numAccess==myId_dummy" + "_" + myId_dummy,
                                    "numAccess", myId_dummy, 
                                    PredicateTag.OperationType.EQ,
                                    new Assertion() {
                                        public boolean isTrue() {
                                            return numAccess == myId_dummy;
                                        }
                                    }
                            );
                            cond = ((TagMonitor) monitor).makeCondition(
                                    "numAccess==myId_dummy" + "_" + myId_dummy,
                                    new Assertion() {
                                        public boolean isTrue() {
                                            return numAccess == myId_dummy;
                                        }
                                    },
                                    false,
                                    tags
                            );

                            break;
                        default:
                            cond = ((iMonitor) monitor).makeCondition(
                                    "numAccess == myId_dummy" + "_" + myId_dummy,
                                    "numAccess", myId_dummy, 
                                    iMonitorCondition.OperationType.EQ, 
                                    new Assertion() {
                                        public boolean isTrue() { 
                                            return numAccess == myId_dummy; } 
                                    },
                                    false);
                            break;
                    }
                    cond.await();
                }

                Common.println("myId: " + myId_dummy + 
                        " numAccess: " + numAccess);
                ++numAccess;
                numAccess %= numProc;
            }
        });
    }
}
