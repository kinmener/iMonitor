

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
            case 'm':
                monitor = new MapMonitor();
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
                AbstractCondition cond;
                switch (type) {
                    case 'n':
                        cond = monitor.makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() { 
                                    return serving == myTicket; } 
                            } 
                            ) ;
                        break;
                    case 'm':
                        cond = ((MapMonitor) monitor).makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() { 
                                    return serving == myTicket; } 
                            },
                            "serving == myTicket" + "_" + myTicket) ;
                        break;
                    default:
                        cond = ((iMonitor) monitor).makeCondition("serving", 
                            myTicket, SimpleCondition.OperationType.EQ, false);
                        break;
                }

                cond.await();
                if (type == 'm') {
                    monitor.removeCondition(cond); 
                }
                Common.println("Reader: " + Thread.currentThread() + " starts to read");
                Common.println("serving: " + serving + " rcnt: " + (rcnt + 1));
                rcnt++;
                serving++;
            }} ) ;
    }

    public void endRead() {
        monitor.DoWithin( new Runnable() {
            public void run() {
                rcnt--;
                Common.println("Reader " + Thread.currentThread() + "ends reading");
            }} ) ;
    }

    public void startWrite() {
        monitor.DoWithin( new Runnable() {
            public void run() {
                
                final int myTicket = ticket;
                ticket++;
                AbstractCondition cond;
                switch (type) {
                    case 'n':
                        cond = monitor.makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() {
                                    return ((serving == myTicket) && (rcnt == 0)); } 
                            } 
                            ) ;
                        break;
                    case 'm':
                        cond = ((MapMonitor) monitor).makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() { 
                                    return serving == myTicket && rcnt == 0; } 
                            },
                            "serving == myTicket && rcnt == 0" + "_" + myTicket) ;
                        break;
                    default:
                        // make complex condition
                        cond = ((iMonitor) monitor).makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() { 
                                    return serving == myTicket && rcnt == 0; } 
                            },
                            "serving == myTicket && rcnt == 0" + "_" + myTicket,
                            false) ;
                        break;
                }
                cond.await();
                if (type == 'm') {
                    monitor.removeCondition(cond); 
                }
                Common.println("Writer " + Thread.currentThread() + " starts to write");
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

