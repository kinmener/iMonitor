package monitor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import util.Common;

public class SimpleCondition extends AbstractCondition {

    public enum OperationType {
        EQ, NEQ, GT, GTE, LT, LTE
    }

    public static class GTEComparator implements Comparator<SimpleCondition> {
        @Override public int compare(SimpleCondition a, SimpleCondition b) {
            if (a.getVal() > b.getVal()) {
                return 1;
            } else if (a.getVal() < b.getVal()) {
                return -1;
            } else {
                if (a.getType() == OperationType.GT) {
                    return -1; 
                } else {
                    return 1;
                }
            }
        }
    }

    public static class LTEComparator implements Comparator<SimpleCondition> {
        @Override public int compare(SimpleCondition a, SimpleCondition b) {
            if (a.getVal() > b.getVal()) {
                return -1;
            } else if (a.getVal() < b.getVal()) {
                return 1;
            } else {
                if (a.getType() == OperationType.GT) {
                    return 1; 
                } else {
                    return -1;
                }
            }
        }
    }

    private final String key;
    private final ConditionManager mger;
    private final int val;
    private final GlobalVariable var;
    private final OperationType type;
    private final ReentrantLock mutex;
    private final Condition cond;
    private final boolean isGlobal;

    public SimpleCondition(String key, GlobalVariable var, int val, 
            OperationType type, ReentrantLock mutex, boolean isGlobal, 
            ConditionManager mger) {

        this.key = key;
        this.var = var;
        this.val = val;
        this.type = type;
        this.mger = mger;
        this.mutex = mutex;
        this.isGlobal = isGlobal;
        cond = mutex.newCondition();
    }

    public int getVal() {
        return val;
    }

    public GlobalVariable getVar() {
        return var;
    }

    public OperationType getType() {
        return type;
    }

    public void printKey() {
        Common.println(key);
    }

    private boolean isTrue() {
        switch (type) {
            case EQ:
                return var.getValue() == val;
            case NEQ:
                return var.getValue() != val;
            case GT:
                return var.getValue() > val;
            case GTE:
                return var.getValue() >= val;
            case LT:
                return var.getValue() < val;
            case LTE:
                return var.getValue() <= val;
        }
        return false;
    }

    public boolean conditionalSignal() {
        if (isTrue()) {
            cond.signal(); 
            return true;
        }
        return false;
    }

    @Override public void await() {
        if(!isTrue()) {
            mger.signalOneAvailable();

            do {
                try {
                    cond.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                }
            } while (!isTrue()); 
        }
        // check and remove this from condition manager
        if (!isGlobal) {
            mger.removeSimpleCondition(key);
        }
    }
}
