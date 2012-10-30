
package monitor;

import java.util.Comparator;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.PriorityQueue;

//import util.Common;

public class SameConditionManager {

    private final ReentrantLock mutex;
    // global var
    private final HashMap<String, GlobalVariable> mapGlobalVar 
        = new HashMap<String, GlobalVariable>();

    // tag  collection
    private final HashMap<String, PredicateSame> mapSame
        = new HashMap<String, PredicateSame>();

    // predicate collection
    private final HashMap<String, SameCondition> mapCond
        = new HashMap<String, SameCondition>();

    // complex predicate
    private final HashSet<SameCondition> setCompCond
        = new HashSet<SameCondition>();

    // equivalence predicate 
    private final HashMap<String, HashMap<Integer, PredicateSame>> mapEPSame 
        = new HashMap<String, HashMap<Integer, PredicateSame>>();

    // ordering predicate
    private final Comparator<PredicateSame> gteCmp 
        = new PredicateSame.GTEComparator();
    private final Comparator<PredicateSame> lteCmp 
        = new PredicateSame.LTEComparator();

    private final HashMap<String, PriorityQueue<PredicateSame>> mapGTEPSame
        = new  HashMap<String, PriorityQueue<PredicateSame>>();
    private final HashMap<String, PriorityQueue<PredicateSame>> mapLTEPSame
        = new HashMap<String, PriorityQueue<PredicateSame>>();

    //HashMap<String, LinkedList<NEPredicate>> mapNEP;

    /*
     *private final iMonitorCondition head = new iMonitorCondition("__head__", 
     *        null, null, true, this);
     *private final iMonitorCondition tail = new iMonitorCondition("__tail__", 
     *        null, null, true, this);
     */

    public SameConditionManager(ReentrantLock mutex) {
        this.mutex = mutex;
        //head.addNext(tail);
    }


    public SameCondition findOneAvailable() {

        // equivalence
        SameCondition ret = null;
        for (Entry<String, HashMap<Integer, PredicateSame>> entry : 
                mapEPSame.entrySet()) {
            
            PredicateSame tag = entry.getValue().get(
                    mapGlobalVar.get(entry.getKey()).getValue());

            if (tag != null) {
                ret = tag.findOneAvailable();
                if (ret != null) {
                    return ret;
                }
            }
        }

        // lteptag
        for (Entry<String, PriorityQueue<PredicateSame>> entry : 
                mapLTEPSame.entrySet()) {
            PredicateSame tag = entry.getValue().peek(); 

            if (tag == null) {
                continue;
            }
            
            LinkedList<PredicateSame> tags = new LinkedList<PredicateSame>();
            while (tag.isTrue()) {
                ret = tag.findOneAvailable();
                if (ret != null) {
                    // add false tags back
                    for (PredicateSame trueSame : tags) {
                        entry.getValue().add(trueSame); 
                    }
                    return ret;
                } else {  // tage is ture but no predicates is true
                    tags.add(entry.getValue().poll());
                    tag = entry.getValue().peek();
                }
            } 
            // add false tags back
            for (PredicateSame trueSame : tags) {
                entry.getValue().add(trueSame); 
            }
        }

        for (Entry<String, PriorityQueue<PredicateSame>> entry : 
                mapGTEPSame.entrySet()) {
            PredicateSame tag = entry.getValue().peek(); 

            if (tag == null) {
                continue;
            }

            LinkedList<PredicateSame> tags = new LinkedList<PredicateSame>();
            while (tag.isTrue()) {
                ret = tag.findOneAvailable();
                if (ret != null) {
                    // add false tags back
                    for (PredicateSame trueSame : tags) {
                        entry.getValue().add(trueSame); 
                    }
                    return ret;
                } else {  // tage is ture but no predicates is true
                    tags.add(entry.getValue().poll());
                    tag = entry.getValue().peek();
                }
            }
            // add false tags back
            for (PredicateSame trueSame : tags) {
                entry.getValue().add(trueSame); 
            }
        }

        // complex 
        for (SameCondition cond : setCompCond) {
            if (cond.isTrue()) {
                return cond;
            }
        }

        return null;
    }


