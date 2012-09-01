package monitor;

import java.util.Comparator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.PriorityQueue;

import util.Common;

public class ConditionManager {

    private final ReentrantLock mutex;
    // global var
    private final HashMap<String, GlobalVariable> mapGlobalVar 
        = new HashMap<String, GlobalVariable>();

    // predicate collection
    private final HashMap<String, iMonitorCondition> mapSP
        = new HashMap<String, iMonitorCondition>();

    private final HashMap<String, iMonitorCondition> mapCP
        = new HashMap<String, iMonitorCondition>();

    // equivalence predicate 
    private final HashMap<String, HashMap<Integer, iMonitorCondition>> mapEP 
        = new HashMap<String, HashMap<Integer, iMonitorCondition>>();

    // equivalence complex predicate
    private final HashMap<String, HashMap<Integer, HashSet<iMonitorCondition>>> mapECP
        = new HashMap<String, HashMap<Integer, HashSet<iMonitorCondition>>>();

    // ordering predicate
    private final Comparator<iMonitorCondition> gteCmp 
        = new iMonitorCondition.GTEComparator();
    private final Comparator<iMonitorCondition> lteCmp 
        = new iMonitorCondition.LTEComparator();

    private final HashMap<String, PriorityQueue<iMonitorCondition>> mapGTEP
        = new  HashMap<String, PriorityQueue<iMonitorCondition>>();
    private final HashMap<String, PriorityQueue<iMonitorCondition>> mapLTEP 
        = new HashMap<String, PriorityQueue<iMonitorCondition>>();

    // maintain number of threads in waiting queue
    private final HashMap<String, Integer> mapWaiters 
        = new HashMap<String, Integer>();

    //HashMap<String, LinkedList<NEPredicate>> mapNEP;

    private final iMonitorCondition head = new iMonitorCondition("__head__", 
            null, null, true, this);
    private final iMonitorCondition tail = new iMonitorCondition("__tail__", 
            null, null, true, this);

    public ConditionManager(ReentrantLock mutex) {
        this.mutex = mutex;
        head.addNext(tail);
    }

    public void signalOneAvailable() {

        // equivalence
        for (Entry<String, HashMap<Integer, iMonitorCondition>> entry : 
                mapEP.entrySet()) {
            iMonitorCondition cond = entry.getValue().get(
                    mapGlobalVar.get(entry.getKey()).getValue());

            if (cond != null) {
                if (cond.conditionalSignal()) {
                    return;
                }
            }
        }

        // equivalence complex
        for (Entry<String, HashMap<Integer, HashSet<iMonitorCondition>>> entry 
                : mapECP.entrySet()) {
            HashSet<iMonitorCondition> setCond = entry.getValue().get(
                    mapGlobalVar.get(entry.getKey()).getValue());

            if (setCond != null) {
                for (iMonitorCondition cond : setCond) {
                    if (cond.conditionalSignal()) {
                        return;
                    }

                }
            }
        }

        for (Entry<String, PriorityQueue<iMonitorCondition>> entry : 
                mapLTEP.entrySet()) {
            iMonitorCondition cond = entry.getValue().peek(); 

            if (cond != null) {
                if (cond.conditionalSignal()) {
                    return;
                }
            }
        }

        for (Entry<String, PriorityQueue<iMonitorCondition>> entry : 
                mapGTEP.entrySet()) {
            iMonitorCondition cond = entry.getValue().peek(); 

            if (cond != null) {
                if (cond.conditionalSignal()) {
                    return;
                }
            }
        }

        // complex 
        for (Entry<String, iMonitorCondition> entry : mapCP.entrySet()) {
            if (entry.getValue().conditionalSignal()) {
                return;
            }
        }
    }

    public void registerGlobalVariable(GlobalVariable var) {
        mapGlobalVar.put(var.name, var);
    }


    public iMonitorCondition makeCondition(String key, String varName, 
            int val, iMonitorCondition.OperationType type, Assertion assertion,
            boolean isGlobal) {

        if (mapSP.containsKey(key)) {
            iMonitorCondition ret = mapSP.get(key);
            if (ret.isNotUsed()) {
                ret.reactivate(); 
                mapWaiters.put(key, 1);
            } else {
                mapWaiters.put(key, mapWaiters.get(key) + 1);
            }
            return ret;
        } 

        mapWaiters.put(key, 1);

        iMonitorCondition ret = new iMonitorCondition(key,
                varName, val, type, assertion, mutex.newCondition(), 
                isGlobal, this);
        mapSP.put(key, ret);

        switch (type) {
            case EQ:
                if (!mapEP.containsKey(varName)) {
                    mapEP.put(varName, 
                            new HashMap<Integer, iMonitorCondition>());
                }   
                mapEP.get(varName).put(val, ret);
                break;
            case NEQ:
                break;
            case GT:
            case GTE:
                if (!mapGTEP.containsKey(varName)) {
                    mapGTEP.put(varName, 
                            new PriorityQueue<iMonitorCondition>(10, gteCmp));
                }
                mapGTEP.get(varName).add(ret);

                break;
            case LT:
            case LTE:
                if (!mapLTEP.containsKey(varName)) {
                    mapLTEP.put(varName, 
                            new PriorityQueue<iMonitorCondition>(10, lteCmp));
                }
                mapLTEP.get(varName).add(ret);

                break;
            case EC:
                if (!mapECP.containsKey(varName)) {
                    mapECP.put(varName, 
                            new HashMap<Integer, HashSet<iMonitorCondition>>());
                }  

                if (!mapECP.get(varName).containsKey(val)) {
                    mapECP.get(varName).put(val, 
                            new HashSet<iMonitorCondition>());    
                }

                mapECP.get(varName).get(val).add(ret);

        }
        return ret;
    }



    // the original one
    public iMonitorCondition makeCondition(
            String key, Assertion assertion, boolean isGlobal) {
        if (mapCP.containsKey(key)) {
            mapWaiters.put(key, mapWaiters.get(key) + 1);
            return mapCP.get(key); 
        }

        mapWaiters.put(key, 1);

        iMonitorCondition ret 
            = new iMonitorCondition(key, assertion, mutex.newCondition(), 
                    isGlobal, this);
        mapCP.put(key, ret);
        return ret;
    }

    public void removeCondition(String key) {
        if (mapWaiters.get(key) == 1) {
            mapWaiters.remove(key); 
            mapCP.remove(key);
        } else {
            mapWaiters.put(key, mapWaiters.get(key) - 1);
        }
    }

    public void removeCondition(String key, iMonitorCondition cond) {
        if (mapWaiters.get(key) > 1) {
            mapWaiters.put(key, mapWaiters.get(key) - 1); 
            return;
        }

        mapWaiters.remove(key);
        mapSP.remove(key);

        switch (cond.getType()) {
            case EQ:
                //mapEP.get(cond.getVarName()).remove(cond.getVal());
                tail.addPrev(cond);
                break;
            case NEQ:
                break;
            case GT:
            case GTE:
                mapGTEP.get(cond.getVarName()).remove(cond);
                break;
            case LT:
            case LTE:
                mapLTEP.get(cond.getVarName()).remove(cond);
                break;
            case EC:
                //mapECP.get(cond.getVarName()).get(cond.getVal()).remove(cond);
                tail.addPrev(cond);
                break;
        }
    }
}
