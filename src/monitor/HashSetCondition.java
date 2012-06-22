package monitor;

import java.util.HashSet;
import java.util.concurrent.locks.Condition;

public class HashSetCondition extends AbstractCondition {

    private AssertionConditionPair self;
    private HashSet<AssertionConditionPair> setPairs;

    public HashSetCondition(Assertion assertion, Condition condition, 
            HashSet<AssertionConditionPair> setPairs_) {
        self = new AssertionConditionPair(assertion, condition);
        setPairs = setPairs_;
        setPairs.add(self);
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
}
