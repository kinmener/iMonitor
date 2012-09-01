package monitor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import util.Common;

public class iMonitorCondition extends AbstractCondition {

    public enum OperationType {
        EQ, NEQ, GT, GTE, LT, LTE, C, EC
    }

    public static class GTEComparator implements Comparator<iMonitorCondition> {
        @Override public int compare(iMonitorCondition a, iMonitorCondition b) {
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

    public static class LTEComparator implements Comparator<iMonitorCondition> {
        @Override public int compare(iMonitorCondition a, iMonitorCondition b) {
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
    private final String varName;
    private final int val;
    private final OperationType type;
    private final Condition cond;
    private final boolean isGlobal;
    private final Assertion assertion;

    private iMonitorCondition prev;
    private iMonitorCondition next;

    public iMonitorCondition(String key, Assertion assertion, Condition cond, 
            boolean isGlobal, ConditionManager mger) {
        this(key, "", 0, OperationType.C, assertion, cond, isGlobal, mger); 
    }

    public iMonitorCondition(String key, String varName, int val,
            OperationType type, Assertion assertion, Condition cond, 
            boolean isGlobal, ConditionManager mger) {

        this.key = key;
        this.varName = varName;
        this.val = val;
        this.type = type;
        this.mger = mger;
        this.cond = cond;
        this.assertion = assertion;
        this.isGlobal = isGlobal;

        prev = null;
        next = null;
    }

    public void addNext(iMonitorCondition other) {
        if (other != null) {
            other.prev = this;
            other.next = next;
        }
        next = other;
    }

    public void addPrev(iMonitorCondition other) {
        if (other != null) {
            other.prev = prev;
            other.next = this;    
        }
        prev = other;
    }

    public boolean isNotUsed() {
        if (next != null && prev != null) {
            return true;
        }
        return false;
    }

    public void reactivate() {
        Common.isBug(next == null || prev == null);
        prev.next = next;
        next.prev = prev;
        next = null;
        prev = null;
    }

    public int getVal() {
        return val;
    }

    public OperationType getType() {
        return type;
    }

    public void printKey() {
        Common.println(key);
    }

    private boolean isTrue() {
        return assertion.isTrue();
    }

    public boolean conditionalSignal() {
        if (isTrue()) {
            cond.signal(); 
            return true;
        }
        return false;
    }

    public String getVarName() {
        return varName;
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
            if (type == OperationType.C) {
                mger.removeCondition(key);
            } else {
                mger.removeCondition(key, this);
            }
        }
    }
}
