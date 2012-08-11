
package examples.BoundedBuffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import examples.util.Common;


class ExplicitBoundedBuffer extends ObjectBoundedBuffer{
    final Lock mutex = new ReentrantLock();
    final Condition notFull  = mutex.newCondition(); 
    final Condition notEmpty = mutex.newCondition(); 

    int putPtr, takePtr, count;

    public ExplicitBoundedBuffer(int n) {
        items = new Object[n];
        putPtr = takePtr = count = 0;
    }

    public void put(Object x) throws InterruptedException {
        mutex.lock();
        try {
            while (count == items.length) 
                notFull.await();
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
        mutex.lock();
        try {
            while (count == 0) 
                notEmpty.await();

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
        mutex.lock();
        try {
            while ((n + count) >= items.length) 
                notFull.await();
            for (int i = 0; i < n; i++) {
                items[putPtr++] = new Object(); 
                if (putPtr == items.length) putPtr = 0;
            }
            count += n;
            Common.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 
            notEmpty.signalAll();
        } finally {
            mutex.unlock();
        }
    }

    public Object[] take(final int n) throws InterruptedException {
        mutex.lock();
        try {
            while (count < n) 
                notEmpty.await();

            Object[] ret = new Object[n];

            for (int i = 0; i < n; i++) {
                ret[i] = items[takePtr++];
                if (takePtr == items.length) takePtr = 0;
            }
            count -= n;
            Common.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
            notFull.signalAll();
            return ret;
        } finally {
            mutex.unlock();
        }
    }
}
