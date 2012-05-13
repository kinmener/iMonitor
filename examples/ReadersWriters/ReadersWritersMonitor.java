
package examples.ReadersWriters;

import java.util.HashMap;
import java.lang.management.*;

public abstract class ReadersWritersMonitor {
    private long syncTime = 0;
    private HashMap<Long, Long> mapThreadCpuTime = new HashMap<Long, Long>();
    private ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public ReadersWritersMonitor() {
        if(!threadMXBean.isThreadCpuTimeEnabled()) {
            threadMXBean.setThreadCpuTimeEnabled(true);
        }
    }

    public abstract void startRead();
    public abstract void endRead();
    public abstract void startWrite();
    public abstract void endWrite();
    
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
