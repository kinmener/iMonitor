

package examples.ReadersWriters;
import monitor.*;	//auto-gen iMonitor

public class NaiveImplicitReadersWritersMonitor extends ReadersWritersMonitor {
    int rcnt;
    int wcnt;
    int wwaiting;
   
    private AbstractImplicitMonitor monitor = new NaiveImplicitMonitor(); //auto-gen
    private AbstractCondition okay_read = monitor.makeCondition( //auto-gen
         new  Assertion() {
            public boolean isTrue() { return (wcnt == 0 && wwaiting == 0); } 
         } ) ;
    private AbstractCondition okay_write = monitor.makeCondition( //auto-gen
         new  Assertion() {
            public boolean isTrue() { return (rcnt == 0 && wcnt == 0); } 
         } ) ;

    public NaiveImplicitReadersWritersMonitor () {
        rcnt = 0; 
        wcnt = 0;
        wwaiting = 0;
    }

    public void startRead() {
      monitor.DoWithin( new Runnable() {
         public void run() {
            setCurrentCpuTime();
            okay_read.await();
            addSyncTime();
            rcnt++;
            //System.out.println("Reader " + Thread.currentThread() + "starts to read");
            //System.out.println("wwaiting: " + wwaiting + "\t rcnt: " + rcnt + "\twcnt: " + wcnt);
            //System.out.flush();
         }} ) ;
    }

    public void endRead() {
      monitor.DoWithin( new Runnable() {
         public void run() {
            rcnt--;
            //System.out.println("Reader " + Thread.currentThread() + "ends reading");
            //System.out.flush();
         }} ) ;
    }

    public void startWrite() {
      monitor.DoWithin( new Runnable() {
         public void run() {
  
            wwaiting++;
            setCurrentCpuTime();
            okay_write.await();
            addSyncTime();
            wwaiting--;
            wcnt = 1;
            //System.out.println("Writer " + Thread.currentThread() + "starts to write");
            //System.out.println("wwaiting: " + wwaiting + "\t rcnt: " + rcnt + "\twcnt: " + wcnt);
            //System.out.flush();
         }} ) ;
    }

    public void endWrite() {
      monitor.DoWithin( new Runnable() {
         public void run() {
            wcnt = 0;
            //System.out.println("Writer " + Thread.currentThread() + "end writing");
            //System.out.flush();
         }} ) ;
    }
}

