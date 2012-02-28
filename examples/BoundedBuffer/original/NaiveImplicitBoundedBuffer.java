

import monitor.*;


class NaiveImplicitBoundedBuffer {
  final Object[] items_;
  int putptr, takeptr, count;
  private NaiveImplicitMonitor monitor = new NaiveImplicitMonitor ();
  private ImplicitCondition notFull = monitor.makeCondition(
      new Assertion() {
        public boolean isTrue() { return count<items_.length ; } } ) ;
  private ImplicitCondition notEmpty = monitor.makeCondition(
      new Assertion() {
        public boolean isTrue() { return count>0 ; } } ) ;

  public NaiveImplicitBoundedBuffer(int n) {
    items_ = new Object[n];
    putptr = takeptr = count = 0;
  }

  public void put(final Object x) 
  {
    monitor.DoWithin( new Runnable() {
      public void run() {
        notFull.await();
        items_[putptr] = x;
        if (++putptr == items_.length) putptr = 0;
        ++count;
    	System.out.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 
      }} ) ;
  }

  public Object take()
  {  
    return monitor.DoWithin( new RunnableWithResult<Object>() {
      public Object run() {
        notEmpty.await();
        Object x = items_[takeptr]; 
        if (++takeptr == items_.length) takeptr = 0;
        --count;
    	System.out.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
        return x;
      }} ) ;
  }
}
