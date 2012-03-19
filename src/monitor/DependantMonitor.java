package monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DependantMonitor extends AbstractImplicitMonitor  {

    Lock mutex;
    HashSet<AssertionConditionPair> setPairs;
    HashMap<String, HashSetCondition> mapConditions;
    HashMap<String, AssertionConditionPair> mapDependantConditionPair;

    public DependantMonitor() {
        mutex = new ReentrantLock();
        setPairs = new HashSet<AssertionConditionPair>();
        mapConditions = new HashMap<String, HashSetCondition>() ;
        mapDependantConditionPair = new HashMap<String, AssertionConditionPair> ();
    }

    @Override
    protected void enter() {
        mutex.lock();
    }

    @Override
    protected void leave() {

        //condition_.signalAll();
        for(AssertionConditionPair pair : setPairs) {
            pair.conditionalSignal();
        }

        mutex.unlock();
    }

    protected void leave(String funcKey) {

        for(AssertionConditionPair pair : setPairs) {
            pair.conditionalSignal();
        }

        mutex.unlock();
    }

    @Override
    public HashSetCondition makeCondition(Assertion assertion) {
        Condition condition = mutex.newCondition();

        return new HashSetCondition(assertion, condition, setPairs);
    }

    public HashSetCondition makeCondition(Assertion assertion, String key) {

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