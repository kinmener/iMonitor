
package examples.BoundedBuffer;
import monitor.*;	//auto-gen iMonitor

public class HashSetBoundedBuffer extends ObjectBoundedBuffer {
   private AbstractImplicitMonitor monitor = new HashSetMonitor(); //auto-gen
   private AbstractCondition cond_1 = monitor.makeCondition( //auto-gen
         new  Assertion() {
            public boolean isTrue() { return count > 0; } 
         } ) ;
   private AbstractCondition cond_0 = monitor.makeCondition( //auto-gen
         new  Assertion() {
            public boolean isTrue() { return count < items.length; } 
         } ) ;

   private final Object[] items;
   private int putptr, takeptr, count;

   public HashSetBoundedBuffer(int n) {
      items = new Object[n];
      putptr = takeptr = count = 0;
   }

   public void put(final Object x) {
      monitor.DoWithin( new Runnable() {
         public void run() {
            setCurrentCpuTime();
            cond_0.await();	//auto-gen iMonitor
            addSyncTime();
            items[putptr] = x; 
            if (++putptr == items.length) putptr = 0;
            ++count;
            //System.out.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 
         }} ) ;
   }

   public Object take() {
      return monitor.DoWithin( new RunnableWithResult<Object>() {
         public Object run() {
            setCurrentCpuTime();
            cond_1.await();	//auto-gen iMonitor
            addSyncTime();
            Object x = items[takeptr]; 
            if (++takeptr == items.length) takeptr = 0;
            --count;
            //System.out.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
            return x;
         }} ) ;
   }
}
