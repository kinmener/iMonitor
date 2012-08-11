
package examples.ReadersWriters;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import examples.util.Common;

public class ExplicitReadersWritersMonitor extends ReadersWritersMonitor {
    final ReentrantLock mutex = new ReentrantLock();
    final Condition okay_read = mutex.newCondition(); 
    final Condition okay_write = mutex.newCondition(); 

    int rcnt;
    int wcnt;

    public ExplicitReadersWritersMonitor() {
        rcnt = 0;
        wcnt = 0;
    }

    public void startRead() {
        mutex.lock();
        try {
            while(wcnt == 1 || mutex.getWaitQueueLength(okay_write) > 0) {
                okay_read.await();
            }

            rcnt++;
            okay_read.signal();
            Common.println("Reader " + Thread.currentThread() + "starts to read");
            Common.println("wwaiting: " + mutex.getWaitQueueLength(okay_write) + "\t rcnt: " + rcnt + "\twcnt: " + wcnt);
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
          mutex.unlock();
        }
    }

    public void endRead() {
        mutex.lock();
        rcnt--;
        if(rcnt == 0) {
            okay_write.signal();
        }
        Common.println("Reader " + Thread.currentThread() + "ends reading");
        mutex.unlock();
    }

    public void startWrite() {
        mutex.lock();
        try {
            while(rcnt != 0 || wcnt != 0) {
                okay_write.await();
            }
            wcnt = 1;
            Common.println("Writer " + Thread.currentThread() + "starts to write");
            Common.println("wwaiting: " + mutex.getWaitQueueLength(okay_write) + "\t rcnt: " + rcnt + "\twcnt: " + wcnt);
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
        wcnt = 0;
        if(mutex.getWaitQueueLength(okay_write) != 0) {
            okay_write.signal();
        }
        else {
            okay_read.signal();
        }
        Common.println("Writer " + Thread.currentThread() + "end writing");
        mutex.unlock();
    }
}
