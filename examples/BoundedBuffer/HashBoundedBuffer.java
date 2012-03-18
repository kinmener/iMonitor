package examples.BoundedBuffer;

import monitor.AbstractCondition;
import monitor.Assertion;
import monitor.HashMonitor;
import monitor.RunnableWithResult;

public class HashBoundedBuffer implements ObjectBoundedBufferInterface {
    private final Object[] items;
    private int putptr, takeptr, count;
    
    private HashMonitor monitor = new HashMonitor(); //auto-gen
    private AbstractCondition cond_1 = monitor.makeCondition( //auto-gen
            new  Assertion() {
                public boolean isTrue() { return count > 0; }}, 
            "count > 0") ;
    private AbstractCondition cond_0 = monitor.makeCondition( //auto-gen
            new  Assertion() {
                public boolean isTrue() { return count < items.length; }}, 
            "count < items.length") ;

    

    public HashBoundedBuffer(int n) {
        items = new Object[n];
        putptr = takeptr = count = 0;
    }

    public void put(final Object x) {
        System.out.println("in put");
        monitor.DoWithin( new Runnable() {
            public void run() {
                cond_0.await();     //auto-gen iMonitor
                items[putptr] = x; 
                if (++putptr == items.length) putptr = 0;
                ++count;
                System.out.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 
            }} ) ;
    }

    public Object take() {
        System.out.println("in take");
        return monitor.DoWithin( new RunnableWithResult<Object>() {
            public Object run() {
                cond_1.await();     //auto-gen iMonitor
                Object x = items[takeptr]; 
                if (++takeptr == items.length) takeptr = 0;
                --count;
                System.out.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
                return x;
            }} ) ;
    }
}
