package examples.BoundedBuffer;

import java.util.concurrent.atomic.AtomicReference;

import util.Common;

public class NoLockBoundedBuffer extends ObjectBoundedBuffer {

    int putPtr, takePtr;
    volatile int count;
    AtomicReference<Thread> refThread;

    public NoLockBoundedBuffer(int n) {
        items = new Object[n];
        putPtr = takePtr = count = 0;
        refThread = new AtomicReference<Thread>(null);
    }
    public void put(Object x) throws InterruptedException {

        for (; ; ) {
            while (count == items.length);

            if (refThread.compareAndSet(null, Thread.currentThread())) {
                if (count == items.length) {
                    refThread.compareAndSet(Thread.currentThread(), null);
                } else {
                    break;
                }
            }    
        }

        assert(count < items.length);

        items[putPtr] = x; 
        if (++putPtr == items.length) putPtr = 0;
        ++count;
        Common.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 

        refThread.compareAndSet(Thread.currentThread(), null);
            //Common.println("reference reset to null suc " + Thread.currentThread());
    }
    public Object take() throws InterruptedException {
        for (; ; ) {
            
            while (count == 0);
            if (refThread.compareAndSet(null, Thread.currentThread())) {
                if (count == 0) {
                    refThread.compareAndSet(Thread.currentThread(), null);
                } else {
                    break;
                }
            }
        }

        assert(count > 0);
        
        Object x = items[takePtr]; 
        if (++takePtr == items.length) takePtr = 0;
        --count;
        Common.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
        
        refThread.compareAndSet(Thread.currentThread(), null);
        return x;
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
