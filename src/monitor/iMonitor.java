package monitor;

import java.util.concurrent.locks.ReentrantLock;

public class iMonitor extends AbstractImplicitMonitor {
    final ReentrantLock mutex = new ReentrantLock(true);
    final ConditionManager mger = new ConditionManager(mutex);

    public void registerGlobalVariable(GlobalVariable var) {
        mger.registerGlobalVariable(var);
    }

    @Override
    protected void enter() {
        mutex.lock();
    }

    @Override
    protected void leave() {
        mger.signalOneAvailable();
        mutex.unlock();
    }
    
    @Override
    public AbstractCondition makeCondition(Assertion assertion) {
        return null;
    }

    public iMonitorCondition makeCondition(String key, Assertion assertion,
            boolean isGlobal) {
        return mger.makeCondition(key, assertion, isGlobal);
    }

        
    public iMonitorCondition makeCondition(String key, String varName, int val, 
            iMonitorCondition.OperationType type, Assertion assertion, 
            boolean isGlobal) {
        return mger.makeCondition(key, varName, val, type, assertion, isGlobal);
    }

    @Override
    public void removeCondition(AbstractCondition condition) {
    }
}
