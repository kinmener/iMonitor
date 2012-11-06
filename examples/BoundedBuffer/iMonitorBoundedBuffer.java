package examples.BoundedBuffer;

import monitor.*;

import util.Common;

public class iMonitorBoundedBuffer extends ObjectBoundedBuffer {
    private int putPtr, takePtr, count;

    private final char type;

    private final AbstractImplicitMonitor monitor;
    private final AbstractCondition notEmpty;
    private AbstractCondition notFull;


    public int getNumFreeSlot() {
        return items.length - count;
    }

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
            case 's':
                monitor = new SetMonitor(); //auto-gen
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
                            public boolean isTrue() { 
                                return count < items.length; 
                            }
                        }, 
                        "count < items.length") ;
                break;
            case 't':
                monitor = new TagMonitor();
                ((TagMonitor) monitor).registerGlobalVariable(
                    new GlobalVariable("count") {
                        public int getValue() {
                            return count;
                        }
                    } 
                    );
                notEmpty = ((TagMonitor) monitor).makeCondition(
                        "count > 0",
                        new Assertion() {
                            public boolean isTrue() {
                                return count > 0;
                            }
                        },
                        true,
                        null 
                        );
                notFull = ((TagMonitor) monitor).makeCondition(
                        "count < items.length",
                        new Assertion() {
                            public boolean isTrue() {
                                return count < items.length;
                            }
                        },
                        true,
                        null 
                        );
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
                        "count > 0",
                        new  Assertion() {
                            public boolean isTrue() { return count > 0; }
                        }, 
                        true);

                notFull = ((iMonitor) monitor).makeCondition( //auto-gen
                        "count < items.length", 
                        new  Assertion() {
                            public boolean isTrue() { 
                                return count < items.length; 
                            }
                        }, 
                        true);

        }
    }

    public void put(final Object x) {
        monitor.DoWithin( new Runnable() {
            public void run() {
                notFull.await();     //auto-gen iMonitor
                items[putPtr] = x; 
                if (++putPtr == items.length) putPtr = 0;
                ++count;
                Common.println("Producer " + Thread.currentThread() 
                    + " puts, #obj: " + count) ; 
            }} ) ;
    }

    public Object take() {
        return monitor.DoWithin( new RunnableWithResult<Object>() {
            public Object run() {
                notEmpty.await();     //auto-gen iMonitor
                Object x = items[takePtr]; 
                if (++takePtr == items.length) takePtr = 0;
                --count;
                Common.println("Consumer " + Thread.currentThread() 
                    + " takes, #obj: " + count) ; 
                return x;
            }} ) ;
    }

    public void put(final int n) {
        monitor.DoWithin( new Runnable() {
            public void run() {
                if (n + count > items.length) {

                    AbstractCondition cond;
                    switch (type) {
                        case 'n':
                        case 's':
                            cond = monitor.makeCondition( //auto-gen
                                new Assertion() {
                                    public boolean isTrue() { 
                                        return (n + count) <= items.length; 
                                    } 
                                } 
                                ) ;
                            break;
                        case 'm':
                            cond = ((MapMonitor) monitor).makeCondition( 
                                new Assertion() {
                                    public boolean isTrue() { 
                                        return (n + count) <= items.length; 
                                    } 
                                }, 
                                "(n + count) <= items.length" + "_" + n
                                ) ;
                            break;
                        case 't':
                            PredicateTag[] tags = new PredicateTag[1];
                            tags[0] = ((TagMonitor) monitor).makeTag(
                                    "(n+count)<=items.length" + "_" + n,
                                    "count", items.length - n, 
                                    PredicateTag.OperationType.LTE,
                                    new Assertion() {
                                        public boolean isTrue() {
                                            return (n + count) <= items.length;
                                        }
                                    }
                                    );
                            cond = ((TagMonitor) monitor).makeCondition(
                                    "(n+count)<=items.length" + "_" + n,
                                    new Assertion() {
                                        public boolean isTrue() {
                                            return (n + count) <= items.length;
                                        }
                                    },
                                    false,
                                    tags
                                    );
                            break;
                        default:
                            cond = ((iMonitor) monitor).makeCondition(
                                    "(n + count) <= items.length" + "_" + n,
                                    "count", items.length - n, 
                                    iMonitorCondition.OperationType.LTE, 
                                    new Assertion() {
                                        public boolean isTrue() { 
                                            return (n + count) <= items.length;
                                        } 
                                    }, 
                                    false);
                            break;
                    }
                    cond.await();
                    if (type == 's') {
                        monitor.removeCondition(cond); 
                    }
                    /*
                     *if (type == 'm') {
                     *    monitor.removeCondition(cond); 
                     *}
                     */
                }
                for (int i = 0; i < n; i++) {
                    items[putPtr++] = new Object();
                    if (putPtr == items.length) putPtr = 0;
                }
                count += n;
                Common.println("Producer " + Thread.currentThread() 
                        + " puts, #obj: " + count) ; 
            }} ) ;
    }

    public Object[] take(final int n) {
        return monitor.DoWithin( new RunnableWithResult<Object[]>() {
            public Object[] run() {
                if (n > count) {

                    AbstractCondition cond;
                    switch (type) {
                        case 'n':
                        case 's':
                            cond = monitor.makeCondition( //auto-gen
                                new Assertion() {
                                    public boolean isTrue() { 
                                        return n <= count; 
                                    } 
                                } 
                                ) ;
                            break;
                        case 'm':
                            cond = ((MapMonitor) monitor).makeCondition( 
                                new Assertion() {
                                    public boolean isTrue() { 
                                        return n <= count; 
                                    } 
                                } ,
                                "n <= count" + "_" + n
                                ) ;
                            break;
                        case 't':
                            PredicateTag[] tags = new PredicateTag[1];
                            tags[0] = ((TagMonitor) monitor).makeTag(
                                    "n<=count" + "_" + n,
                                    "count", n, 
                                    PredicateTag.OperationType.GTE,
                                    new Assertion() {
                                        public boolean isTrue() {
                                            return n <= count;
                                        }
                                    }
                                    );
                            cond = ((TagMonitor) monitor).makeCondition(
                                    "n<=count" + "_" + n,
                                    new Assertion() {
                                        public boolean isTrue() {
                                            return n <= count;
                                        }
                                    },
                                    false,
                                    tags
                                    );
                            break;

                        default:
                            cond = ((iMonitor) monitor).makeCondition(
                                    "n <= count" + "_" + n,
                                    "count", n, 
                                    iMonitorCondition.OperationType.GTE, 
                                    new Assertion() {
                                        public boolean isTrue() { 
                                            return n <= count; 
                                        } 
                                    },
                                    false);
                            break;
                    }
                    cond.await();
                    if (type == 's') {
                        monitor.removeCondition(cond); 
                    }
                    /*
                     *if (type == 'm') {
                     *    monitor.removeCondition(cond); 
                     *}
                     */
                }
                Object[] ret = new Object[n];
                for (int i = 0; i < n; i++) {
                    ret[i] = items[takePtr++];
                    if (++takePtr == items.length) takePtr = 0;
                }
                count -= n;
                Common.println("Consumer " + Thread.currentThread() 
                        + " takes " + n + " objs, remaining #obj: " + count) ; 
                return ret;
            }} ) ;
    }
    public long getNumContextSwitch() {
                return AbstractCondition.getNumContextSwitch();
    }
}
