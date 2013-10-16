
package examples.BoundedBuffer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

import util.Common;


class JDKBoundedBuffer extends ObjectBoundedBuffer{
    private long numContextSwitch = 0;
    private BlockingQueue<Object> internalQueue;
    public JDKBoundedBuffer(int n) {
        internalQueue = new LinkedBlockingQueue<Object>(n); 
    }

    public int getNumFreeSlot() {
        return items.length - internalQueue.size();
    }
    public long getNumContextSwitch() {
        return numContextSwitch;
    }

    public void put(Object x) throws InterruptedException {
        internalQueue.put(x);
        Common.println("Producer " + Thread.currentThread() 
                        + " puts, #obj: " + internalQueue.size()) ; 
    }

    public Object take() throws InterruptedException {
        Object ret = internalQueue.take();
                Common.println("Consumer " + Thread.currentThread() 
                    + " takes, #obj: " + internalQueue.size()) ; 
        return ret;
    }

    public void put(final Object[] objs) throws InterruptedException {
    }

    public Object[] take(final int n) throws InterruptedException {
        return null;
    }
}

