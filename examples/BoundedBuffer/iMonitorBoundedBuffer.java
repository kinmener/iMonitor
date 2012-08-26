package examples.BoundedBuffer;

import monitor.AbstractCondition;
import monitor.AbstractImplicitMonitor;
import monitor.Assertion;
import monitor.GlobalVariable;
import monitor.SimpleCondition;
import monitor.iMonitor;
import monitor.MapMonitor;
import monitor.NaiveImplicitMonitor;
import monitor.RunnableWithResult;

import util.Common;

public class iMonitorBoundedBuffer extends ObjectBoundedBuffer {
    private int putPtr, takePtr, count;

    private final char type;

    private final AbstractImplicitMonitor monitor;
    private final AbstractCondition notEmpty;
    private AbstractCondition notFull;


    public iMonitorBoundedBuffer(int n, char type) {
        items = new Object[n];
        putPtr = takePtr = count = 0;

        this.type = type;

        switch (type) {
            case 'n':
                monitor = new NaiveImplicitMonitor(); //auto-gen
                notEmpty = monitor.makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { 
                                return count > 0; 
                            } 
                        } ) ;
                notFull = monitor.makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { 
                                return count < items.length; 
                            } 
                        } ) ;
                break;
            case 'm':
                monitor = new MapMonitor(); //auto-gen
                notEmpty = ((MapMonitor) monitor).makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { return count > 0; }
                        }, 
                        "count > 0") ;
                notFull = ((MapMonitor) monitor).makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { return count < items.length; }
                        }, 
                        "count < items.length") ;
                break;
            default:            
                monitor = new iMonitor(); //auto-gen
                ((iMonitor) monitor).registerGlobalVariable(
                    new GlobalVariable("count") {
                        public int getValue() {
                            return count;
                        }
                    } 
                    ); 
                notEmpty = ((iMonitor) monitor).makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { return count > 0; }
                        }, 
                        "count > 0", true);

                notFull = ((iMonitor) monitor).makeCondition( //auto-gen
                        new  Assertion() {
                            public boolean isTrue() { return count < items.length; }
                        }, 
                        "count < items.length", true);

        }
    }

    public void put(final Object x) {
        monitor.DoWithin( new Runnable() {
            public void run() {
                notFull.await();     //auto-gen iMonitor
                items[putPtr] = x; 
                if (++putPtr == items.length) putPtr = 0;
                ++count;
                Common.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 
            }} ) ;
    }

    public Object take() {
        return monitor.DoWithin( new RunnableWithResult<Object>() {
            public Object run() {
                notEmpty.await();     //auto-gen iMonitor
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
                AbstractCondition cond;
                switch (type) {
                    case 'n':
                        cond = monitor.makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() { return (n + count) <= items.length; } 
                            } 
                            ) ;
                        break;
                    case 'm':
                        cond = ((MapMonitor) monitor).makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() { return (n + count) <= items.length; } 
                            }, 
                            "(n + count) <= items.length" + "_" + n
                            ) ;
                        break;
                    default:
                        cond = ((iMonitor) monitor).makeCondition("count", 
                            items.length - n, 
                            SimpleCondition.OperationType.LTE, false);
                        break;
                }
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
                AbstractCondition cond;
                switch (type) {
                    case 'n':
                        cond = monitor.makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() { return n <= count; } 
                            } 
                            ) ;
                        break;
                    case 'm':
                        cond = ((MapMonitor) monitor).makeCondition( //auto-gen
                            new Assertion() {
                                public boolean isTrue() { return n <= count; } 
                            } ,
                            "(n + count) <= items.length" + "_" + n
                            ) ;
                        break;
                    default:
                        cond = ((iMonitor) monitor).makeCondition("count", 
                            n, SimpleCondition.OperationType.GTE, false);
                        break;
                }
                cond.await();
                Object[] ret = new Object[n];
                for (int i = 0; i < n; i++) {
                    ret[i] = items[takePtr++];
                    if (++takePtr == items.length) takePtr = 0;
                }
                count -= n;
                Common.println("Consumer " + Thread.currentThread() + " takes " + n + " objs, remaining #obj: " + count) ; 
                return ret;
            }} ) ;
    }
}
