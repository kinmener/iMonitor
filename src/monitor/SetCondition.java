package monitor;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

public class SetCondition extends AbstractCondition {

    final private AssertionConditionPair self;
    final private HashSet<SetCondition> setCondition;


    public SetCondition(ReentrantLock mutex, Assertion assertion, 
            HashSet<SetCondition> setCondition) {

        self = new AssertionConditionPair(assertion, mutex);
        this.setCondition = setCondition;

        setCondition.add(this);
    }

    public boolean conditionalSignal() {
        return self.conditionalSignal();
    }

    @Override
    public void await() {
        if(!self.assertionIsTrue()) {
            // condition.signalAll();
            for(SetCondition cond : setCondition) {
                if (cond.conditionalSignal()) {
                    break;
                }
            }
            do {
                try {
                    numContextSwitch++;
                    self.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                }
            } while(!self.assertionIsTrue()); 
        }
    }
}
