package monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DependantMonitor extends AbstractImplicitMonitor  {

    Lock mutex;
    HashSet<AssertionConditionPair> setPairs;
    HashMap<String, DependantCondition> mapConditions;
    HashMap<String, AssertionConditionPair> mapDependantConditionPair;

    public DependantMonitor() {
        mutex = new ReentrantLock();
        setPairs = new HashSet<AssertionConditionPair>();
        mapConditions = new HashMap<String, DependantCondition>() ;
        mapDependantConditionPair = new HashMap<String, AssertionConditionPair> ();
    }

    @Override
    protected void enter() {
        mutex.lock();
    }

    @Override
    protected void leave() {

        //condition_.signalAll();
        for(AssertionConditionPair pair : setPairs) {
            pair.conditionalSignal();
        }

        mutex.unlock();
    }

    protected void leave(String funcKey) {
        if(mapDependantConditionPair.containsKey(funcKey) && 
                mapDependantConditionPair.get(funcKey).conditionalSignal()) {
            mutex.unlock();
            return;
        }

        for(AssertionConditionPair pair : setPairs) {
            if(pair.conditionalSignal()) {
                mapDependantConditionPair.put(funcKey, pair);
                break;
            }
        }
        mutex.unlock();
    }
    public void DoWithin(Runnable runnable, String funcKey) {
        enter();
        try {
            runnable.run();  
        } finally {
            leave(funcKey);
        }
    }
    public<T> T DoWithin( RunnableWithResult<T> runnable, String funcKey) {
        enter() ;
        try {   
            return runnable.run() ; 
        }
        finally {
            leave(funcKey) ; 
        }
    }
    public<T extends Exception> void DoWithin( RunnableWithException<T> runnable, String funcKey ) throws T {
      enter() ;
      try {
        runnable.run() ; }
      finally {
       leave(funcKey) ; }
    }
    public<T1, T2 extends Exception> T1 DoWithin( RunnableWithResultAndException<T1, T2> runnable, String funcKey ) throws T2 {
      enter() ;
      try {
        return runnable.run() ; }
      finally {
       leave(funcKey) ; }
    }

    @Override
    public DependantCondition makeCondition(Assertion assertion) {
        Condition condition = mutex.newCondition();

        return new DependantCondition(assertion, condition, setPairs);
    }

    public DependantCondition makeCondition(Assertion assertion, String key) {

        if(mapConditions.containsKey(key)) {
            return mapConditions.get(key);
        }

        DependantCondition ret = makeCondition(assertion);
        mapConditions.put(key, ret);
        return ret;
    }

    @Override
    public void removeCondition(AbstractCondition condition) {
        setPairs.remove(condition);   
    }
}
