package monitor;

import java.util.concurrent.locks.Condition; 

public class ImplicitCondition {
  private Assertion assertion_;
  private java.util.concurrent.locks.Condition condition_;

  public ImplicitCondition(java.util.concurrent.locks.Condition condition, Assertion assertion) {
    condition_ = condition;
    assertion_ = assertion; 
  }

  public void await() {
    while(!assertion_.isTrue()) {
      try {
        condition_.signalAll();
        condition_.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }finally {
      }
    }
  }
}

