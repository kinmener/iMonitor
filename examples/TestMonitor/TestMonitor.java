
package examples.TestMonitor;

import java.util.HashMap;
import java.lang.management.*;

public abstract class TestMonitor {
    public abstract void access(int myId);
    
    protected void setCurrentCpuTime() {
        mapThreadCpuTime.put(Thread.currentThread().getId(), threadMXBean.getCurrentThreadCpuTime());
    }

    protected void addSyncTime() {
        syncTime += threadMXBean.getCurrentThreadCpuTime() - mapThreadCpuTime.get(Thread.currentThread().getId());
    }
    
    long syncTime = 0;
    HashMap<Long, Long> mapThreadCpuTime = new HashMap<Long, Long>();
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    public long getSyncTime() {
        return syncTime;
    }
}
