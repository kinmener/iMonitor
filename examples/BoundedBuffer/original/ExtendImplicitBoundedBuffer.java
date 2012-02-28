

import monitor.*;


public class ExtendImplicitBoundedBuffer extends NaiveImplicitMonitor implements ObjectBoundedBufferInterface {
	final Object[] items_;
	int putptr, takeptr, count;
	private ImplicitCondition notFull = makeCondition(
			new Assertion() {
				public boolean isTrue() { return count<items_.length ; } } ) ;
	private ImplicitCondition notEmpty = makeCondition(
			new Assertion() {
				public boolean isTrue() { return count>0 ; } } ) ;

	public ExtendImplicitBoundedBuffer(int n) {
		items_ = new Object[n];
		putptr = takeptr = count = 0;
	}

	public void put(final Object x) 
	{
		Enter();    
		notFull.await();
		items_[putptr] = x;
		if (++putptr == items_.length) putptr = 0;
		++count;
		System.out.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 
		Leave();
	}

	public Object take()
	{  
		Enter();    
		notEmpty.await();
		Object x = items_[takeptr]; 
		if (++takeptr == items_.length) takeptr = 0;
		--count;
		System.out.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
		Leave();
		return x;
	}
}
