package pdsl;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
