package examples.ExpensivePredicate;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExplicitExpensivePredicateMonitor implements ExpensivePredicateMonitor {

    Lock mutex;
    int[] values;
    Condition[] conditions;
    int numProc;

    public ExplicitExpensivePredicateMonitor(int numProc_) {

        numProc = numProc_;
        mutex = new ReentrantLock();
        values = new int[numProc];
        conditions = new Condition[numProc];

        for (int i = 0; i < values.length; ++i) {
            values[i] = i; 
            conditions[i] = mutex.newCondition();
        }
    }

    public void setValue(int pid) {
        mutex.lock();
        int pidOfMin = getPidOfMinValue();
        if(pid != pidOfMin) {
            conditions[pidOfMin].signal();
            try {
                conditions[pid].await();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            pidOfMin = getPidOfMinValue();
        }
        values[pid] = values[pid] + numProc;
        conditions[getPidOfMinValue()].signal();
        System.out.println("pid: " + pid + " set value: " + values[pid]);
        mutex.unlock();
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
  //  private int getAvg() {
  //      int sum = 0;
  //      for (int i = 0; i < values.length; ++i) {
  //          sum += values[i];
  //      }
  //      return sum/values.length;
  //  }
}
