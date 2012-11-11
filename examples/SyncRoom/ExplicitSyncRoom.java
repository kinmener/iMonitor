package examples.SyncRoom;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import util.Common;

public class ExplicitSyncRoom extends SyncRoom {
  
    ReentrantLock mutex = new ReentrantLock(true);
    private final HashMap<Integer, Condition> mapCondition 
        = new HashMap<Integer, Condition>();


    public ExplicitSyncRoom(int N) {
        super(N);
    }
    public void enterRoom(int n) {
        mutex.lock();
        int myTicket = ticket;
        ticket++;

       
        Condition cond = null;
        if (myTicket != serving || (usedRoom != n && usedRoom != -1)) {
            cond = mutex.newCondition();
            mapCondition.put(myTicket, cond);
        }
       
        try {
            while (myTicket != serving || (usedRoom != n && usedRoom != -1)) {
                if (myTicket != serving && mapCondition.containsKey(serving)) {
                    mapCondition.get(serving).signal(); 
                }
                //Common.println(Thread.currentThread() 
                //        + " waits for entering room " + n + ", myTicket: " 
                //        + myTicket + ", serving: " + serving 
                //        + ", usedRoom: "+ usedRoom);
                cond.await();
            }
            Common.println(Thread.currentThread() + " enters room " + n
                    + ", myTicket: " + myTicket + ", serving: " + serving 
                    + ", usedRoom: "+ usedRoom);

            usedRoom = n; 
            if (cond != null) {
                mapCondition.remove(myTicket);
            }
            rcnt[n]++;
            serving++;
        } catch(Exception e) {
        } finally {
            mutex.unlock();
        }
    }
    
    public void leaveRoom(int n) {
        mutex.lock();
        rcnt[n]--;
        if (rcnt[n] == 0) {
            usedRoom = -1;
            Condition cond = mapCondition.get(serving);
            if (cond != null) {
                cond.signal();
            }
        }
        Common.println(Thread.currentThread() + " leaves room " + n);
        mutex.unlock();
    }
}
