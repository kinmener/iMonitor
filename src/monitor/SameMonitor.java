package monitor;

import java.util.concurrent.locks.ReentrantLock;

public class SameMonitor extends AbstractImplicitMonitor {
    final ReentrantLock mutex = new ReentrantLock(true);
    final SameConditionManager mger = new SameConditionManager(mutex);

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

    public SameCondition makeCondition(String key, Assertion assertion, 
            boolean isGlobal, PredicateSame[] tags) {
        return mger.makeCondition(key, assertion, isGlobal, tags);
    }

    public PredicateSame makeSame(String key, String varName, int val, 
            PredicateSame.OperationType type, Assertion assertion) {
        return mger.makeSame(key, varName, val, type, assertion);
    }
    
    @Override
    public void removeCondition(AbstractCondition condition) {
    }
}
