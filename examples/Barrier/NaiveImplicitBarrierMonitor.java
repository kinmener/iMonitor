package examples.BarrierMonitor;
import monitor.*;	//auto-gen iMonitor

public class NaiveImplicitBarrierMonitor extends BarrierMonitor {
    private AbstractImplicitMonitor __monitor__628 = new NaiveImplicitMonitor(); //auto-gen
    private int numProc;
    private int numAccess;

    public NaiveImplicitBarrierMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;
    }

    public void access(int myId) {
        __monitor__628.DoWithin( new Runnable() {
            public void run() {
                ++numAccess;
                AbstractCondition cond_1 = __monitor__628.makeCondition( //auto-gen
                    new Assertion() {
                        public boolean isTrue() { return (numAccess % numProc) == 0; } 
                    } 
                ) ;
                setCurrentCpuTime();
                cond_1.await();
                addSyncTime();
            }} ) ;
    }
}
