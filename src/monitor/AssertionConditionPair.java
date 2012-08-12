package monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AssertionConditionPair {
    final private Assertion assertion;
    final private Condition condition;
    final private ReentrantLock mutex;
    
    public AssertionConditionPair(Assertion assertion, ReentrantLock mutex) {
        this.assertion = assertion;
        this.mutex = mutex;
        this.condition = mutex.newCondition();
    }

    public boolean hasWaiters() {
       return mutex.hasWaiters(condition);
    }

    public boolean conditionalSignal() {
        if(mutex.hasWaiters(condition) && assertion.isTrue()) {
            condition.signal();
            return true;
        }
        return false;
    }
    
    public void conditionalAwait() throws InterruptedException {
        if(!assertion.isTrue()) {
            condition.await();
        }
    }
    public void await() throws InterruptedException {
        condition.await();
    }
    public void signal() {
        condition.signal();
    }
    public boolean assertionIsTrue() {
        return assertion.isTrue();
    }
}
