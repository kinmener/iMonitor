package examples.SleepingBarber;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class ExplicitBarberShop extends BarberShop {
    ReentrantLock mutex = new ReentrantLock(true);
    Condition custReady = mutex.newCondition();
    Condition barberReady = mutex.newCondition();

    public void cutHair() {
        mutex.lock();
        try {
            while (maxFreeSeat == numFreeSeat) {
                custReady.await(); 
            }
            numFreeSeat++;

        } catch(InterruptedException e) {
        }
        barberReady.signal();
        mutex.unlock();
    } 
    public boolean waitToCut() {
        mutex.lock();
        boolean ret = false;
        if (numFreeSeat > 0) {
            numFreeSeat--;
            custReady.signal(); 
            try {
                barberReady.await();
            } catch(InterruptedException e) {
            }
            ret = true;
        } 
        mutex.unlock();
        return ret;
    }
}
