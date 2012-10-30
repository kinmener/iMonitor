package monitor;

import java.util.Comparator;
import java.util.HashSet;

public class PredicateSame {
    public enum OperationType {
        EQ, NEQ, GT, GTE, LT, LTE, C, EC, NONE
    }

    public static class GTEComparator implements Comparator<PredicateSame> {
        @Override public int compare(PredicateSame a, PredicateSame b) {
            if (a.val > b.val) {
                return 1;
            } else if (a.val < b.val) {
                return -1;
            } else {
                if (a.type == OperationType.GT) {
                    return -1; 
                } else {
                    return 1;
                }
            }
        }
    }

    public static class LTEComparator implements Comparator<PredicateSame> {
        @Override public int compare(PredicateSame a, PredicateSame b) {
            if (a.getVal() > b.getVal()) {
                return -1;
            } else if (a.getVal() < b.getVal()) {
                return 1;
            } else {
                if (a.type == OperationType.GT) {
                    return 1; 
                } else {
                    return -1;
                }
            }
        }
    }
    
    //private final String key;
    public final String varName;
    public final int val;
    public final OperationType type;
    
    private final SameConditionManager mger;
    private final HashSet<SameCondition> setCond;
    private final Assertion assertion;



    public PredicateSame(String varName, int val, OperationType type, 
            Assertion assertion, SameConditionManager mger) {
        this.varName = varName;
        this.type = type;
        this.val = val;
        this.mger = mger;
        this.assertion = assertion;

        setCond = new HashSet<SameCondition>();
    }

    public int getVal() {
        return val;
    }
   
    public boolean isTrue() {
        return assertion.isTrue();
    }

    public void addCondition(SameCondition cond) {
        if (setCond.size() == 0) {
            mger.addSame(this);
        }
        setCond.add(cond);
    }

    public void removeCondition(SameCondition cond) {
        setCond.remove(cond);
        if (setCond.size() == 0) {
            mger.removeSame(this); 
        }
    }

    public SameCondition findOneAvailable() {
        for (SameCondition cond : setCond) {
            if (cond.isTrue()) {
                return cond;
            }
        }
        return null;
    }

    public boolean signalOneAvailable() {
        for (SameCondition cond : setCond) {
            if (cond.conditionalSignal()) {
                return true;
            }
        }
        return false;
    }
}
