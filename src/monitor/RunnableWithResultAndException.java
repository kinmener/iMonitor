package monitor;

public interface RunnableWithResultAndException<T1, T2 extends Exception> {
	public T1 run() throws T2;
}

