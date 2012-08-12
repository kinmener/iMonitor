package monitor;

import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class HashCondition extends AbstractCondition {

    private final AssertionConditionPair self;
    private final HashSet<AssertionConditionPair> setPair;
    private final String key;


    public HashCondition(Assertion assertion, ReentrantLock mutex, 
            HashSet<AssertionConditionPair> setPair, String key) {
        self = new AssertionConditionPair(assertion, mutex);
        setPair.add(self);
        this.setPair = setPair;
        this.key = key;
    }

    public AssertionConditionPair getSelf() {
       return self;
    }
    public String getKey() {
       return key;
    }

    public boolean hasWaiters() {
       return self.hasWaiters();
    }
    @Override
    public void await() {
        if(!self.assertionIsTrue()) {
            // condition.signalAll();
            for(AssertionConditionPair pair : setPair) {
                if(pair.conditionalSignal()) {
                    break;
                }
            }
            do {
                try {
                    self.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                }
            } while(!self.assertionIsTrue()); 
        }
    }
}
