package examples.TicketReadersWriters;

import java.util.HashMap;

public abstract class ReadersWritersMonitor {


    public abstract void startRead();
    public abstract void endRead();
    public abstract void startWrite();
    public abstract void endWrite();
}
