package pdsl;

import java.util.HashSet;
import locks.Condition;

public class PDSLCondition {

    private Condition self;
    private HashSet<Condition> setConditions;

    public PDSLCondition(Condition self_, HashSet<Condition> setConditions_) {
        self = self_;
        setConditions = setConditions_;
    }

    public void await() throws InterruptedException {
        for(Condition cond : setConditions) {
            if(cond != self) {
                cond.signal();
            }
        }
        self.await();
    }
}
