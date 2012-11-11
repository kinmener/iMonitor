package examples.SyncRoom;

import examples.util.DoneCounter;
import java.util.concurrent.atomic.AtomicInteger;
import util.Common;

class Process extends Thread {
    SyncRoom room;
    final int numAccess;
    private DoneCounter doneCounter ;

    public Process(SyncRoom room, int numAccess, DoneCounter doneCounter) {
        this.room = room;
        this.numAccess = numAccess;
        this.doneCounter = doneCounter;
    }

    public void run() {
        for (int i = 0; i < numAccess; i++) {
            int n = (int) (room.N * Math.random());
            room.enterRoom(n);
            try {
                Thread.sleep(10);
            } catch(Exception e) {
            }
            room.leaveRoom(n);
        }
        doneCounter.increment();
    }
}

public class TestSyncRoom {
    
    public static void main (String [] args)
    {
        int numProcess = 10;
        int totalAccess = 10;
        int numRoom = 10;

        SyncRoom room = null; 
        try {
            numRoom = Integer.parseInt(args[3]);
            switch(args[0].charAt(0)) {
            case 'e':
                room = new ExplicitSyncRoom(numRoom);
                break;
            default: 
                room = new AutoSyncRoom(numRoom, args[0].charAt(0));
                break;
            }
            numProcess = Integer.parseInt(args[1]);
            totalAccess = Integer.parseInt(args[2]);
        } catch (Exception e) { /* use defaults */ 
            e.printStackTrace();
            return;
        }

        long startTime = System.currentTimeMillis();

        DoneCounter doneCounter = new DoneCounter() ;
        doneCounter.set(numProcess) ;

        Process[] p = new Process[numProcess];
        for( int k = 0 ; k < numProcess; ++k ) {
            p[k] = new Process(room, totalAccess / numProcess, doneCounter);
            p[k].start(); 
        }

        doneCounter.waitForDone();
       
        long execTime = System.currentTimeMillis() - startTime;
        System.out.println( execTime );
    }
}
