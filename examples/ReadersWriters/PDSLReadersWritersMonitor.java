
package examples.ReadersWriters;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import pdsl.PDSLLock;

public class PDSLReadersWritersMonitor extends ReadersWritersMonitor {
    final PDSLLock mutex = new PDSLLock();

    int rcnt;
    int wcnt;
    int wwaiting;

    public PDSLReadersWritersMonitor() {
        rcnt = 0;
        wcnt = 0;
        wwaiting = 0;
    }

    public void startRead() {
        mutex.lock();
        try {
            setCurrentCpuTime();
            while(wcnt == 1 || wwaiting > 0) {
                mutex.await();
            }
            addSyncTime();

            rcnt++;
            //System.out.println("Reader " + Thread.currentThread() + "starts to read");
            //System.out.println("wwaiting: " + mutex.getWaitQueueLength(okay_write) + "\t rcnt: " + rcnt + "\twcnt: " + wcnt);
            //System.out.flush();
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
          mutex.unlock();
        }
    }

    public void endRead() {
        mutex.lock();
        rcnt--;
        //System.out.println("Reader " + Thread.currentThread() + "ends reading");
        //System.out.flush();
        mutex.unlock();
    }

    public void startWrite() {
        mutex.lock();
        try {
            wwaiting++;
            setCurrentCpuTime();
            while(rcnt != 0 || wcnt != 0) {
                mutex.await();
            }
            addSyncTime();
            wwaiting--;
            wcnt = 1;
            //System.out.println("Writer " + Thread.currentThread() + "starts to write");
            //System.out.println("wwaiting: " + mutex.getWaitQueueLength(okay_write) + "\t rcnt: " + rcnt + "\twcnt: " + wcnt);
            //System.out.flush();
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
        //System.out.println("Writer " + Thread.currentThread() + "end writing");
        //System.out.flush();
        mutex.unlock();
    }
}
