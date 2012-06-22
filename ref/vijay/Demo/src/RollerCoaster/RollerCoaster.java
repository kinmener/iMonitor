/*
 * RollerCoaster.java by Yen-Jung Chang
 */

package RollerCoaster;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RollerCoaster {
	final static int CAR_CAPACITY = 5;
	final static int NUM_PASSENGER = 15;
	final static int NUM_ROUNDS = 3;
	
	final int carCapacity;
	final int numPassenger;
	final int numRounds;
	final Passenger[] passengers;
	
	final ReentrantLock lock = new ReentrantLock();
	final Condition Queue = lock.newCondition();
	final Condition Boarded = lock.newCondition();
	final Condition CarStarted = lock.newCondition();
	final Condition Unloaded = lock.newCondition();
	
	int passengerOnCar = 0;
	int passegnerLeftCar = 0;
	
	RollerCoaster(int carCapacity, int numPassenger, int numRounds) {
		this.carCapacity = carCapacity;
		this.numPassenger = numPassenger;
		this.numRounds = numRounds;
		this.passengers = new Passenger[numPassenger];
	}
	
	/* Get passengers and car started */
	public void startUp() {
		Car car = new Car(numRounds);
		car.start();
		for (int i = 0; i < numPassenger; ++i) {
			this.passengers[i] = new Passenger(i);
			passengers[i].start();
		}
	}
	
	public static void main(String[] args) {
		RollerCoaster rc = new RollerCoaster(CAR_CAPACITY, NUM_PASSENGER, NUM_ROUNDS);
		rc.startUp();
	}
	
	/* Coasters */
	public class Car extends Thread {
		private final int numRounds;
		
		Car(int numRounds) {
			this.numRounds = numRounds;
		}
		
		public void run() {
			for (int i = 0; i < numRounds; ++i) {
				lock.lock();
				try {
					System.out.println("Car: Boarding...");
					Queue.signalAll();
					while (passengerOnCar < carCapacity)
						Boarded.await(); // wait for boarding to complete
					
					System.out.println("Car: Riding...");
					CarStarted.signalAll();
					
					// put some delay here
					// End of riding
					Unloaded.await();
					System.out.println("Car: Unloading...");
					passegnerLeftCar = 0;
					passengerOnCar = 0;
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
			System.out.println("Car: Broken... Everyone go home now.");
			System.exit(0);
		}
	}
	
	/* Passenger */
	public class Passenger extends Thread {
		private final int id;
		
		Passenger(int id) {
			this.id = id;
		}
		
		public void run() {
			while(true) {
				try {
					takeRide();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void takeRide() throws InterruptedException {
			lock.lock();
			try {
				while (passengerOnCar == carCapacity)
					Queue.await();	// Wait in a line
				
				// Boarding
				++passengerOnCar;
				if(passengerOnCar == carCapacity) Boarded.signal(); // signal the car to start
				
				// Wait the car to start
				System.out.println("Passenger " + id + " is waiting car to start.");
				CarStarted.await();
				
				// Riding
				System.out.println("Passenger " + id + " says: Whee!");
				
				// Unloading
				++passegnerLeftCar;
				if(passengerOnCar == carCapacity)
					Unloaded.signal();
			} finally {
				lock.unlock();
			}
		}
	}
}
