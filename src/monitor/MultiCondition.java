package monitor;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;

public class MultiCondition extends AbstractCondition {

    private Condition condition;
    private Assertion assertion;
    private HashMap<Assertion, Condition> mapConditions;
  
    public MultiCondition(Condition condition_, Assertion assertion_,
            HashMap<Assertion, Condition> mapConditions_) {
        condition = condition_;
        assertion = assertion_;
        mapConditions = mapConditions_;
    }
    
    public Assertion getAssertion() {
        return assertion;
    }

    @Override
    public void await() {
        while(!assertion.isTrue()) {
            try {
//                condition.signalAll();
                for(Entry<Assertion, Condition> e: mapConditions.entrySet()) {
                    if(e.getKey().isTrue()) {
                        e.getValue().signal();
                    }
                }
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
            }
        }
    }
}
