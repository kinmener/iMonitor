package monitor;

import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HashSetMonitor extends AbstractImplicitMonitor  {

    Lock mutex = new ReentrantLock();
    HashSet<AssertionConditionPair> setPairs;

    @Override
    protected void enter() {
        mutex.lock();
    }

    @Override
    protected void leave() {
        if(setPairs != null) {
            //condition_.signalAll();
            for(AssertionConditionPair pair : setPairs) {
                if (pair.conditionalSignal()) {
                    break;
                }
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

    @Override
    public void removeCondition(AbstractCondition condition) {
        setPairs.remove(condition);   
    }
}
