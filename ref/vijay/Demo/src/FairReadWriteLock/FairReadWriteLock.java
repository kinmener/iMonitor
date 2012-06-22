/*
 * FairReadWriteLock.java by Dr. Vijay Garg and Yen-Jung Chang
 */
package FairReadWriteLock;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FairReadWriteLock {
	private int readerCount = 0; // number of readers in CS
	private int sequenceNumber = 1;
	private boolean writerInCS = false;
	private int writeReq = 0; //sequence number for the write request
	private ArrayList<Integer> nextWrite = new ArrayList<Integer>(); // writers in the waiting queue
	
	private final ReentrantLock lock = new ReentrantLock();
	
	public void beginRead() {
		lock.lock();
		try {
			int mynumber = sequenceNumber;  
			sequenceNumber++;
			while (writerInCS || ((!nextWrite.isEmpty()) && (writeReq < mynumber)))
				lock.wait();
			readerCount++;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void endRead() {
		lock.lock();
		try {
			readerCount--;
			if (readerCount == 0) lock.notifyAll();
		} finally {
			lock.unlock();
		}
	}

	public void beginWrite() {
		lock.lock();
		try {
			int mynumber = sequenceNumber;  
			sequenceNumber++;
			nextWrite.add(mynumber);
			writeReq = nextWrite.get(0);
			while (writerInCS || readerCount > 0 || (writeReq != mynumber))
				lock.wait();
			writerInCS = true;
			nextWrite.remove(0);
			if (!nextWrite.isEmpty()) writeReq = nextWrite.get(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void endWrite() {
		lock.lock();
		try {
			writerInCS = false;
			lock.notifyAll();
		} finally {
			lock.unlock();
		}
	}
}
