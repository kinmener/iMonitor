
package monitor;


import java.util.concurrent.locks.Lock; 
import java.util.concurrent.locks.Condition; 
import java.util.concurrent.locks.ReentrantLock;

public class NaiveImplicitMonitor extends AbstractImplicitMonitor {
  private java.util.concurrent.locks.Condition condition_ = null;
  //public NaiveImplicitMonitor(Assertion assertion) {}
  protected final void Enter() {
    lock_.lock();
    occupant_ = Thread.currentThread();
  }
  protected final void Leave() {
    if(condition_ != null) condition_.signal();
    occupant_ = null;
    lock_.unlock();
  }
  public void DoWithin( Runnable runnable ) {
    super.DoWithin( runnable ) ; }

  public<T> T DoWithin( RunnableWithResult<T> runnable ) {
    return super.DoWithin( runnable ) ; }

  public ImplicitCondition makeCondition(Assertion assertion) {
    if(condition_ == null) condition_ = lock_.newCondition();
    return new ImplicitCondition(condition_, assertion);
  }
}
