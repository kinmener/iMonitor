package examples.SleepingBarber;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import util.Common;

public class ExplicitBarberShop extends BarberShop {
    ReentrantLock mutex = new ReentrantLock(true);
    Condition custReady = mutex.newCondition();
    Condition barberReady = mutex.newCondition();

    public ExplicitBarberShop(int maxFreeSeat) {
        super(maxFreeSeat);
    }
    public void cutHair() {
        mutex.lock();
        try {
            while (maxFreeSeat == numFreeSeat) {
                Common.println("baber wait for consumer");
                custReady.await(); 
            }
            numFreeSeat++;

        } catch(InterruptedException e) {
        }
        barberReady.signal();
        Common.println("Barber " + Thread.currentThread() + " cut hair");
        mutex.unlock();
    } 
    public boolean waitToCut() {
        mutex.lock();
        boolean ret = false;
        if (numFreeSeat > 0) {
            numFreeSeat--;
            custReady.signal(); 
            try {
                Common.println("consumer wait for a barber");
                barberReady.await();
            } catch(InterruptedException e) {
            }
            ret = true;
        } 
        Common.println("Consumer " + Thread.currentThread() + "been cut");
        mutex.unlock();
        return ret;
    }
}
