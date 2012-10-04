

package examples.TicketReadersWriters;
import monitor.*;	//auto-gen iMonitor

import util.Common;

public class iMonitorReadersWriters extends ReadersWritersMonitor {
    private int rcnt;
    private int ticket;
    private int serving;
    private char type;

    private final AbstractImplicitMonitor monitor;

    public iMonitorReadersWriters(char type) {
        this.type = type;

        rcnt = 0;
        ticket = 0;
        serving = 0;

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
                    new GlobalVariable("serving") {
                        public int getValue() {
                            return serving;
                        }
                    } 
                    );
                break;
            default:
                monitor = new iMonitor();
                ((iMonitor) monitor).registerGlobalVariable(
                    new GlobalVariable("serving") {
                        public int getValue() {
                            return serving;
                        }
                    } 
                    );
                break;
        }
    }

    public void startRead() {
        monitor.DoWithin( new Runnable() {
            public void run() {
                final int myTicket = ticket;
                ticket++;
                if (myTicket != serving) {

                    AbstractCondition cond;
                    switch (type) {
                        case 'n':
                        case 's':
                            cond = monitor.makeCondition( //auto-gen
                                new Assertion() {
                                    public boolean isTrue() { 
                                        return serving == myTicket; } 
                                } 
                                ) ;
                            break;
                        case 'm':
                            cond = ((MapMonitor) monitor).makeCondition( 
                                new Assertion() {
                                    public boolean isTrue() { 
                                        return serving == myTicket; } 
                                },
                                "serving == myTicket" + "_" + myTicket) ;
                            break;
                        case 't':
                            PredicateTag[] tags = new PredicateTag[1];
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
                            cond = ((TagMonitor) monitor).makeCondition(
                                    "serving==myTicket" + "_" + myTicket,
                                    new Assertion() {
                                        public boolean isTrue() {
                                            return serving == myTicket;
                                        }
                                    },
                                    false,
                                    tags
                            );
                            break;
                        default:
                            cond = ((iMonitor) monitor).makeCondition(
                                    "serving == myTicket" + "_" + myTicket,
                                    "serving", myTicket, 
                                    iMonitorCondition.OperationType.EQ,
                                         new Assertion() {
                                            public boolean isTrue() { 
                                                return serving == myTicket; } 
                                            },
                                    false);
                            break;
                    }

                    cond.await();
                    if (type == 'm') {
                        monitor.removeCondition(cond); 
                    }
                }
                Common.println("Reader: " + Thread.currentThread() 
                        + " starts to read");
                Common.println("serving: " + serving + " rcnt: " + (rcnt + 1));
                rcnt++;
                serving++;
            }} ) ;
    }

    public void endRead() {
        monitor.DoWithin( new Runnable() {
            public void run() {
                rcnt--;
                Common.println("Reader " + Thread.currentThread() 
                    + "ends reading");
            }} ) ;
    }

    public void startWrite() {
        monitor.DoWithin( new Runnable() {
            public void run() {

                final int myTicket = ticket;
                ticket++;

                if (serving != myTicket || rcnt != 0) {

                    AbstractCondition cond;
                    switch (type) {
                        case 'n':
                        case 's':
                            cond = monitor.makeCondition( //auto-gen
                                new Assertion() {
                                    public boolean isTrue() {
                                        return ((serving == myTicket) 
                                            && (rcnt == 0)); } 
                                } 
                                ) ;
                            break;
                        case 'm':
                            cond = ((MapMonitor) monitor).makeCondition( 
                                new Assertion() {
                                    public boolean isTrue() { 
                                        return serving == myTicket 
                                                && rcnt == 0; } 
                                },
                                "serving == myTicket && rcnt == 0" + "_" 
                                + myTicket) ;
                            break;
                        case 't':
                            PredicateTag[] tags = new PredicateTag[1];
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
                            cond = ((TagMonitor) monitor).makeCondition(
                                    "serving==myTicket && rcnt==0" + "_" 
                                    + myTicket,
                                    new Assertion() {
                                        public boolean isTrue() {
                                            return serving == myTicket 
                                                    && rcnt == 0;
                                        }
                                    },
                                    false,
                                    tags
                            );
                            break;

                        default:
                            // make complex condition
                            cond = ((iMonitor) monitor).makeCondition( 
                                    "serving == myTicket && rcnt == 0" + "_"
                                            + myTicket,
                                    "serving", myTicket, 
                                    iMonitorCondition.OperationType.EC,
                                    new Assertion() {
                                        public boolean isTrue() { 
                                            return serving == myTicket 
                                                    && rcnt == 0; } 
                                    },
                                    false) ;
                            break;
                    }
                    cond.await();
                    if (type == 'm') {
                        monitor.removeCondition(cond); 
                    }
                }
                Common.println("Writer " + Thread.currentThread() 
                        + " starts to write");
                Common.println("serving: " + serving + " rcnt: " + rcnt);
            }} ) ;
    }

    public void endWrite() {
        monitor.DoWithin( new Runnable() {
            public void run() {
                serving++;
                Common.println("Writer " + Thread.currentThread() + "end writing");
            }} ) ;
    }
}

