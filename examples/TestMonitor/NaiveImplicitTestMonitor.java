
package examples.TestMonitor;
import monitor.*;	//auto-gen iMonitor

public class NaiveImplicitTestMonitor implements TestMonitor {
    private AbstractImplicitMonitor __monitor__628 = new NaiveImplicitMonitor(); //auto-gen
    private int numProc;
    private int numAccess;

    public NaiveImplicitTestMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;
    }

    public void access(int myId) {
        final int myId_dummy = myId;
        __monitor__628.DoWithin( new Runnable() {
            public void run() {
                AbstractCondition cond_1 = __monitor__628.makeCondition( //auto-gen
                    new Assertion() {
                        public boolean isTrue() { return (numAccess % numProc) == myId_dummy; } } ) ;
                cond_1.await();
                //System.out.println("myId: " + myId_dummy + " numAccess: " + numAccess);
                ++numAccess;
                __monitor__628.removeCondition(cond_1);
            }} ) ;
    }
}
