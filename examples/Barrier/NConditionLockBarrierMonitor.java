package examples.BarrierMonitor;

import pdsl.NConditionLock;
import pdsl.PDSLCondition;

public class NConditionLockBarrierMonitor extends BarrierMonitor {
    final NConditionLock mutex = new NConditionLock();
    PDSLCondition[] conds;
    
    private int numProc;
    private int numAccess;
    public NConditionLockBarrierMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;

        conds = new PDSLCondition[numProc];

        for(int i = 0; i < numProc; ++i) {
            conds[i] = mutex.newCondition();
        }

    }
    public void access(int myId) {
        mutex.lock();
        setCurrentCpuTime();
        ++numAccess;
        while((numAccess % numProc) != 0) {
            try {
                conds[myId].await();
            } catch(InterruptedException e) {
            }
        }
        addSyncTime();
        //System.out.println("myId: " + myId + " numAccess: " + numAccess);
        mutex.unlock();
    }
}
