
package examples.BoundedBuffer;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import pdsl.PDSLLock;

class PDSLLockBoundedBuffer extends ObjectBoundedBuffer {
  final PDSLLock lock_ = new PDSLLock();

  final Object[] items_;
  int putptr, takeptr, count;


  public PDSLLockBoundedBuffer(int n) {
    items_ = new Object[n];
    putptr = takeptr = count = 0;
  }

  public void put(Object x) throws InterruptedException {
    lock_.lock();
    try {
      setCurrentCpuTime();
      while (count == items_.length) 
        lock_.await();
      addSyncTime();
      items_[putptr] = x; 
      if (++putptr == items_.length) putptr = 0;
      ++count;
    } finally {
      lock_.unlock();
    }
  }

  public Object take() throws InterruptedException {
    lock_.lock();
    try {
      setCurrentCpuTime();
      mapThreadCpuTime.put(Thread.currentThread().getId(), threadMXBean.getCurrentThreadCpuTime());
      while (count == 0) 
        lock_.await();
      addSyncTime();
      Object x = items_[takeptr]; 
      if (++takeptr == items_.length) takeptr = 0;
      --count;
      //System.out.println("Consumer " + Thread.currentThread() + " takes, #obj: " + count) ; 
      return x;
    } finally {
      lock_.unlock();
    }
  } 
}

