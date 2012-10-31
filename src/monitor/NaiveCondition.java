package monitor;

import java.util.concurrent.locks.Condition; 

public class NaiveCondition extends AbstractCondition {
    private Assertion assertion;
    private Condition condition;

    public NaiveCondition(Condition condition_, Assertion assertion_) {
        condition = condition_;
        assertion = assertion_; 
    }

    public void await() {
        if(!assertion.isTrue()) {
            condition.signalAll();
            do {
                try {
                    numContextSwitch++;
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                }
            } while(!assertion.isTrue());
        }
    }
}

