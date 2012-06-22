/*
 * ProducerConsumers.java by Yen-Jung Chang
 */
package ProducerConsumers;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumers {
	static final int MAX_CAPACITY = 100;
	static final int NUM_PRODUCERS = 4;
	static final int NUM_CONSUMERS = 3;
	boolean simulationDone = false;
	
	/* Reentrant lock and Conditions */
	final ReentrantLock lock = new ReentrantLock();
	final Condition hasProduct = lock.newCondition();
	final Condition hasRoom = lock.newCondition();
	
	/* Buffer */
	LinkedList<Object> buffer = new LinkedList<Object>();

	/* Producer */
	class Producer extends Thread {
		public void run() {
			while (true) {
				Object product = doProduce();
				
				lock.lock();
				try {
					while (buffer.size() == MAX_CAPACITY) {	// buffer is full
						try {
							if (simulationDone) break;
							System.out.println("Producer is WAITING");
							hasRoom.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (simulationDone) break;
					buffer.addFirst(product);
					hasProduct.signalAll();
					System.out.println("Produced; Buffer size: " + buffer.size());
				} finally {
					lock.unlock();
				}
			}
		}

		Object doProduce() {
			return new Object();
		}
	}

	/* Consumer */
	class Consumer extends Thread {
		public void run() {
			while (true) {
				@SuppressWarnings("unused")
				Object obj = null;
				lock.lock();
				try {
					while (buffer.size() == 0) { // buffer is empty
						try {
							if (simulationDone) break;
							System.out.println("Consumer is WAITING");
							hasProduct.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (simulationDone) break;
					obj = buffer.removeLast();
					hasRoom.signalAll();
					System.out.println("Object consumed; Buffer size: " + buffer.size());
				} finally {
					lock.unlock();
				}
			}
		}
	}

	// Get all producers and consumers started
	ProducerConsumers(int nP, int nC) {
		for (int i = 0; i < nP; ++i)
			new Producer().start();
		for (int i = 0; i < nC; ++i)
			new Consumer().start();
	}

	public static void main(String[] args) throws InterruptedException {
		// Start to produce and consume
		ProducerConsumers pc = new ProducerConsumers(NUM_PRODUCERS, NUM_CONSUMERS);

		// Keep running for 5 seconds
		Thread.sleep(500); 

		// The end
		pc.lock.lock();
		try {
			pc.simulationDone = true;
			pc.hasProduct.signalAll();
			pc.hasRoom.signalAll();
		} finally {
			pc.lock.unlock();
		}
	}
}