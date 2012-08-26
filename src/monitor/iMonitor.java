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

    public ComplexCondition makeCondition(Assertion assertion, String key, 
            boolean isGlobal) {
        return mger.makeComplexCondition(key, assertion, isGlobal);
    }

    public SimpleCondition makeCondition(String varName, int val, 
            SimpleCondition.OperationType type, boolean isGlobal) {
        String key = varName;

        switch (type) {
            case EQ:
                key += "=";
                break;
            case NEQ:
                key += "!=";
                break;
            case GT:
                key += ">";
                break;
            case GTE:
                key += ">=";
                break;
            case LT:
                key += "<";
                break;
            case LTE:
                key += "<=";
                break;
        }
        key += val;
        return mger.makeSimpleCondition(key, varName, val, type, isGlobal);
    }

    @Override
    public void removeCondition(AbstractCondition condition) {
    }

}
