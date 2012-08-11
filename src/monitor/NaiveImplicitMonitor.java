package monitor;

import java.util.concurrent.locks.Condition; 
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NaiveImplicitMonitor extends AbstractImplicitMonitor {
    private Lock mutex = new ReentrantLock();
    private Condition condition = null;
    
    protected final void enter() {
        mutex.lock();
    }
    protected final void leave() {
        if(condition != null) condition.signalAll();
        mutex.unlock();
    }

    public NaiveCondition makeCondition(Assertion assertion) {
        if(condition == null) condition = mutex.newCondition();
        return new NaiveCondition(condition, assertion);
    }
    public void removeCondition(AbstractCondition abstractCondition) {
    }
    
}
