
package examples.TestMonitor;
import monitor.*;	//auto-gen iMonitor

public class MultiConditionGlobalTestMonitor implements TestMonitor {
    private AbstractImplicitMonitor __monitor__628 = new MultiConditionMonitor(); //auto-gen
    private AbstractCondition[] conds;
    private int numProc;
    private int numAccess;

    public MultiConditionGlobalTestMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;
        conds = new AbstractCondition[numProc];
        for(int i = 0; i < numProc; ++i) {
            final int i_dummy = i;
            conds[i] = __monitor__628.makeCondition( 
                    new Assertion() {
                        public boolean isTrue() { return (numAccess % numProc) == i_dummy; } } ) ;

        }
    }

    public void access(int myId) {
        final int myId_dummy = myId;
        __monitor__628.DoWithin( new Runnable() {
            public void run() {
                conds[myId_dummy].await();
                //System.out.println("myId: " + myId_dummy + " numAccess: " + numAccess);
                ++numAccess;
            }} ) ;
    }
}
