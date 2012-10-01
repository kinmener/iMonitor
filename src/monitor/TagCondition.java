package monitor;

import java.util.concurrent.locks.Condition;
import java.util.HashSet;

public class TagCondition extends AbstractCondition {
    
    private final Assertion assertion;
    private final Condition cond;
    private final boolean isGlobal;
    private final PredicateTag[] tags;
    private final TagConditionManager mger;

    private int numWaiters;

    public TagCondition(Assertion assertion, Condition cond, boolean isGlobal, 
            PredicateTag[] tags, TagConditionManager mger) {
        this.assertion = assertion;
        this.cond = cond;
        this.isGlobal = isGlobal;
        this.tags = tags;
        this.mger = mger;
        numWaiters = 0;
    }

    private boolean isTrue() {
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
        if(!isTrue()) {
            mger.signalOneAvailable();
            if (numWaiters == 0) {
                if (tags == null) {
                    mger.addComplexCondition(this);    
                } else {
                    for (int i = 0; i < tags.length; i++) {
                        tags[i].addCondition(this);
                    }
                }
            }
            numWaiters++;
            do {
                try {
                    cond.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                }
            } while (!isTrue()); 
            // check and remove this from condition manager
            numWaiters--;
            if (numWaiters == 0) {
                if (tags == null) {
                    mger.removeComplexCondition(this);    
                } else {
                    for (int i = 0; i < tags.length; i++) {
                        tags[i].removeCondition(this); 
                    }
                }
            }
        }

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
