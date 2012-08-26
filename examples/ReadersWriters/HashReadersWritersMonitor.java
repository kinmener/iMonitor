

package examples.ReadersWriters;
import monitor.*;	//auto-gen iMonitor

import util.Common;

public class HashReadersWritersMonitor extends ReadersWritersMonitor {
    int rcnt;
    int wcnt;
    int wwaiting;
   
    private HashMonitor monitor = new HashMonitor(); //auto-gen
    private AbstractCondition okay_read = monitor.makeCondition( //auto-gen
         new  Assertion() {
            public boolean isTrue() { return (wcnt == 0 && wwaiting == 0); } 
         }, "test1" ) ;
    private AbstractCondition okay_write = monitor.makeCondition( //auto-gen
         new  Assertion() {
            public boolean isTrue() { return (rcnt == 0 && wcnt == 0); } 
         }, "test2" ) ;

    public HashReadersWritersMonitor() {
        rcnt = 0; 
        wcnt = 0;
        wwaiting = 0;
    }

    public void startRead() {
      monitor.DoWithin( new Runnable() {
         public void run() {
            okay_read.await();
            rcnt++;
            Common.println("Reader " + Thread.currentThread() + "starts to read");
            Common.println("wwaiting: " + wwaiting + "\t rcnt: " + rcnt + "\twcnt: " + wcnt);
         }} ) ;
    }

    public void endRead() {
      monitor.DoWithin( new Runnable() {
         public void run() {
            rcnt--;
            Common.println("Reader " + Thread.currentThread() + "ends reading");
         }} ) ;
    }

    public void startWrite() {
      monitor.DoWithin( new Runnable() {
         public void run() {
  
            wwaiting++;
            okay_write.await();
            wwaiting--;
            wcnt = 1;
            Common.println("Writer " + Thread.currentThread() + "starts to write");
            Common.println("wwaiting: " + wwaiting + "\t rcnt: " + rcnt + "\twcnt: " + wcnt);
         }} ) ;
    }

    public void endWrite() {
      monitor.DoWithin( new Runnable() {
         public void run() {
            wcnt = 0;
            Common.println("Writer " + Thread.currentThread() + "end writing");
         }} ) ;
    }
}

