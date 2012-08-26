package examples.RoundRobinMonitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import util.Common;

public class ExplicitRoundRobinMonitor extends RoundRobinMonitor {
    final Lock mutex = new ReentrantLock();
    Condition[] conds;
    
    private int numProc;
    private int numAccess;
    public ExplicitRoundRobinMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;

        conds = new Condition[numProc];

        for(int i = 0; i < numProc; ++i) {
            conds[i] = mutex.newCondition();
        }
    }
    public void access(int myId) {
        mutex.lock();
        while((numAccess) != myId) {
            try {
                conds[numAccess].signal();
                conds[myId].await();
            } catch(InterruptedException e) {
            }
        }
        Common.println("myId: " + myId + " numAccess: " + numAccess);
        ++numAccess;
        numAccess %= numProc;
        conds[numAccess].signal();
        mutex.unlock();
    }
}
