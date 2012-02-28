package monitor;

import java.util.concurrent.locks.Condition; 

public class NavieCondition extends AbstractCondition {
    private Assertion assertion;
    private Condition condition;

    public NavieCondition(Condition condition_, Assertion assertion_) {
        condition = condition_;
        assertion = assertion_; 
    }

    public void await() {
        while(!assertion.isTrue()) {
            try {
                condition.signalAll();
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
            }
        }
    }
}

