
package monitor;

import java.util.Comparator;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.PriorityQueue;

//import util.Common;

public class TagConditionManager {

    private final ReentrantLock mutex;
    // global var
    private final HashMap<String, GlobalVariable> mapGlobalVar 
        = new HashMap<String, GlobalVariable>();

    // tag  collection
    private final HashMap<String, PredicateTag> mapTag
        = new HashMap<String, PredicateTag>();

    // predicate collection
    private final HashMap<String, TagCondition> mapCond
        = new HashMap<String, TagCondition>();

    // complex predicate
    private final HashSet<TagCondition> setCompCond
        = new HashSet<TagCondition>();

    // equivalence predicate 
    private final HashMap<String, HashMap<Integer, PredicateTag>> mapEPTag 
        = new HashMap<String, HashMap<Integer, PredicateTag>>();

    // ordering predicate
    private final Comparator<PredicateTag> gteCmp 
        = new PredicateTag.GTEComparator();
    private final Comparator<PredicateTag> lteCmp 
        = new PredicateTag.LTEComparator();

    private final HashMap<String, PriorityQueue<PredicateTag>> mapGTEPTag
        = new  HashMap<String, PriorityQueue<PredicateTag>>();
    private final HashMap<String, PriorityQueue<PredicateTag>> mapLTEPTag
        = new HashMap<String, PriorityQueue<PredicateTag>>();

    //HashMap<String, LinkedList<NEPredicate>> mapNEP;

    /*
     *private final iMonitorCondition head = new iMonitorCondition("__head__", 
     *        null, null, true, this);
     *private final iMonitorCondition tail = new iMonitorCondition("__tail__", 
     *        null, null, true, this);
     */

    public TagConditionManager(ReentrantLock mutex) {
        this.mutex = mutex;
        //head.addNext(tail);
    }


    public void signalOneAvailable() {

        // equivalence
        for (Entry<String, HashMap<Integer, PredicateTag>> entry : 
                mapEPTag.entrySet()) {
            
            PredicateTag tag = entry.getValue().get(
                    mapGlobalVar.get(entry.getKey()).getValue());

            if (tag != null) {
                if (tag.signalOneAvailable()) {
                    return;
                }
            }
        }

        // lteptag
        for (Entry<String, PriorityQueue<PredicateTag>> entry : 
                mapLTEPTag.entrySet()) {
            PredicateTag tag = entry.getValue().peek(); 

            if (tag == null) {
                continue;
            }
            
            LinkedList<PredicateTag> tags = new LinkedList<PredicateTag>();
            while (tag.isTrue()) {
                if (tag.signalOneAvailable()) {
                    // add false tags back
                    for (PredicateTag trueTag : tags) {
                        entry.getValue().add(trueTag); 
                    }
                    return;
                } else {  // tage is ture but no predicates is true
                    tags.add(entry.getValue().poll());
                    tag = entry.getValue().peek();
                }
            } 
            // add false tags back
            for (PredicateTag trueTag : tags) {
                entry.getValue().add(trueTag); 
            }
        }

        for (Entry<String, PriorityQueue<PredicateTag>> entry : 
                mapGTEPTag.entrySet()) {
            PredicateTag tag = entry.getValue().peek(); 

            if (tag == null) {
                continue;
            }

            LinkedList<PredicateTag> tags = new LinkedList<PredicateTag>();
            while (tag.isTrue()) {
                if (tag.signalOneAvailable()) {
                    // add false tags back
                    for (PredicateTag trueTag : tags) {
                        entry.getValue().add(trueTag); 
                    }
                    return;
                } else {  // tage is ture but no predicates is true
                    tags.add(entry.getValue().poll());
                    tag = entry.getValue().peek();
                }
                return;
            }
        }

        // complex 
        for (TagCondition cond : setCompCond) {
            if (cond.conditionalSignal()) {
                return;
            }
        }
    }

    public void registerGlobalVariable(GlobalVariable var) {
        mapGlobalVar.put(var.name, var);
    }


    public PredicateTag makeTag(String key, String varName, int val, 
            PredicateTag.OperationType type, Assertion assertion) {
        
        PredicateTag tag = mapTag.get(key);
        if (tag == null) {
            tag = new PredicateTag(varName, val, type, assertion, this);
            mapTag.put(key, tag);
        }

        return tag;
    }

    public TagCondition makeCondition(String key, Assertion assertion, 
            boolean isGlobal, PredicateTag[] tags) {

        TagCondition cond = mapCond.get(key);
        if (cond == null) {
            cond = new TagCondition(assertion, mutex.newCondition(),     
                    isGlobal, tags, this);
            mapCond.put(key, cond);
        }

        return cond;
    }
    
    public void addTag(PredicateTag tag) {
        switch (tag.type) {
            case EQ:
                if (!mapEPTag.containsKey(tag.varName)) {
                    mapEPTag.put(tag.varName, 
                            new HashMap<Integer, PredicateTag>());
                }   
                mapEPTag.get(tag.varName).put(tag.val, tag);
                break;
            case NEQ:
                break;
            case GT:
            case GTE:
                if (!mapGTEPTag.containsKey(tag.varName)) {
                    mapGTEPTag.put(tag.varName, 
                            new PriorityQueue<PredicateTag>(10, gteCmp));
                }
                mapGTEPTag.get(tag.varName).add(tag);
                break;
            case LT:
            case LTE:
                if (!mapLTEPTag.containsKey(tag.varName)) {
                    mapLTEPTag.put(tag.varName, 
                            new PriorityQueue<PredicateTag>(10, lteCmp));
                }
                mapLTEPTag.get(tag.varName).add(tag);
                break;
            default:
                break;
        }
 
    }

    public void removeTag(PredicateTag tag) {
        switch (tag.type) {
            case EQ:
                mapEPTag.get(tag.varName).remove(tag.val);
                break;
            case NEQ:
                break;
            case GT:
            case GTE:
                mapGTEPTag.get(tag.varName).remove(tag);
                break;
            case LT:
            case LTE:
                mapLTEPTag.get(tag.varName).remove(tag);
                break;
            default:
                break;
        }
 
    }

    public void addComplexCondition(TagCondition cond) {
        setCompCond.add(cond);
    }
    public void removeComplexCondition(TagCondition cond) {
        setCompCond.remove(cond);
    }
}
