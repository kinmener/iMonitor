package monitor;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

public class MapCondition extends AbstractCondition {

    final private AssertionConditionPair self;
    final private HashMap<String, MapCondition> mapCondition;
    final String key;

    public MapCondition(String key, ReentrantLock mutex, Assertion assertion, 
            HashMap<String, MapCondition> mapCondition) {
        self = new AssertionConditionPair(assertion, mutex);
        this.mapCondition = mapCondition;
        this.key = key;

        mapCondition.put(key, this);
    }

    public String getKey() {
        return key;
    }
    public boolean conditionalSignal() {
        return self.conditionalSignal();
    }

    @Override
    public void await() {
        if(!self.assertionIsTrue()) {
            // condition.signalAll();
            for(Entry<String, MapCondition> entry : mapCondition.entrySet()) {
                if (entry.getValue().conditionalSignal()) {
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
