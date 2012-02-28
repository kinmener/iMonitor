
import monitor.* ;

/** Count the number of threads that complete a task.
 * One thread can wait until they are all done.
 */

public class DoneCounter extends NaiveImplicitMonitor {
    int count = 0 ;
    int goal = 0 ;
    ImplicitCondition done = makeCondition( new Assertion() {
        public boolean isTrue() { return count == goal ; } } ) ;

    public void set( int val ) {
        Enter() ;
        goal = val ;
        count = 0 ;
        Leave() ; }

    public int increment() {
        Enter() ;
        count += 1 ;
        int value = count ;
        Leave() ;
        return value ;
    }

    public void waitForDone() {
        Enter() ;
        done.await() ;
        Leave() ;
    }
}
