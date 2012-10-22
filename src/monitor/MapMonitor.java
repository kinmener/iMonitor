package monitor;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

public class MapMonitor extends AbstractImplicitMonitor  {

    final ReentrantLock mutex = new ReentrantLock(true);
    final HashMap<String, MapCondition> mapCondition
            = new HashMap<String, MapCondition>();

    @Override
    protected void enter() {
        mutex.lock();
    }

    @Override
    protected void leave() {
        if(mapCondition != null) {
            //condition_.signalAll();
            for(Entry<String, MapCondition> entry 
                    : mapCondition.entrySet()) {
                if (entry.getValue().conditionalSignal()) {
                    break;
                }
            }
        }
        mutex.unlock();
    }

    @Override
    public MapCondition makeCondition(Assertion assertion) {
        //Condition condition = mutex.newCondition();
        return null;
    }
    
    public MapCondition makeCondition(Assertion assertion, String key) {
        if(mapCondition.containsKey(key)) {
            return mapCondition.get(key);
        }
        
        MapCondition ret = new MapCondition(key, mutex, assertion, mapCondition);
        return ret;
    }
    
    

    @Override
    public void removeCondition(AbstractCondition condition) {
        mapCondition.remove(((MapCondition) condition).getKey());   
    }
}
