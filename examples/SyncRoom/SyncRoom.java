
package examples.SyncRoom;

public abstract class SyncRoom {
    int[] rcnt;
    public final int N;

    int usedRoom;

    protected int ticket;
    protected int serving;

    public SyncRoom(int N) {
        this.N = N;
        rcnt = new int[N];
        ticket = 0; 
        serving = 0;
        usedRoom = -1;
        for (int i = 0; i < N; i++) {
            rcnt[i] = 0;
        }
    }

    public abstract void enterRoom(int n); 
    public abstract void leaveRoom(int n); 
}
