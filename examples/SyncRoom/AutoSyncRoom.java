
package examples.SyncRoom;
import monitor.*;


import util.Common;

public class AutoSyncRoom extends SyncRoom {

    private final char type;
    private final AbstractImplicitMonitor monitor;


    public AutoSyncRoom(int N, char type) {
        super(N);
        this.type = type;
        switch (type) {
            case 'n':
                monitor = new NaiveImplicitMonitor();
                break;
            case 's':
                monitor = new SetMonitor();
                break;
            case 't':
                monitor = new TagMonitor();
                ((TagMonitor) monitor).registerGlobalVariable(
                    new GlobalVariable("serving") {
                        public int getValue() {
                            return serving;
                        }
                    } 
                    );
                break;
            default:
                monitor = null;
                break;
        }
    }

    public void enterRoom(int n) {
        final int n_dummy = n;
        monitor.DoWithin( new Runnable() {
            public void run() {
                final int myTicket = ticket;
                ticket++;
                if (myTicket != serving || 
                        (usedRoom != n_dummy && usedRoom != -1)) {

                    AbstractCondition cond;
                    switch (type) {
                        case 'n':
                        case 's':
                            cond = monitor.makeCondition( //auto-gen
                                new Assertion() {
                                    public boolean isTrue() { 
                                        return (serving == myTicket) 
                                            && (usedRoom == n_dummy 
                                            || usedRoom == -1); } 
                                } 
                                ) ;
                            break;
                        case 't':
                            PredicateTag[] tags = new PredicateTag[2];
                            tags[0] = ((TagMonitor) monitor).makeTag(
                                    "serving==myTicket" + "_" + myTicket,
                                    "serving", myTicket, 
                                    PredicateTag.OperationType.EQ,
                                    new Assertion() {
                                        public boolean isTrue() {
                                            return serving == myTicket;
                                        }
                                    }
                            );
                            tags[1] = null;
                            cond = ((TagMonitor) monitor).makeCondition(
                                    "serving==myTicket" + "_" + myTicket,
                                    new Assertion() {
                                        public boolean isTrue() {
                                            return (serving == myTicket) && 
                                                    (usedRoom == n_dummy ||
                                                     usedRoom == -1);
                                        }
                                    },
                                    false,
                                    tags
                            );
                            break;
                        default:
                            cond = null;
                            break;
                    }

                    cond.await();
                    if (type == 's') {
                        monitor.removeCondition(cond); 
                    }
                }
                Common.println(Thread.currentThread() + " enters room " + n_dummy
                        + ", myTicket: " + myTicket + ", serving: " + serving 
                        + ", usedRoom: "+ usedRoom);
                
                usedRoom = n_dummy; 
                rcnt[n_dummy]++;
                serving++;
            }});
    }

    public void leaveRoom(int n) {
        final int n_dummy = n;
        monitor.DoWithin( new Runnable() {
            public void run() {
                rcnt[n_dummy]--;
                if (rcnt[n_dummy] == 0) {
                    usedRoom = -1;
                }
                Common.println(Thread.currentThread() + " leaves room " + n_dummy);
            }});
    }
}
