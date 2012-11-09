package examples.H2OBarrier;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import util.Common;

public class ExplicitH2OBarrier extends H2OBarrier {
  
    ReentrantLock mutex = new ReentrantLock(true);
    Condition HWait = mutex.newCondition();
    Condition OWait = mutex.newCondition();

    public void OReady() {
        mutex.lock();
        wO++;
        while ((aO == 0) && (wH < 2)) {
            try {
                OWait.await(); 
            } catch(InterruptedException e) {
            }
        }

        if (aO == 0) {
            wH -= 2;
            aH += 2;
            wO -= 1;
            numWater++;
            Common.println(numWater + " water is made");    
            HWait.signal();
            HWait.signal();

        } else {
            aO -= 1;
        }

        mutex.unlock();
    }
    
    public void HReady() {
        mutex.lock();
        wH++;
        while((aH == 0) && (wO < 1 || wH < 2)) {
            try {
                HWait.await();
            } catch(InterruptedException e) {
            }
        }

        if (aH == 0) {
            wH -= 2;
            aH += 1;
            wO -= 1;
            aO += 1;

            numWater++;
            Common.println(numWater + " water is made");    
            HWait.signal();
            OWait.signal();
           
        } else {
            aH -= 1;
        }

        mutex.unlock();
    }
}
