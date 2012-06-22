/*
 * testFairReadWriteLock.java by Yen-Jung Chang
 */

package FairReadWriteLock;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class testFairReadWriteLock implements Runnable {
	static final int SLEEP_DURATION = 100;
	static final int NUM_EXECUTION = 1000;
	protected final FairReadWriteSet s = new FairReadWriteSet();
	
	// randomly execute the available methods of the concurrent Set
	public void run() {
		Random generator = new Random();
		int act = generator.nextInt(100);
		try {
			if (act < 60) {
				s.contains(generator.nextInt(100));
			} else if (act < 65) {
				s.size();
			} else if (act < 90) {
				s.add(generator.nextInt(100));
			} else{
				s.remove(generator.nextInt(100));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// a concurrent Set using FairReadWriteLock
	class FairReadWriteSet {
		final HashSet<Integer> h = new HashSet<Integer>();
		final FairReadWriteLock lock = new FairReadWriteLock();
		
		public boolean contains(int x) throws InterruptedException {
			lock.beginRead();
			System.out.println("(Read) contains:" + x);
			Thread.sleep(SLEEP_DURATION);
			try { return h.contains(x); }
			finally { lock.endRead(); }
		}
		
		public int size() throws InterruptedException {
			lock.beginRead();
			System.out.println("(Read) size");
			Thread.sleep(SLEEP_DURATION);
			try { return h.size(); }
			finally { lock.endRead();}
		}
		
		public boolean add(int x) throws InterruptedException {
			lock.beginWrite();
			System.out.println("(Write) add:" + x);
			Thread.sleep(SLEEP_DURATION);
			try { return h.add(x); }
			finally { lock.endWrite(); }
		}
		
		public boolean remove(int x) throws InterruptedException {
			lock.beginWrite();
			System.out.println("(Write) remove:" + x);
			Thread.sleep(SLEEP_DURATION);
			try { return h.remove(x); }
			finally { lock.endWrite(); }
		}
	}
	
	static public void main(String args[]) {
		ExecutorService pool = Executors.newCachedThreadPool();
		for (int i = 0; i < NUM_EXECUTION; ++i)
			pool.submit(new testFairReadWriteLock());
		pool.shutdown();
	}
}
