
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
    protected Object[] items;
    public abstract void put (Object x)  throws InterruptedException ;
    public abstract Object take ()  throws InterruptedException ;
    public abstract void put(final int n) throws InterruptedException;
    public abstract Object[] take(final int n) throws InterruptedException;
    public int size() {
        return items.length;
    }
}
