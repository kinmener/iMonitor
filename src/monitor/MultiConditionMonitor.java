package monitor;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;

public class MultiConditionMonitor extends AbstractImplicitMonitor  {

    HashMap<Assertion, Condition> mapConditions;

    @Override
    protected void Enter() {
        lock.lock();
        occupant = Thread.currentThread();
    }

    @Override
    protected void Leave() {
        if(mapConditions != null) {
            //condition_.signalAll();
            for(Entry<Assertion, Condition> e: mapConditions.entrySet()) {
                e.getValue().signal();
            }
        }
        occupant = null;
        lock.unlock();
    }

    @Override
    public MultiCondition makeCondition(Assertion assertion) {
        Condition condition = lock.newCondition();
        if(mapConditions == null) {
            mapConditions = new HashMap<Assertion, Condition>();
        }
        mapConditions.put(assertion, condition);
        return new MultiCondition(condition, assertion, mapConditions);
    }
}
