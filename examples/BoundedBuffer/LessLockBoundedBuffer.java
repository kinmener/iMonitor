package examples.BoundedBuffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import util.Common;

public class LessLockBoundedBuffer extends ObjectBoundedBuffer {

    int putPtr, takePtr, count;
    final Lock mutex = new ReentrantLock(true);
    final Condition notFull  = mutex.newCondition(); 
    final Condition notEmpty = mutex.newCondition(); 

    public LessLockBoundedBuffer(int n) {
        items = new Object[n];
        putPtr = takePtr = count = 0;
    }

    public void put(Object x) throws InterruptedException {
        int iters = 0;
        boolean yFlag = false;

        while (count == items.length) {
            iters++;
            if (iters > 10000) {
                yFlag = true;
                break;
            }
        }
        if (yFlag) {
            Thread.yield();
        }

        mutex.lock();
        try {
            while (count == items.length) {
                notFull.await();
            }
            items[putPtr] = x; 
            if (++putPtr == items.length) putPtr = 0;
            ++count;
            Common.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 
            notEmpty.signal();
        } finally {
            mutex.unlock();
        }
    }
    public Object take() throws InterruptedException {
        int iters = 0;
        boolean yFlag = false;

        while (count == 0) {
            iters++;
            if (iters > 10000) {
                yFlag = true;
                break;
            }
        }
        if (yFlag) {
            Thread.yield();
        }

        mutex.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }

            Object x = items[takePtr]; 
            if (++takePtr == items.length) takePtr = 0;
            --count;
            Common.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
            notFull.signal();
            return x;
        } finally {
            mutex.unlock();
        }
    }
    public void put(final int n) throws InterruptedException {
    }
    public Object[] take(final int n) throws InterruptedException {
        return null;
    }
    public int getNumFreeSlot() {
        return 0;
    }
    public long getNumContextSwitch() {
        return 0;
    }
}
