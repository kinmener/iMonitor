package monitor;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ListMonitor extends AbstractImplicitMonitor  {

    Lock mutex = new ReentrantLock();
    HashMap<Assertion, Condition> mapConditions;

    @Override
    protected void enter() {
        mutex.lock();
    }

    @Override
    protected void leave() {
        if(mapConditions != null) {
            //condition_.signalAll();
            for(Entry<Assertion, Condition> e: mapConditions.entrySet()) {
                if(e.getKey().isTrue()) {
                    e.getValue().signal();
                }
            }
        }
        mutex.unlock();
    }

    @Override
    public ListCondition makeCondition(Assertion assertion) {
        Condition condition = mutex.newCondition();
        if(mapConditions == null) {
            mapConditions = new HashMap<Assertion, Condition>();
        }
        mapConditions.put(assertion, condition);
        return new ListCondition(condition, assertion, mapConditions);
    }
    
    @Override
    public void removeCondition(AbstractCondition condition) {
        assert mapConditions.remove(((ListCondition)condition).getAssertion()) != null;
    }
}
