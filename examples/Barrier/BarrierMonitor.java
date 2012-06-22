
package examples.BarrierMonitor;

import java.util.HashMap;
import java.lang.management.*;

public abstract class BarrierMonitor {
    long syncTime = 1;
    HashMap<Long, Long> mapThreadCpuTime = new HashMap<Long, Long>();
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public abstract void access(int myId);
   
    public BarrierMonitor() {
        if(threadMXBean.isThreadCpuTimeEnabled()) {
            threadMXBean.setThreadCpuTimeEnabled(true);
        }
    }

    public long getSyncTime() {
        return syncTime;
    }

    protected void setCurrentCpuTime() {
        mapThreadCpuTime.put(Thread.currentThread().getId(), threadMXBean.getCurrentThreadCpuTime());
    }

    protected void addSyncTime() {
        syncTime += threadMXBean.getCurrentThreadCpuTime() - mapThreadCpuTime.get(Thread.currentThread().getId());
    }
    
}
