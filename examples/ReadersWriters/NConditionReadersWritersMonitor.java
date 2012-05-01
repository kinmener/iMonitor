
package examples.ReadersWriters;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import pdsl.NConditionLock;
import pdsl.PDSLCondition;

public class NConditionReadersWritersMonitor extends ReadersWritersMonitor {
    final NConditionLock mutex = new NConditionLock();
    final PDSLCondition okay_read = mutex.newCondition(); 
    final PDSLCondition okay_write = mutex.newCondition(); 

    int rcnt;
    int wcnt;
    int wwaiting;

    public NConditionReadersWritersMonitor() {
        rcnt = 0;
        wcnt = 0;
        wwaiting = 0;
    }

    public void startRead() {
        mutex.lock();
        try {
            setCurrentCpuTime();
            while(wcnt == 1 || wwaiting > 0) {
                okay_read.await();
            }
            addSyncTime();

            rcnt++;
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
        System.out.println("Reader " + Thread.currentThread() + "ends reading");
        System.out.flush();
        mutex.unlock();
    }

    public void startWrite() {
        mutex.lock();
        try {
            wwaiting++;
            setCurrentCpuTime();
            while(rcnt != 0 || wcnt != 0) {
                okay_write.await();
            }
            addSyncTime();
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
        System.out.println("Writer " + Thread.currentThread() + "end writing");
        System.out.flush();
        mutex.unlock();
    }
}
