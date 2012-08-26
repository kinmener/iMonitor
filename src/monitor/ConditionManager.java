package monitor;

import java.util.Comparator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.PriorityQueue;

import util.Common;

public class ConditionManager {

    private final ReentrantLock mutex;
    // global var
    private final HashMap<String, GlobalVariable> mapGlobalVar 
        = new HashMap<String, GlobalVariable>();


    // complex predicate collection
    private final HashMap<String, ComplexCondition> mapCP 
        = new HashMap<String, ComplexCondition>();

    // simple predicate collection
    private final HashMap<String, SimpleCondition> mapSP
        = new HashMap<String, SimpleCondition>();

    // equivalence predicate 
    private final HashMap<String, HashMap<Integer, SimpleCondition>> mapEP 
        = new HashMap<String, HashMap<Integer, SimpleCondition>>();

    private final Comparator<SimpleCondition> gteCmp 
        = new SimpleCondition.GTEComparator();
    private final Comparator<SimpleCondition> lteCmp 
        = new SimpleCondition.LTEComparator();

    private final HashMap<String, PriorityQueue<SimpleCondition>> mapGTEP
        = new  HashMap<String, PriorityQueue<SimpleCondition>>();
    private final HashMap<String, PriorityQueue<SimpleCondition>> mapLTEP 
        = new HashMap<String, PriorityQueue<SimpleCondition>>();

    // maintain number of threads in waiting queue
    private final HashMap<String, Integer> mapWaiters 
        = new HashMap<String, Integer>();

    //HashMap<String, LinkedList<NEPredicate>> mapNEP;

    public ConditionManager(ReentrantLock mutex) {
        this.mutex = mutex;
    }

    public void signalOneAvailable() {

        // equivalence
        for (Entry<String, HashMap<Integer, SimpleCondition>> entry : 
                mapEP.entrySet()) {
            SimpleCondition cond = entry.getValue().get(
                    mapGlobalVar.get(entry.getKey()).getValue());

            if (cond != null) {
                if (cond.conditionalSignal()) {
                    return;
                }
            }
        }


        for (Entry<String, PriorityQueue<SimpleCondition>> entry : 
                mapLTEP.entrySet()) {
            SimpleCondition cond = entry.getValue().peek(); 

            if (cond != null) {
                if (cond.conditionalSignal()) {
                    return;
                }
            }
        }

        for (Entry<String, PriorityQueue<SimpleCondition>> entry : 
                mapGTEP.entrySet()) {
            SimpleCondition cond = entry.getValue().peek(); 

            if (cond != null) {
                if (cond.conditionalSignal()) {
                    return;
                }
            }
        }

        // complex 
        for (Entry<String, ComplexCondition> entry : mapCP.entrySet()) {
            if (entry.getValue().conditionalSignal()) {
                return;
            }
        }
    }

    public void registerGlobalVariable(GlobalVariable var) {
        mapGlobalVar.put(var.name, var);
    }

    public SimpleCondition makeSimpleCondition(String key, String varName, 
            int val, SimpleCondition.OperationType type, boolean isGlobal) {
        if (mapSP.containsKey(key)) {
            mapWaiters.put(key, mapWaiters.get(key) + 1);
            return mapSP.get(key);
        } 

        mapWaiters.put(key, 1);

        SimpleCondition ret = new SimpleCondition(key,
                mapGlobalVar.get(varName), val, type, mutex, isGlobal, this);
        mapSP.put(key, ret);

        switch (type) {
            case EQ:
                if (!mapEP.containsKey(varName)) {
                    mapEP.put(varName, 
                            new HashMap<Integer, SimpleCondition>());
                }   
                mapEP.get(varName).put(val, ret);
                break;
            case NEQ:
                break;
            case GT:
            case GTE:
                if (!mapGTEP.containsKey(varName)) {
                    mapGTEP.put(varName, 
                            new PriorityQueue<SimpleCondition>(10, gteCmp));
                }
                mapGTEP.get(varName).add(ret);

                /*
                 *Common.println("print gtep ======================");
                 *for (SimpleCondition cond : mapGTEP.get(varName)) {
                 *    cond.printKey();
                 *}
                 *Common.println("=================================");
                 */
                break;
            case LT:
            case LTE:
                if (!mapLTEP.containsKey(varName)) {
                    mapLTEP.put(varName, 
                            new PriorityQueue<SimpleCondition>(10, lteCmp));
                }
                mapLTEP.get(varName).add(ret);

                /*
                 *Common.println("print ltep ======================");
                 *for (SimpleCondition cond : mapLTEP.get(varName)) {
                 *    cond.printKey();
                 *}
                 *Common.println("=================================");
                 */
                break;
        }
        return ret;
    }

    // the original one
    public ComplexCondition makeComplexCondition(
            String key, Assertion assertion, boolean isGlobal) {
        if (mapCP.containsKey(key)) {
            mapWaiters.put(key, mapWaiters.get(key) + 1);
            return mapCP.get(key); 
        }
        
        mapWaiters.put(key, 1);

        ComplexCondition ret 
            = new ComplexCondition(key, mutex, assertion, isGlobal, this);
        mapCP.put(key, ret);
        return ret;
    }

    public void removeComplexCondition(String key) {
        if (mapWaiters.get(key) == 1) {
            mapWaiters.remove(key); 
            mapCP.remove(key);
        } else {
            mapWaiters.put(key, mapWaiters.get(key) - 1);
        }
    }

    public void removeSimpleCondition(String key) {
        if (mapWaiters.get(key) > 1) {
            mapWaiters.put(key, mapWaiters.get(key) - 1); 
            return;
        }

        mapWaiters.remove(key);
        SimpleCondition cond = mapSP.get(key);

        mapSP.remove(key);
        
        switch (cond.getType()) {
            case EQ:
                mapEP.get(cond.getVar().name).remove(cond.getVal());
                break;
            case NEQ:
                break;
            case GT:
            case GTE:
                mapGTEP.get(cond.getVar().name).remove(cond);
                break;
            case LT:
            case LTE:
                mapLTEP.get(cond.getVar().name).remove(cond);
                break;
        }
    }
}
