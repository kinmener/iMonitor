package examples.H2OBarrier;

import examples.util.DoneCounter;
import java.util.concurrent.atomic.AtomicInteger;
import util.Common;

class HGenerator extends Thread {
    H2OBarrier hb;
    AtomicInteger numH;
    final int totalH;
    private DoneCounter doneCounter ;

    public HGenerator(H2OBarrier hb, int totalH, AtomicInteger numH, 
            DoneCounter doneCounter) {
        this.hb = hb;
        this.totalH = totalH;
        this.numH = numH;
        this.doneCounter = doneCounter;
    }

    public void run() {
        int n = numH.getAndIncrement();
        while (n < totalH) {
            hb.HReady();
            n = numH.getAndIncrement();
        }
        doneCounter.increment();
    }
}

class OGenerator extends Thread {
    H2OBarrier hb; 
    AtomicInteger numO;
    final int totalO;
    private DoneCounter doneCounter ;

    public OGenerator(H2OBarrier hb, int totalO, AtomicInteger numO,
            DoneCounter doneCounter) {
        this.hb = hb;
        this.totalO = totalO;
        this.numO = numO;
        this.doneCounter = doneCounter;
    }
    public void run() {
        int n = numO.getAndIncrement();
        while (n < totalO) {
            hb.OReady(); 
            n = numO.getAndIncrement();
        }
        doneCounter.increment();
    }
}

public class TestH2OBarrier {
    
    public static void main (String [] args)
    {
        int numOGenerator = 10;
        int numHGenerator = 10;
        int totalWater = 10;

        AtomicInteger numO = new AtomicInteger(0);
        AtomicInteger numH = new AtomicInteger(0);


        H2OBarrier hb = null; 
        try {
            switch(args[0].charAt(0)) {
            case 'e':
                hb = new ExplicitH2OBarrier();
                break;
            default: 
                hb = new AutoH2OBarrier(args[0].charAt(0));
                break;
            }
            numHGenerator = Integer.parseInt(args[1]);
            numOGenerator = Integer.parseInt(args[2]);
            totalWater = Integer.parseInt(args[3]);
        } catch (Exception e) { /* use defaults */ 
            e.printStackTrace();
            return;
        }
        long startTime = System.currentTimeMillis();

        DoneCounter doneCounter = new DoneCounter() ;
        doneCounter.set(numHGenerator + numOGenerator) ;

        HGenerator[] h = new HGenerator[numHGenerator];
        for( int k = 0 ; k < numHGenerator; ++k ) {
            h[k] = new HGenerator(hb, totalWater * 2, numH, doneCounter);
            h[k].start(); 
        }

        OGenerator[] o = new OGenerator[numOGenerator];
        for( int k = 0 ; k < numOGenerator; ++k ) {
            o[k] = new OGenerator(hb, totalWater, numO, doneCounter);
            o[k].start(); 
        }

        doneCounter.waitForDone();
       
        long execTime = System.currentTimeMillis() - startTime;
        System.out.println( execTime );
    }
}
