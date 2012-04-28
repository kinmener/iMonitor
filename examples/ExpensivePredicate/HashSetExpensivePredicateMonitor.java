
package examples.ExpensivePredicate;
import monitor.*;	//auto-gen iMonitor

public class HashSetExpensivePredicateMonitor implements ExpensivePredicateMonitor {

    private AbstractImplicitMonitor __monitor__628 = new HashSetMonitor(); //auto-gen
    int[] values;
    int numProc;

    public HashSetExpensivePredicateMonitor(int numProc_) {
        numProc = numProc_;
        values = new int[numProc];
        for (int i = 0; i < values.length; ++i) {
            values[i] = i; 
        }
    }
    public void setValue(int pid) {
        final int pid_dummy = pid;
        __monitor__628.DoWithin( new Runnable() {
            public void run() {
                AbstractCondition cond_1 = __monitor__628.makeCondition( //auto-gen
                    new Assertion() {
                        public boolean isTrue() { return pid_dummy == getPidOfMinValue() ; } 
                    } ) ;
                cond_1.await();
                values[pid_dummy] = values[pid_dummy] + numProc;
                System.out.println("pid: " + pid_dummy + " set value: " + values[pid_dummy]);
            }} ) ;
    }
    private int getPidOfMinValue() {
        int pid = -1;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < values.length; ++i) {
            if(min > values[i]) {
                min = values[i];
                pid = i;
            }
        }
        return pid;
    }
}
