
package examples.BoundedBuffer;
/**
 * <p>Title: Monitor package and examples</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Memorial University of Newfoundland</p>
 * @author Theodore S. Norvell
 * @version 1.0
 */

import java.util.HashMap;
import java.lang.management.*;

public abstract class ObjectBoundedBuffer {
    public abstract void put (Object x)  throws InterruptedException ;
    public abstract Object take ()  throws InterruptedException ;
//    public long getSyncTime() {
//        return syncTime;
//    }

//    protected void setCurrentCpuTime() {
//        mapThreadCpuTime.put(Thread.currentThread().getId(), threadMXBean.getCurrentThreadCpuTime());
//    }
//
//    protected void addSyncTime() {
//        syncTime += threadMXBean.getCurrentThreadCpuTime() - mapThreadCpuTime.get(Thread.currentThread().getId());
//    }
  
//    long syncTime = 0;
//    HashMap<Long, Long> mapThreadCpuTime = new HashMap<Long, Long>();
//    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
}
