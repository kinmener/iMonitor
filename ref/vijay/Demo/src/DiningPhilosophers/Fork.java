/*
 * Fork.java by Yen-Jung Chang
 */

package DiningPhilosophers;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Fork {
	private boolean isAvailable = true;
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition forkAvilable = lock.newCondition();

	public boolean isAvailable() {
		return isAvailable;
	}

	public void acquire() throws InterruptedException {
		lock.lock();
		try {
			// if the fork is not available, sleep until it is available.
			while (isAvailable == false) forkAvilable.await();
			isAvailable = false;
		} finally {
			lock.unlock();
		}
	}
	
	public void release() {
		lock.lock();
		try {
			isAvailable = true;
			forkAvilable.signal();
		} finally {
			lock.unlock();
		}
	}

}
