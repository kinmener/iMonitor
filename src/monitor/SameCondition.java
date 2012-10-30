package monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SameCondition extends AbstractCondition {
    
    private final Assertion assertion;
    private final Condition cond;
    private final boolean isGlobal;
    private final PredicateSame[] tags;
    private final SameConditionManager mger;
    private final ReentrantLock mutex;

    private int numWaiters;

    public SameCondition(Assertion assertion, ReentrantLock mutex, 
            boolean isGlobal, 
            PredicateSame[] tags, SameConditionManager mger) {
        this.assertion = assertion;
        this.mutex = mutex;
        this.cond = mutex.newCondition();
        this.isGlobal = isGlobal;
        this.tags = tags;
        this.mger = mger;
        numWaiters = 0;
        for (int i = 0; i < tags.length; i++) {
            tags[i].addCondition(this);
        }
    }

    public boolean isTrue() {
        return assertion.isTrue();
    }
    
    public boolean conditionalSignal() {
        if (isTrue()) {
            cond.signal(); 
            return true;
        }
        return false;
    }

    @Override public void await() {
        mutex.unlock();
        while (true) {
            if (isTrue()) {
                mutex.lock();
                if (isTrue()) {
                    break; 
                } else {
                    mutex.unlock();
                }
            } else {
                SameCondition sigCond = mger.findOneAvailable();
                mutex.lock();
                //mger.signalOneAvailable();
                if (isTrue()) {
                    break; 
                }
                if (sigCond != null) {
                    sigCond.conditionalSignal();
                }

                try {
                    cond.await();
                } catch(InterruptedException e) {
                }
                mutex.unlock();
            }
        }
        //if(!isTrue()) {
        //    mger.signalOneAvailable();
        //    if (numWaiters == 0) {
        //        if (tags == null) {
        //            mger.addComplexCondition(this);    
        //        } else {
        //            for (int i = 0; i < tags.length; i++) {
        //                tags[i].addCondition(this);
        //            }
        //        }
        //    }
        //    numWaiters++;
        //    do {
        //        try {
        //            cond.await();
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        } finally {
        //        }
        //    } while (!isTrue()); 
        //    // check and remove this from condition manager
        //    numWaiters--;
        //    if (numWaiters == 0) {
        //        if (tags == null) {
        //            mger.removeComplexCondition(this);    
        //        } else {
        //            for (int i = 0; i < tags.length; i++) {
        //                tags[i].removeCondition(this); 
        //            }
        //        }
        //    }
        //}

        // remove predicate modified later 
        //if (!isGlobal && numWaiters == 0) {
        //    if (type == OperationType.C) {
        //        mger.removeCondition(key);
        //    } else if (type != OperationType.EQ && type != OperationType.EC){
        //        mger.removeCondition(key, this);
        //    }
        //}
    }
}