    public void signalOneAvailable() {

        // equivalence
        for (Entry<String, HashMap<Integer, PredicateSame>> entry : 
                mapEPSame.entrySet()) {
            
            PredicateSame tag = entry.getValue().get(
                    mapGlobalVar.get(entry.getKey()).getValue());

            if (tag != null) {
                if (tag.signalOneAvailable()) {
                    return;
                }
            }
        }

        // lteptag
        for (Entry<String, PriorityQueue<PredicateSame>> entry : 
                mapLTEPSame.entrySet()) {
            PredicateSame tag = entry.getValue().peek(); 

            if (tag == null) {
                continue;
            }
            
            LinkedList<PredicateSame> tags = new LinkedList<PredicateSame>();
            while (tag.isTrue()) {
                if (tag.signalOneAvailable()) {
                    // add false tags back
                    for (PredicateSame trueSame : tags) {
                        entry.getValue().add(trueSame); 
                    }
                    return;
                } else {  // tage is ture but no predicates is true
                    tags.add(entry.getValue().poll());
                    tag = entry.getValue().peek();
                }
            } 
            // add false tags back
            for (PredicateSame trueSame : tags) {
                entry.getValue().add(trueSame); 
            }
        }

        for (Entry<String, PriorityQueue<PredicateSame>> entry : 
                mapGTEPSame.entrySet()) {
            PredicateSame tag = entry.getValue().peek(); 

            if (tag == null) {
                continue;
            }

            LinkedList<PredicateSame> tags = new LinkedList<PredicateSame>();
            while (tag.isTrue()) {
                if (tag.signalOneAvailable()) {
                    // add false tags back
                    for (PredicateSame trueSame : tags) {
                        entry.getValue().add(trueSame); 
                    }
                    return;
                } else {  // tage is ture but no predicates is true
                    tags.add(entry.getValue().poll());
                    tag = entry.getValue().peek();
                }
            }
            // add false tags back
            for (PredicateSame trueSame : tags) {
                entry.getValue().add(trueSame); 
            }
        }

        // complex 
        for (SameCondition cond : setCompCond) {
            if (cond.conditionalSignal()) {
                return;
            }
        }
    }

    public void registerGlobalVariable(GlobalVariable var) {
        mapGlobalVar.put(var.name, var);
    }


    public PredicateSame makeSame(String key, String varName, int val, 
            PredicateSame.OperationType type, Assertion assertion) {
        
        PredicateSame tag = mapSame.get(key);
        if (tag == null) {
            tag = new PredicateSame(varName, val, type, assertion, this);
            mapSame.put(key, tag);
        }

        return tag;
    }

    public SameCondition makeCondition(String key, Assertion assertion, 
            boolean isGlobal, PredicateSame[] tags) {

        SameCondition cond = mapCond.get(key);
        if (cond == null) {
            cond = new SameCondition(assertion, mutex, isGlobal, tags, this);
            mapCond.put(key, cond);
        }

        return cond;
    }
    
    public void addSame(PredicateSame tag) {
        switch (tag.type) {
            case EQ:
                if (!mapEPSame.containsKey(tag.varName)) {
                    mapEPSame.put(tag.varName, 
                            new HashMap<Integer, PredicateSame>());
                }   
                mapEPSame.get(tag.varName).put(tag.val, tag);
                break;
            case NEQ:
                break;
            case GT:
            case GTE:
                if (!mapGTEPSame.containsKey(tag.varName)) {
                    mapGTEPSame.put(tag.varName, 
                            new PriorityQueue<PredicateSame>(10, gteCmp));
                }
                mapGTEPSame.get(tag.varName).add(tag);
                break;
            case LT:
            case LTE:
                if (!mapLTEPSame.containsKey(tag.varName)) {
                    mapLTEPSame.put(tag.varName, 
                            new PriorityQueue<PredicateSame>(10, lteCmp));
                }
                mapLTEPSame.get(tag.varName).add(tag);
                break;
            default:
                break;
        }
 
    }

    public void removeSame(PredicateSame tag) {
        switch (tag.type) {
            case EQ:
                mapEPSame.get(tag.varName).remove(tag.val);
                break;
            case NEQ:
                break;
            case GT:
            case GTE:
                mapGTEPSame.get(tag.varName).remove(tag);
                break;
            case LT:
            case LTE:
                mapLTEPSame.get(tag.varName).remove(tag);
                break;
            default:
                break;
        }
 
    }

    public void addComplexCondition(SameCondition cond) {
        setCompCond.add(cond);
    }
    public void removeComplexCondition(SameCondition cond) {
        setCompCond.remove(cond);
    }
}
