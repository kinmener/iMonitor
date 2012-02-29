
package monitor;

//import java.util.ArrayList ;
import java.util.concurrent.locks.Lock; 
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractImplicitMonitor {
    final Lock lock = new ReentrantLock(true);
    volatile Thread occupant = null;

    protected abstract void Enter(); 

    protected abstract void Leave(); 

    public void DoWithin(Runnable runnable) {
        Enter();
        try {
            runnable.run();  
        } finally {
            Leave();
        }
    }
    public<T> T DoWithin( RunnableWithResult<T> runnable ) {
        Enter() ;
        try {   
            return runnable.run() ; 
        }
        finally {
            Leave() ; 
        }
    }
    public<T extends Exception> void DoWithin( RunnableWithException<T> runnable ) throws T {
      Enter() ;
      try {
        runnable.run() ; }
      finally {
       /* if( occupant_ == Thread.currentThread() ) */ Leave() ; }
    }
    public abstract AbstractCondition makeCondition(Assertion assertion);
    public abstract void removeCondition(AbstractCondition abstractCondition);
}


