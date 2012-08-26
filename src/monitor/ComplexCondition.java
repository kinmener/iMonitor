package monitor;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ComplexCondition extends AbstractCondition {

    private final ConditionManager mger;

    private final String key;
    private final boolean isGlobal;
    private final Assertion assertion;
    private final Condition condition;

    public ComplexCondition(String key, ReentrantLock mutex, 
            Assertion assertion, boolean isGlobal, ConditionManager mger) {
        this.assertion = assertion;
        condition = mutex.newCondition();
        this.key = key;
        this.mger = mger;
        this.isGlobal = isGlobal;
    }

    public String getKey() {
        return key;
    }
    public boolean conditionalSignal() {
        if (assertion.isTrue()) {
            condition.signal();
            return true;
        }
        return false;
    }

    @Override public void await() {
        if(!assertion.isTrue()) {
            // condition.signalAll();
            mger.signalOneAvailable();

            do {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                }
            } while(!assertion.isTrue()); 
        }
        // check and remove this from condition manager
        if (!isGlobal) {
            mger.removeComplexCondition(key);
        }
    }
}
