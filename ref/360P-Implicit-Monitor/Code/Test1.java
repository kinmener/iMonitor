import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Test1 extends Thread
{
    public static boolean verbose;
    private static Queue queue;
    private boolean put;
    private int id;
    private static int count = 0;
    public Test1(boolean putl)
    {
	id = count++;
	put = putl;
    }
    public static void main(String [] args)
    {
	verbose = false;
	for(String s : args)
	{
	    if(s.equals("monitor"))
		queue = new QueueExplicitMonitor(Integer.parseInt(args[1]));
	    else if(s.equals("implicit"))
		queue = new QueueImplicitMonitor(Integer.parseInt(args[1]));
	    else if(s.equals("explicit"))
		queue = new QueueExplicitCondition(Integer.parseInt(args[1]));
	    else if(s.equals("implicitall"))
		queue = new QueueImplicitMonitorAll(Integer.parseInt(args[1]));
	    else if(s.equals("explicitall"))
		queue = new QueueExplicitConditionAll(Integer.parseInt(args[1]));
	    else if(s.equals("v") || s.equals("verbose"))
		verbose = true;
	}
	ArrayList<Test1> threads = new ArrayList<Test1>();
	for(int i = 0; i < 1000; i++)
	    threads.add(new Test1(false));
	for(int j = 0; j < 1000; j++)
	    threads.add(new Test1(true));
	long time = System.currentTimeMillis();
	for(Test1 thread : threads)
	    thread.start();
	for(Test1 thread : threads)
	{
	    try
	    {
		thread.join();
	    }
	    catch(InterruptedException e)
	    {
	    }
	}
	time = System.currentTimeMillis() - time;
	if(!verbose)
	    System.out.println(time);
    }

    public void print(String i)
    {
	if(verbose)
	    System.out.println(id + ": " + i);
    }

    @Override public void run()
    {
	for(int i = 0; i < 100; i++)
	{
	    if(put)
		queue.put(this);
	    else
		queue.take(this);
	}
    }
}

interface Queue
{
    public void put(Test1 t);
    public void take(Test1 t);
}

class QueueExplicitMonitor implements Queue
{
    private int size;
    private int count = 0;
    public QueueExplicitMonitor(int s)
    {
	size = s;
    }
    @Override public synchronized void put(Test1 t)
    {
	while(count >= size)
	{
	    try
	    {
		t.print("Wait");
		wait();
		t.print("Woke");
	    }
	    catch(InterruptedException e)
	    {
	    }
	}
	t.print("Enter");
	count++;
	t.print("" + count);
	t.print("Exit");
	notifyAll();
    }
    @Override public synchronized void take(Test1 t)
    {
	while(count <= 0)
	{
	    try
	    {
		t.print("Wait");
		wait();
		t.print("Woke");
	    }
	    catch(InterruptedException e)
	    {
	    }
	}
	t.print("Enter");
	count--;
	t.print("" + count);
	t.print("Exit");
	notifyAll();
    }
}

class QueueExplicitCondition implements Queue
{
    ReentrantLock lock = new ReentrantLock(false);
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();
    private int size;
    private int count = 0;
    public QueueExplicitCondition(int s)
    {
	size = s;
    }
    @Override public void put(Test1 t)
    {
	lock.lock();
	while(count >= size)
	{
	    try
	    {
		t.print("Wait");
		notFull.await();
		t.print("Woke");
	    }
	    catch(InterruptedException e)
	    {
	    }
	}
	t.print("Enter");
	count++;
	t.print("" + count);
	t.print("Exit");
	notEmpty.signal();
	lock.unlock();
    }
    @Override public void take(Test1 t)
    {
	lock.lock();
	while(count <= 0)
	{
	    try
	    {
		t.print("Wait");
		notEmpty.await();
		t.print("Woke");
	    }
	    catch(InterruptedException e)
	    {
	    }
	}
	t.print("Enter");
	count--;
	t.print("" + count);
	t.print("Exit");
	notFull.signal();
	lock.unlock();
    }
}

class QueueExplicitConditionAll implements Queue
{
    ReentrantLock lock = new ReentrantLock(false);
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();
    private int size;
    private int count = 0;
    public QueueExplicitConditionAll(int s)
    {
	size = s;
    }
    @Override public void put(Test1 t)
    {
	lock.lock();
	while(count >= size)
	{
	    try
	    {
		t.print("Wait");
		notFull.await();
		t.print("Woke");
	    }
	    catch(InterruptedException e)
	    {
	    }
	}
	t.print("Enter");
	count++;
	t.print("" + count);
	t.print("Exit");
	notEmpty.signalAll();
	lock.unlock();
    }
    @Override public void take(Test1 t)
    {
	lock.lock();
	while(count <= 0)
	{
	    try
	    {
		t.print("Wait");
		notEmpty.await();
		t.print("Woke");
	    }
	    catch(InterruptedException e)
	    {
	    }
	}
	t.print("Enter");
	count--;
	t.print("" + count);
	t.print("Exit");
	notFull.signalAll();
	lock.unlock();
    }
}

class QueueImplicitMonitor implements Queue
{
    private class ConditionNotFull implements ImplicitCondition
    {
	@Override public boolean check()
	{
	    return count < size;
	}
    }
    private class ConditionNotEmpty implements ImplicitCondition
    {
	@Override public boolean check()
	{
	    return count > 0;
	}
    }
    private ConditionNotFull cnf = new ConditionNotFull();
    private ConditionNotEmpty cne = new ConditionNotEmpty();
    private int size;
    private int count = 0;
    ImplicitMonitor monitor = new ImplicitMonitor();
    public QueueImplicitMonitor(int s)
    {
	size = s;
    }
    @Override public void put(Test1 t)
    {
	monitor.lock();
	if(!cnf.check())
	{
	    t.print("Wait");
	    monitor.wait(cnf);
	    t.print("Woke");
	}
	t.print("Enter");
	count++;
	t.print("" + count);
	t.print("Exit");
	monitor.unlock();
    }
    @Override public void take(Test1 t)
    {
	monitor.lock();
	if(!cne.check())
	{
	    t.print("Wait");
	    monitor.wait(cne);
	    t.print("Woke");
	}
	t.print("Enter");
	count--;
	t.print("" + count);
	t.print("Exit");
	monitor.unlock();
    }
}

class QueueImplicitMonitorAll implements Queue
{
    private class ConditionNotFull implements ImplicitCondition
    {
	@Override public boolean check()
	{
	    return count < size;
	}
    }
    private class ConditionNotEmpty implements ImplicitCondition
    {
	@Override public boolean check()
	{
	    return count > 0;
	}
    }
    private ConditionNotFull cnf = new ConditionNotFull();
    private ConditionNotEmpty cne = new ConditionNotEmpty();
    private int size;
    private int count = 0;
    ImplicitMonitorAll monitor = new ImplicitMonitorAll();
    public QueueImplicitMonitorAll(int s)
    {
	size = s;
    }
    @Override public void put(Test1 t)
    {
	monitor.lock();
	if(!cnf.check())
	{
	    t.print("Wait");
	    monitor.wait(cnf);
	    t.print("Woke");
	}
	t.print("Enter");
	count++;
	t.print("" + count);
	t.print("Exit");
	monitor.unlock();
    }
    @Override public void take(Test1 t)
    {
	monitor.lock();
	if(!cne.check())
	{
	    t.print("Wait");
	    monitor.wait(cne);
	    t.print("Woke");
	}
	t.print("Enter");
	count--;
	t.print("" + count);
	t.print("Exit");
	monitor.unlock();
    }
}
