
package monitor;

//import java.util.ArrayList ;
import java.util.concurrent.locks.Lock; 
import java.util.concurrent.locks.Condition; 
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractImplicitMonitor {
  final Lock lock_ = new ReentrantLock(true);
  volatile Thread occupant_ = null;

  protected abstract void Enter(); 

  protected abstract void Leave(); 

  protected void DoWithin(Runnable runnable) {
    Enter();
    try {
      runnable.run();  
    } finally {
      Leave();
    }
  }
  protected<T> T DoWithin( RunnableWithResult<T> runnable ) {
    Enter() ;
    try {
      return runnable.run() ; }
    finally {
     /* if( occupant_ == Thread.currentThread() ) */ Leave() ; }
  }
  protected abstract ImplicitCondition makeCondition(Assertion assertion); 
}


