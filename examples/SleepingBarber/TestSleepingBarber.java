package examples.SleepingBarber;


import examples.util.DoneCounter;
class Barber extends Thread {
    private DoneCounter doneCounter ;
    BarberShop bs;
    int numCut;

    public Barber(BarberShop bs, int numCut, DoneCounter doneCounter) {
        this.bs = bs;
        this.numCut = numCut;
        this.doneCounter = doneCounter;
    }

    public void run() {
        for(int i = 0; i < numCut; i++) {
            bs.cutHair();
        }
        doneCounter.increment() ;
    }
}

class Consumer extends Thread {
    private DoneCounter doneCounter ;
    BarberShop bs; 
    long numCut;

    public Consumer(BarberShop bs, int numCut, DoneCounter doneCounter) {
        this.bs = bs;
        this.numCut = numCut;
        this.doneCounter = doneCounter;
    }
    public void run() {
        for (int i = 0; i < numCut; ) {
            if (bs.waitToCut()) {
                ++i; 
            }
        }
        doneCounter.increment() ;
    }
}

public class TestSleepingBarber {
    
    public static void main (String [] args)
    {
        int numBarber = 10;
        int numConsumer = 10;
        int totalCut = 10; 

        BarberShop bs = null; 
        int maxSeat = Integer.parseInt(args[0]);
        try {
            switch(args[1].charAt(0)) {
            case 'e':
                bs = new ExplicitBarberShop(maxSeat);
                break;
            default: 
                bs = new AutoBarberShop(maxSeat, args[1].charAt(0));
                break;
            }
            numBarber = Integer.parseInt(args[2]);
            numConsumer = Integer.parseInt(args[3]);
            totalCut = Integer.parseInt(args[4]);
        } catch (Exception e) { /* use defaults */ 
            e.printStackTrace();
            return;
        }
        DoneCounter doneCounter = new DoneCounter() ;
        doneCounter.set(numBarber + numConsumer) ;
        long startTime = System.currentTimeMillis();


        Barber[] b = new Barber[numBarber];
        for( int k = 0 ; k < numBarber; ++k ) {
            b[k] = new Barber(bs, totalCut/numBarber, doneCounter);
            b[k].start(); 
        }

        Consumer[] c = new Consumer[numConsumer];
        for( int k = 0 ; k < numConsumer; ++k ) {
            c[k] = new Consumer(bs, totalCut/numConsumer, doneCounter) ;
            c[k].start(); 
        }

        doneCounter.waitForDone() ;
       
        long execTime = System.currentTimeMillis() - startTime;
        System.out.println( execTime );
    }
}
