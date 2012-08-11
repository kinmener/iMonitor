package monitor;

import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SetMonitor extends AbstractImplicitMonitor  {

    final private ReentrantLock mutex = new ReentrantLock();
    final private HashSet<SetCondition> setCondition 
            = new HashSet<SetCondition>();

    @Override
    protected void enter() {
        mutex.lock();
    }

    @Override
    protected void leave() {
        if(setCondition != null) {
            //condition_.signalAll();
            for(SetCondition cond: setCondition) {
                if (cond.conditionalSignal()) {
                    break;
                }
            }
        }
        mutex.unlock();
    }

    @Override
    public SetCondition makeCondition(Assertion assertion) {
        return new SetCondition(mutex, assertion, setCondition);
    }

    @Override
    public void removeCondition(AbstractCondition condition) {
        setCondition.remove(condition);   
    }
}
