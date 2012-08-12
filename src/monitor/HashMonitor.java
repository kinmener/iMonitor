package monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class HashMonitor extends AbstractImplicitMonitor  {

    ReentrantLock mutex = new ReentrantLock();
    HashSet<AssertionConditionPair> setPair 
         = new HashSet<AssertionConditionPair>();
    HashMap<String, HashCondition> mapCondition 
         = new HashMap<String, HashCondition>();

    @Override
    protected void enter() {
        mutex.lock();
    }

    @Override
    protected void leave() {
        if(setPair != null) {
            //condition_.signalAll();
            for(AssertionConditionPair pair : setPair) {
                if(pair.conditionalSignal()) {
                    break;
                }
            }
        }
        mutex.unlock();
    }

    @Override
    public HashCondition makeCondition(Assertion assertion) {
        return null;
    }
    
    public HashCondition makeCondition(Assertion assertion, String key) {
        if(mapCondition.containsKey(key)) {
            return mapCondition.get(key);
        }
        
        HashCondition ret = new HashCondition(assertion, mutex, setPair, key);
        mapCondition.put(key, ret);
        return ret;
    }

    @Override
    public void removeCondition(AbstractCondition condition) {
       HashCondition cond = (HashCondition) condition;
       if (!cond.hasWaiters()) {
         setPair.remove(cond.getSelf());
         mapCondition.remove(cond.getKey());
       }
    }
}
