package examples.RoundRobinMonitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.HashMap;

import util.Common;

public class MapExplicitRoundRobinMonitor extends RoundRobinMonitor {
    final Lock mutex = new ReentrantLock();
    HashMap<Integer, Condition> mapCond;
    
    private int numProc;
    private int numAccess;
    public MapExplicitRoundRobinMonitor(int numProc_) {
        numProc = numProc_;
        numAccess = 0;

        mapCond = new HashMap<Integer, Condition>();

        for(int i = 0; i < numProc; ++i) {
            mapCond.put(i, mutex.newCondition());
        }
    }
    public void access(int myId) {
        mutex.lock();
        while((numAccess) != myId) {
            try {
                mapCond.get(numAccess).signal();
                mapCond.get(myId).await();
            } catch(InterruptedException e) {
            }
        }
        Common.println("myId: " + myId + " numAccess: " + numAccess);
        ++numAccess;
        numAccess %= numProc;
        mapCond.get(numAccess).signal();
        mutex.unlock();
    }
}
