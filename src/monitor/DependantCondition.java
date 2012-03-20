package monitor;

import java.util.HashSet;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;

public class DependantCondition extends AbstractCondition {

    private AssertionConditionPair self;
    private HashSet<AssertionConditionPair> setPairs;
    HashMap<String, AssertionConditionPair> mapDependantConditionPair;

    public DependantCondition(Assertion assertion, Condition condition, 
            HashSet<AssertionConditionPair> setPairs_) {
        self = new AssertionConditionPair(assertion, condition);
        setPairs = setPairs_;
        setPairs.add(self);
        mapDependantConditionPair = new HashMap<String, AssertionConditionPair> ();
    }

 

    @Override
    public void await() {
        while (!self.assertionIsTrue()) {
            //                condition.signalAll();
            if(!mapDependantConditionPair.containsKey(self.getGlobalState()) || 
                    !mapDependantConditionPair.get(self.getGlobalState()).conditionalSignal()) {

                for(AssertionConditionPair pair : setPairs) {
                    if(pair.conditionalSignal()) {
                        mapDependantConditionPair.put(self.getGlobalState(), pair);
                        break;
                    }
                }
            }
            try {
                self.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
            }
        }
    }
}
