package monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HashMonitor extends AbstractImplicitMonitor  {

    Lock mutex = new ReentrantLock();
    HashSet<AssertionConditionPair> setPairs;
    HashMap<String, HashSetCondition> mapConditions;

    @Override
    protected void enter() {
        mutex.lock();
    }

    @Override
    protected void leave() {
        if(setPairs != null) {
            //condition_.signalAll();
            for(AssertionConditionPair pair : setPairs) {
                pair.conditionalSignal();
            }
        }
        mutex.unlock();
    }

    @Override
    public HashSetCondition makeCondition(Assertion assertion) {
        Condition condition = mutex.newCondition();
        if(setPairs == null) {
            setPairs = new HashSet<AssertionConditionPair>();
        }
        return new HashSetCondition(assertion, condition, setPairs);
    }
    
    public HashSetCondition makeCondition(Assertion assertion, String key) {
        if(mapConditions == null) {
            mapConditions = new HashMap<String, HashSetCondition>();
        }
        if(mapConditions.containsKey(key)) {
            return mapConditions.get(key);
        }
        
        HashSetCondition ret = makeCondition(assertion);
        mapConditions.put(key, ret);
        return ret;
    }
    
    

    @Override
    public void removeCondition(AbstractCondition condition) {
        setPairs.remove(condition);   
    }
}