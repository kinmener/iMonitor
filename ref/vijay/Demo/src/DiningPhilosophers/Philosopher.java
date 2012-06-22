/*
 * Philosopher.java by Yen-Jung Chang
 */

package DiningPhilosophers;
import java.util.Random;

public class Philosopher implements Runnable {
	/* random sleeping in order to simulate the transfer of state, i.e., thinking to hungry. */
	final static int SLEEP_DURATION = 1000;
	final Random random = new Random();
	
	protected int id;
	protected Fork leftFork;
	protected Fork rightFork;
	protected int numRounds; // rounds of eating

	public Philosopher(int id, int numRounds, Fork leftFork, Fork rightFork) {
		this.id = id;
		this.leftFork = leftFork;
		this.rightFork = rightFork;
		this.numRounds = numRounds;
	}

	public void run() {
		try {
			for (int i = 0; i < numRounds; i++) {
				// thinking -> hungry
				print("is thinking");
				Thread.sleep(random.nextInt(SLEEP_DURATION));
				print("is hungry");
				
				// grabbing forks
				if (id == 0) { // the first philosopher picks right fork up first
					print("is picking up right fork");
					rightFork.acquire();
					print("is picking up left fork");
					leftFork.acquire();
				} else { // the other philosophers pick left fork up first
					print("is picking up left fork");
					leftFork.acquire();
					print("is picking up right fork");
					rightFork.acquire();
				}

				// eating
				print("is eating");
				Thread.sleep(random.nextInt(SLEEP_DURATION));
				
				// eating -> thinking
				print("is finished");
				leftFork.release();
				rightFork.release();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void print(String message) {
		System.out.println("Philosopher " + id + " " + message);
	}
}
