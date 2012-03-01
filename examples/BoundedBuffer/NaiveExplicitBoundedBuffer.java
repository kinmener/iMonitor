
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class NaiveExplicitBoundedBuffer implements ObjectBoundedBufferInterface {
  final Lock lock_ = new ReentrantLock();
  final Condition not_full_  = lock_.newCondition(); 
  final Condition not_empty_ = lock_.newCondition(); 

  final Object[] items_;
  int putptr, takeptr, count;

  public NaiveExplicitBoundedBuffer(int n) {
    items_ = new Object[n];
    putptr = takeptr = count = 0;
  }

  public void put(Object x) throws InterruptedException {
    lock_.lock();
    try {
      while (count == items_.length) 
        not_full_.await();
      items_[putptr] = x; 
      if (++putptr == items_.length) putptr = 0;
      ++count;
      //System.out.println("Producer " + Thread.currentThread() + " puts, #obj: " + count) ; 
      not_empty_.signal();
    } finally {
      lock_.unlock();
    }
  }

  public Object take() throws InterruptedException {
    lock_.lock();
    try {
      while (count == 0) 
        not_empty_.await();
      Object x = items_[takeptr]; 
      if (++takeptr == items_.length) takeptr = 0;
      --count;
      //System.out.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
      not_full_.signal();
      return x;
    } finally {
      lock_.unlock();
    }
  } 
}

