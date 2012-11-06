
package examples.SleepingBarber;


public abstract class BarberShop {
    public BarberShop(int maxFreeSeat) {
        this.maxFreeSeat = maxFreeSeat;
        numFreeSeat = maxFreeSeat;
    }

    protected int numFreeSeat;
    protected int maxFreeSeat;
    public abstract void cutHair();
    public abstract boolean waitToCut();

}
