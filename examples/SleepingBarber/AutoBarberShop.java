
package examples.SleepingBarber;
import monitor.*;

import util.Common;

class AutoBarberShop extends BarberShop {
    
    int numAvailableBarber;
    
    private final char type;
    private final AbstractImplicitMonitor monitor;
    private AbstractCondition barberReady;
    private AbstractCondition consumerReady;


    public AutoBarberShop(int maxFreeSeat, char type) {
        super(maxFreeSeat);
        numAvailableBarber = 0;
        this.type = type;
        final int maxSeat = maxFreeSeat;
        switch (type) {
            case 'n':
                monitor = new NaiveImplicitMonitor();
                consumerReady = monitor.makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { 
                                return numFreeSeat < maxSeat;
                            } 
                        } ) ;
                barberReady = monitor.makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { 
                                return numAvailableBarber > 0; 
                            } 
                        } ) ;
                break;
            case 's':
                monitor = new SetMonitor();
                consumerReady = monitor.makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { 
                                return numFreeSeat < maxSeat;
                            } 
                        } ) ;
                barberReady = monitor.makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { 
                                return numAvailableBarber > 0; 
                            } 
                        } ) ;
                break;
            case 't':
                monitor = new TagMonitor();
                ((TagMonitor) monitor).registerGlobalVariable(
                    new GlobalVariable("numFreeSeat") {
                        public int getValue() {
                            return numFreeSeat;
                        }
                    } 
                    );
                consumerReady = ((TagMonitor) monitor).makeCondition(
                        "numFreeSet < maxSeat",
                        new Assertion() {
                            public boolean isTrue() {
                                return numFreeSeat < maxSeat;
                            }
                        },
                        true,
                        null 
                        );
                barberReady = ((TagMonitor) monitor).makeCondition(
                        "numAvailableBarber > 0",
                        new Assertion() {
                            public boolean isTrue() {
                                return numAvailableBarber > 0;
                            }
                        },
                        true,
                        null 
                        );
                break;
            default:
                monitor = null;
                break;
        }
    }

    public void cutHair() {
        monitor.DoWithin( new Runnable() {
            public void run() {
                Common.println("baber wait for consumer");
                consumerReady.await();     //auto-gen iMonitor
                numFreeSeat++;
                numAvailableBarber++;
        Common.println("Barber " + Thread.currentThread() + " cut hair");
            }} ) ;
    }

    public boolean waitToCut() {
        return monitor.DoWithin( new RunnableWithResult<Boolean>() {
            public Boolean run() {
                if (numFreeSeat == 0) {
                    return false;
                }
                numFreeSeat--;
                Common.println("consumer wait for a barber");
                barberReady.await();
                numAvailableBarber--;
        Common.println("Consumer " + Thread.currentThread() + "been cut");
                return true;
            }} ) ;
    }
}
