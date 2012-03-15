package monitor;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;

public class DependantCondition extends AbstractCondition {

    private Condition condition;
    private Assertion assertion;
    private HashMap<Assertion, Condition> mapConditions;

    public DependantCondition(Condition condition_, Assertion assertion_,
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
        if(!assertion.isTrue()) {
            //                condition.signalAll();
            for(Entry<Assertion, Condition> e: mapConditions.entrySet()) {
                if(e.getKey().isTrue()) {
                    e.getValue().signal();
                }
            }
            do {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                }
            } while(!assertion.isTrue()); 
        }
    }
}
