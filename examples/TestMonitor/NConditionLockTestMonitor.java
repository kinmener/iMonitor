package examples.TestMonitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pdsl.NConditionLock;
import pdsl.PDSLCondition;

public class NConditionLockTestMonitor extends TestMonitor {
    final NConditionLock mutex = new NConditionLock();
    PDSLCondition[] conds;
    
    private int numProc;
    private int numAccess;
    public NConditionLockTestMonitor(int numProc_) {
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
        while((numAccess % numProc) != myId) {
            try {
                conds[myId].await();
            } catch(InterruptedException e) {
            }
        }
        addSyncTime();
        //System.out.println("myId: " + myId + " numAccess: " + numAccess);
        ++numAccess;
        mutex.unlock();
    }
}
