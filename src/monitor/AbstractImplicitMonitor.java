
package monitor;


public abstract class AbstractImplicitMonitor {

    protected abstract void enter(); 

    protected abstract void leave(); 

    public void DoWithin(Runnable runnable) {
        enter();
        try {
            runnable.run();  
        } finally {
            leave();
        }
    }
    public<T> T DoWithin( RunnableWithResult<T> runnable ) {
        enter() ;
        try {   
            return runnable.run() ; 
        }
        finally {
            leave() ; 
        }
    }
    public<T extends Exception> void DoWithin( 
            RunnableWithException<T> runnable ) throws T {
      enter() ;
      try {
        runnable.run() ; }
      finally {
       leave() ; }
    }
    public<T1, T2 extends Exception> T1 DoWithin( 
            RunnableWithResultAndException<T1, T2> runnable ) throws T2 {
      enter() ;
      try {
        return runnable.run() ; }
      finally {
       leave() ; }
    }
    public abstract AbstractCondition makeCondition(Assertion assertion);
    public abstract void removeCondition(AbstractCondition condition);
}


