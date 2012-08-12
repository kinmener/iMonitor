
package examples.DiningPhilosophers;
import monitor.*;	//auto-gen iMonitor

public class HashDiningServerImpl extends DiningServer {

    private HashMonitor __monitor__628 = new HashMonitor(); //auto-gen
    /**

     * Constructor.
     * @param numPhils The number of dining philosophers.
     */
    private HashDiningServerImpl(int numPhils) {
        super(numPhils);
    }

    /**

     * Factory method.
     * @param numPhils The number of dining philosophers.
     * @return A dining server table object.
     */
    public static DiningServer newInstance(int numPhils) {
        DiningServer instance = new HashDiningServerImpl(numPhils);
        return instance;
    }

    /**

     * Test the availability of the two forks for a hungry philosopher.
     * If available, set the state of the hungry philosopher to eating.
     * @param k The number of the hungry philosopher.
     */
    private void test(int k) { 
        final int k_dummy = k;
        __monitor__628.DoWithin( new Runnable() { public void run() {
            if (state[left(k_dummy)] != State.EATING && state[k_dummy] == State.HUNGRY &&
                state[right(k_dummy)] != State.EATING) {
                state[k_dummy] = State.EATING;
                }
        } }); }

        /**

         * A hungry philosopher attempts to pick up its two forks.  If available,
         * the philosopher eats, else waits.
         * @param i The number of the hungry philosopher.
         * @throws InterruptedException
         */
        protected void takeForks(int i) throws InterruptedException { 
            final int i_dummy = i;
            __monitor__628.DoWithin( new RunnableWithException<InterruptedException>() { public void run() throws InterruptedException {
                state[i_dummy] = State.HUNGRY;
                printState("begin takeForks");
                test(i_dummy);
                printState("end   takeForks");
                AbstractCondition cond_1 = __monitor__628.makeCondition( //auto-gen
                    new Assertion() {
                        public boolean isTrue() { return state[i_dummy] == State.EATING; } 
                    },
                    "state[i_dummy] == State.EATING" + "_" + i_dummy) ;

                cond_1.await();
            } }); }

            /**

             * A philosopher has finished eating.  Return its two forks to the table
             * and check for hungry neighbors.  If a hungry neighbor's two forks
             * are now available, nudge the neighbor.
             * @param i The number of the philosopher finished eating.
             */
            protected void putForks(int i) { 
                final int i_dummy = i;
                __monitor__628.DoWithin( new Runnable() { public void run() {
                    if (state[i_dummy] != State.EATING) return;
                    state[i_dummy] = State.THINKING;
                    printState("begin  putForks");
                    test(left(i_dummy));  test(right(i_dummy));
                    printState("end    putForks");
                } }); }
}
