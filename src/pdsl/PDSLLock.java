package pdsl;


import locks.Condition;
import locks.Lock;
import locks.ReentrantLock;

public class PDSLLock {
    private Lock innerLock;
    private Condition innerCondition;

    public PDSLLock() {
        innerLock = new ReentrantLock();
        innerCondition = innerLock.newCondition();
    }

    public void lock() {
        innerLock.lock();
    }

    public void unlock() {
        innerCondition.signalAll();
        innerLock.unlock();
    }

    public void await() throws InterruptedException {
        innerCondition.signalAll();
        innerCondition.await();
    }
}
