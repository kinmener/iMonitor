package examples.SleepingBarber;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import util.Common;

public class ExplicitBarberShop extends BarberShop {
    ReentrantLock mutex = new ReentrantLock(true);
    Condition custReady = mutex.newCondition();
    Condition barberReady = mutex.newCondition();

    int numAvailableBarber;

    public ExplicitBarberShop(int maxFreeSeat) {
        super(maxFreeSeat);
        numAvailableBarber = 0;
    }
    public void cutHair() {
        mutex.lock();
        try {
            while (maxFreeSeat == numFreeSeat) {
                Common.println("baber wait for consumer");
                custReady.await(); 
            }

        } catch(InterruptedException e) {
        }

        numFreeSeat++;
        numAvailableBarber++;
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

                while (numAvailableBarber == 0) {
                    barberReady.await();
                }
            } catch(InterruptedException e) {
            }
            ret = true;
        } 
        numAvailableBarber--;
        Common.println("Consumer " + Thread.currentThread() + "been cut");
        mutex.unlock();
        return ret;
    }
}
