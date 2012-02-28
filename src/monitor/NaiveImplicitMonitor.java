package monitor;

import java.util.concurrent.locks.Condition; 

public class NaiveImplicitMonitor extends AbstractImplicitMonitor {
    private Condition condition = null;
    
    protected final void Enter() {
        lock.lock();
        occupant = Thread.currentThread();
    }
    protected final void Leave() {
        if(condition != null) condition.signal();
        occupant = null;
        lock.unlock();
    }

    public NavieCondition makeCondition(Assertion assertion) {
        if(condition == null) condition = lock.newCondition();
        return new NavieCondition(condition, assertion);
    }
}
