
package examples.ReadersWriters;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ExplicitReadersWritersMonitor implements ReadersWritersMonitor {
    final Lock mutex = new ReentrantLock();
    final Condition okay_read = mutex.newCondition(); 
    final Condition okay_write = mutex.newCondition(); 

    int rcnt;
    int wcnt;
    int wwaiting;

    public ExplicitReadersWritersMonitor() {
        rcnt = 0;
        wcnt = 0;
        wwaiting = 0;
    }

    public void startRead() {
        mutex.lock();
        try {
            while(wcnt == 1 || wwaiting > 0) {
                okay_read.await();
            }

            rcnt++;
            okay_read.signal();
            System.out.println("Reader " + Thread.currentThread() + "starts to read");
            System.out.println("wwaiting: " + wwaiting + "\t rcnt: " + rcnt + "\twcnt: " + wcnt);
            System.out.flush();
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
        System.out.println("Reader " + Thread.currentThread() + "ends reading");
        System.out.flush();
        mutex.unlock();
    }

    public void startWrite() {
        mutex.lock();
        try {
            wwaiting++;
            while(rcnt != 0 || wcnt != 0) {
                okay_write.await();
            }
            wwaiting--;
            wcnt = 1;
            System.out.println("Writer " + Thread.currentThread() + "starts to write");
            System.out.println("wwaiting: " + wwaiting + "\t rcnt: " + rcnt + "\twcnt: " + wcnt);
            System.out.flush();
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
        if(wwaiting != 0) {
            okay_write.signal();
        }
        else {
            okay_read.signal();
        }
        System.out.println("Writer " + Thread.currentThread() + "end writing");
        System.out.flush();
        mutex.unlock();
    }
}
