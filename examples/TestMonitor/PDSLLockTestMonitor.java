package examples.TestMonitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pdsl.PDSLLock;

public class PDSLLockTestMonitor extends TestMonitor {
    final PDSLLock mutex = new PDSLLock();
    
    private int numProc;
    private int numAccess;
    public PDSLLockTestMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;

    }
    public void access(int myId) {
        mutex.lock();
        setCurrentCpuTime();
        while((numAccess % numProc) != myId) {
            try {
                mutex.await(); 
            } catch(InterruptedException e) {
            }
        }
        addSyncTime();
        //System.out.println("myId: " + myId + " numAccess: " + numAccess);
        ++numAccess;
        mutex.unlock();
    }
}
