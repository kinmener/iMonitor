package monitor;

import java.util.concurrent.locks.ReentrantLock;

public class TagMonitor extends AbstractImplicitMonitor {
    final ReentrantLock mutex = new ReentrantLock(true);
    final TagConditionManager mger = new TagConditionManager(mutex);

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

    public TagCondition makeCondition(String key, Assertion assertion, 
            boolean isGlobal, PredicateTag[] tags) {
        return mger.makeCondition(key, assertion, isGlobal, tags);
    }

    public PredicateTag makeTag(String key, String varName, int val, 
            PredicateTag.OperationType type, Assertion assertion) {
        return mger.makeTag(key, varName, val, type, assertion);
    }
    
    @Override
    public void removeCondition(AbstractCondition condition) {
    }
}
