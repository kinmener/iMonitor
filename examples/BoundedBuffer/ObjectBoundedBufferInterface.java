
package examples.BoundedBuffer;
/**
 * <p>Title: Monitor package and examples</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Memorial University of Newfoundland</p>
 * @author Theodore S. Norvell
 * @version 1.0
 */

public interface ObjectBoundedBufferInterface {
    public void put (Object x)  throws InterruptedException ;
    public Object take ()  throws InterruptedException ;
}
