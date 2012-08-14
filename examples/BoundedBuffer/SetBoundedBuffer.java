
package examples.BoundedBuffer;
import monitor.AbstractCondition;
import monitor.Assertion;
import monitor.SetMonitor;
import monitor.RunnableWithResult;

import examples.util.Common;

public class SetBoundedBuffer extends ObjectBoundedBuffer {
    private SetMonitor monitor = new SetMonitor(); //auto-gen
    private AbstractCondition cond_1 = monitor.makeCondition( //auto-gen
            new  Assertion() {
                public boolean isTrue() { return count > 0; } 
            } ) ;
    private AbstractCondition cond_0 = monitor.makeCondition( //auto-gen
            new  Assertion() {
                public boolean isTrue() { return count < items.length; } 
            } ) ;

    private int putPtr, takePtr, count;

    public SetBoundedBuffer(int n) {
        items = new Object[n];
        putPtr = takePtr = count = 0;
    }

    public void put(final Object x) {
        monitor.DoWithin( new Runnable() {
            public void run() {
                cond_0.await();	//auto-gen iMonitor
                items[putPtr] = x; 
                if (++putPtr == items.length) putPtr = 0;
                ++count;
                Common.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 
            }} ) ;
    }

    public Object take() {
        return monitor.DoWithin( new RunnableWithResult<Object>() {
            public Object run() {
                cond_1.await();	//auto-gen iMonitor
                Object x = items[takePtr]; 
                if (++takePtr == items.length) takePtr = 0;
                --count;
                Common.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
                return x;
            }} ) ;
    }

    public void put(final int n) {
        monitor.DoWithin( new Runnable() {
            public void run() {
                AbstractCondition cond = monitor.makeCondition( //auto-gen
                    new Assertion() {
                        public boolean isTrue() { return (n + count) <= items.length; } 
                    } 
                ) ;
                cond.await();

                for (int i = 0; i < n; i++) {
                    items[putPtr++] = new Object();
                    if (putPtr == items.length) putPtr = 0;
                }
                count += n;
                Common.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 
            }} ) ;
    }

    public Object[] take(final int n) {
        return monitor.DoWithin( new RunnableWithResult<Object[]>() {
            public Object[] run() {
                AbstractCondition cond = monitor.makeCondition( //auto-gen
                    new Assertion() {
                        public boolean isTrue() { return n <= count; } 
                    } 
                ) ;
                cond.await();
                Object[] ret = new Object[n];
                for (int i = 0; i < n; i++) {
                    ret[i] = items[takePtr++];
                    if (++takePtr == items.length) takePtr = 0;
                }
                count -= n;
                Common.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
                return ret;
            }} ) ;
    }
}
