package monitor;

import java.util.concurrent.locks.Condition;

public class AssertionConditionPair {
    private Assertion assertion;
    private Condition condition;
    
    public AssertionConditionPair(Assertion assertion_, Condition condition_) {
        assertion = assertion_;
        condition = condition_;
    }
    
    public boolean conditionalSignal() {
        if(assertion.isTrue()) {
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
