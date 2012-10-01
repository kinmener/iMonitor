package monitor;

import java.util.Comparator;
import java.util.HashSet;

public class PredicateTag {
    public enum OperationType {
        EQ, NEQ, GT, GTE, LT, LTE, C, EC, NONE
    }

    public static class GTEComparator implements Comparator<PredicateTag> {
        @Override public int compare(PredicateTag a, PredicateTag b) {
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

    public static class LTEComparator implements Comparator<PredicateTag> {
        @Override public int compare(PredicateTag a, PredicateTag b) {
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
    
    private final TagConditionManager mger;
    private final HashSet<TagCondition> setCond;
    private final Assertion assertion;



    public PredicateTag(String varName, int val, OperationType type, 
            Assertion assertion, TagConditionManager mger) {
        this.varName = varName;
        this.type = type;
        this.val = val;
        this.mger = mger;
        this.assertion = assertion;

        setCond = new HashSet<TagCondition>();
    }

    public int getVal() {
        return val;
    }
   
    public boolean isTrue() {
        return assertion.isTrue();
    }

    public void addCondition(TagCondition cond) {
        if (setCond.size() == 0) {
            mger.addTag(this);
        }
        setCond.add(cond);
    }

    public void removeCondition(TagCondition cond) {
        setCond.remove(cond);
        if (setCond.size() == 0) {
            mger.removeTag(this); 
        }
    }

    public boolean signalOneAvailable() {
        for (TagCondition cond : setCond) {
            if (cond.conditionalSignal()) {
                return true;
            }
        }
        return false;
    }
}
