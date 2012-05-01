package pdsl;

import java.util.HashSet;

import locks.Condition;
import locks.Lock;
import locks.ReentrantLock;

public class NConditionLock {
    private Lock innerLock;
    private Condition innerCondition;
    private HashSet<Condition> setConditions;

    public NConditionLock () {
        innerLock = new ReentrantLock();
        setConditions = new HashSet<Condition>();
    }

    public void lock() {
        innerLock.lock();
    }

    public void unlock() {
        for(Condition cond : setConditions) {
            cond.signal();
        }
        innerLock.unlock();
    }


    public PDSLCondition newCondition() {
        Condition cond = innerLock.newCondition();
        setConditions.add(cond);        
        PDSLCondition ret = new PDSLCondition(cond, setConditions);
        return ret;
    }
}
