package examples.BarrierMonitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExplicitBarrierMonitor extends BarrierMonitor {
    final Lock mutex = new ReentrantLock();

    Condition cond;
    
    private int numProc;
    private int numAccess;
    
    public ExplicitBarrierMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;

        cond = mutex.newCondition();

    }
    public void access(int myId) {
        mutex.lock();
        setCurrentCpuTime();
        numAccess++;
        while((numAccess % numProc) != 0) {
            //System.out.println(Thread.currentThread() + " numAccess: " + numAccess);
            try {
                cond.await();
            } catch(InterruptedException e) {
            }
        }
        addSyncTime();
        //System.out.println("myId: " + myId_dummy + " numAccess: " + numAccess);
        cond.signalAll();
        mutex.unlock();
    }
}
