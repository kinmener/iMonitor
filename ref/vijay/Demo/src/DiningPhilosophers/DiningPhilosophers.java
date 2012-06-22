/*
 * DiningPhilosophers.java by Yen-Jung Chang
 */

package DiningPhilosophers;

public class DiningPhilosophers {
	static final int NUM_PHILOSOPHERS = 3;
	static final int NUM_ROUNDS = 2;
	
	/* initialize philosophers and forks. Then make them started. */
	public static void main(String args[]) {
		Fork[] forks = new Fork[NUM_PHILOSOPHERS];
		Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];

		for (int i = 0; i < NUM_PHILOSOPHERS; ++i) {
			forks[i] = new Fork();
		}

		for (int i = 0; i < NUM_PHILOSOPHERS; ++i) {
			Fork leftFork = forks[i];
			Fork rightFork = forks[(i + 1) % NUM_PHILOSOPHERS];
			philosophers[i] = new Philosopher(i, NUM_ROUNDS, leftFork, rightFork);
			Thread thread = new Thread(philosophers[i]);
			thread.start();
		}
	}
}
