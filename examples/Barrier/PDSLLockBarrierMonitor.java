/*
 * Classname
 *
 * Version info
 *
 * Copyright notice
 */

package examples.BarrierMonitor;

import pdsl.PDSLLock;


public class PDSLLockBarrierMonitor extends BarrierMonitor { 
    final PDSLLock mutex = new PDSLLock();
    
    private int numProc;
    private int numAccess;
    public PDSLLockBarrierMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;

    }
    public void access(int myId) {
        mutex.lock();
        setCurrentCpuTime();
        ++numAccess;
        while((numAccess % numProc) != 0) {
            try {
                mutex.await(); 
            } catch(InterruptedException e) {
            }
        }
        addSyncTime();
        //System.out.println("myId: " + myId + " numAccess: " + numAccess);
        mutex.unlock();
    }
}
