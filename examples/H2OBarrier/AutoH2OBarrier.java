
package examples.H2OBarrier;
import monitor.*;


import util.Common;

public class AutoH2OBarrier extends H2OBarrier {

    private final char type;
    private final AbstractImplicitMonitor monitor;
    private AbstractCondition HWait;
    private AbstractCondition OWait;


    public AutoH2OBarrier(char type) {

        this.type = type;
        switch (type) {
            case 'n':
                monitor = new NaiveImplicitMonitor();
                HWait = monitor.makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { 
                                return ((aH > 0) || (wO >= 1 && wH >= 2));
                            } 
                        } ) ;
                OWait = monitor.makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { 
                                return ((aO > 0) || (wH >= 2));
                            } 
                        } ) ;
                break;
            case 's':
                monitor = new SetMonitor();
                HWait = monitor.makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { 
                                return ((aH > 0) || (wO >= 1 && wH >= 2));
                            } 
                        } ) ;
                OWait = monitor.makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { 
                                return ((aO > 0) || (wH >= 2));
                            } 
                        } ) ;
                break;
            case 't':
                monitor = new TagMonitor();
                HWait = ((TagMonitor) monitor).makeCondition(
                        "HWait",
                        new Assertion() {
                            public boolean isTrue() {
                                return ((aH > 0) || (wO >= 1 && wH >= 2));
                            }
                        },
                        true,
                        null 
                        );
                OWait = ((TagMonitor) monitor).makeCondition(
                        "OWait",
                        new Assertion() {
                            public boolean isTrue() {
                                return ((aO > 0) || (wH >= 2));
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

    public void OReady() {
        monitor.DoWithin( new Runnable() {
            public void run() {
                wO++;
                OWait.await();

                if (aO == 0) {
                    wH -= 2;
                    aH += 2;
                    wO -= 1;
                    numWater++;
                    Common.println(numWater + " water is made");    
                } else {
                    aO--; 
                }
            }}
        );
    }

    public void HReady() {
        monitor.DoWithin( new Runnable() {
            public void run() {
                wH++;
                HWait.await();
                
                if (aH == 0) {
                    wH -= 2;
                    aH += 1;
                    wO -= 1;
                    aO += 1;
                    numWater++;
                    Common.println(numWater + " water is made");    
                } else {
                    aH -= 1;
                }
            }}
        );
    }
}
