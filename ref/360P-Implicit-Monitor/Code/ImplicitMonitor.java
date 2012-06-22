import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.ArrayList;

public class ImplicitMonitor
{
    private class ConditionData
    {
	public ConditionData(ImplicitCondition a)
	{
	    condition = a;
	    lockCondition = null;
	    numWaiting = 1;
	}
	public ImplicitCondition condition;
	public Condition lockCondition;
	public int numWaiting;
	@Override public boolean equals(Object o)
	{
	    if(o instanceof ConditionData)
		return condition.equals(((ConditionData)o).condition);
	    else
		return false;
	}
    }
    private final ReentrantLock lock = new ReentrantLock(false);
    private ArrayList<ConditionData> conditions = new ArrayList<ConditionData>();
    public void lock()
    {
	lock.lock();
    }
    public void unlock()
    {
	signal();
	lock.unlock();
    }
    public void wait(ImplicitCondition c)
    {

	if(!lock.isHeldByCurrentThread())
	    throw new IllegalMonitorStateException();
	if(c.check())
	    return;
	signal();
	ConditionData condition = new ConditionData(c);
	int index = conditions.indexOf(condition);
	if(index == -1)
	{
	    condition.lockCondition = lock.newCondition();
	    conditions.add(condition);
	}
	else
	{
	    condition = conditions.get(index);
	    condition.numWaiting++;
	}
	while(!c.check())
	{
	    try
	    {
		condition.lockCondition.await();
	    }
	    catch(InterruptedException e)
	    {
	    }
	}
	condition.numWaiting--;
    }
    private void signal()
    {
	for(ConditionData c : conditions)
	{
	    if(c.condition.check())
		c.lockCondition.signal();
	}
    }
}
