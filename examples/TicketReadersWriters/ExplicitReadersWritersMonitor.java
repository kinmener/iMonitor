
package examples.TicketReadersWriters;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;

import util.Common;

public class ExplicitReadersWritersMonitor extends ReadersWritersMonitor {
    private final ReentrantLock mutex = new ReentrantLock(true);
    private final HashMap<Integer, Condition> mapCondition 
        = new HashMap<Integer, Condition>();

    private int ticket;
    private int serving;
    private int rcnt;

    public ExplicitReadersWritersMonitor() {
        ticket = 0;
        serving = 0;
        rcnt = 0;
    }

    public void startRead() {
        mutex.lock();
        int myTicket = ticket;
        ticket++;
        

        Condition cond = mutex.newCondition();
        mapCondition.put(myTicket, cond);

        try {
            while (myTicket != serving) {
                if (mapCondition.containsKey(serving)) {
                    mapCondition.get(serving).signal();
                }
                cond.await(); 
            }

            mapCondition.remove(myTicket);
            Common.println("Reader: " + Thread.currentThread() + " starts to read");
            Common.println("serving: " + serving + " rcnt: " + (rcnt + 1));

            rcnt++;
            serving++;
            if (mapCondition.containsKey(serving)) {
                mapCondition.get(serving).signal();
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }

    public void endRead() {
        mutex.lock();
        rcnt--;
        if (mapCondition.containsKey(serving)) {
            mapCondition.get(serving).signal();
        }
        Common.println("Reader " + Thread.currentThread() + " ends reading");
        mutex.unlock();
    }

    public void startWrite() {
        mutex.lock();
        int myTicket = ticket;
        ticket++;
        Condition cond = mutex.newCondition();
        mapCondition.put(myTicket, cond);
        
        try {
            while (myTicket != serving || rcnt != 0) {
                if (myTicket != serving && mapCondition.containsKey(serving)) {
                    mapCondition.get(serving).signal();
                }
                cond.await(); 
            }
            mapCondition.remove(myTicket);

            Common.println("Writer " + Thread.currentThread() + " starts to write");
            Common.println("serving: " + serving + " rcnt: " + rcnt);
        } 
        catch(InterruptedException e)  {
            e.printStackTrace();
        }
        finally {
            mutex.unlock();
        }
    }

    public void endWrite() {
        mutex.lock();
        serving++;
        if (mapCondition.containsKey(serving)) {
            mapCondition.get(serving).signal();
        }
        Common.println("Writer " + Thread.currentThread() + " end writing");
        mutex.unlock();
    }
}
