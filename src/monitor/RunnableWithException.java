package monitor;

public interface RunnableWithException<T extends Exception> {
	public void run() throws T;
}

