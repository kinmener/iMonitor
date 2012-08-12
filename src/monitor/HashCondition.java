package monitor;

import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class HashCondition extends AbstractCondition {

    private AssertionConditionPair self;
    private HashSet<AssertionConditionPair> setPairs;
    private final String key;

    public HashCondition(Assertion assertion, ReentrantLock mutex, 
            HashSet<AssertionConditionPair> setPairs_, String key) {
        self = new AssertionConditionPair(assertion, mutex);
        setPairs = setPairs_;
        setPairs.add(self);
        this.key = key;
    }


    public String getKey() {
        return key;
    }

    @Override
    public void await() {
        if(!self.assertionIsTrue()) {
            //                condition.signalAll();
            for(AssertionConditionPair pair : setPairs) {
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

    public void remove() {
        setPairs.remove(self);
    }
}
