package Tree;

import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLException;

import org.omg.PortableServer.THREAD_POLICY_ID;

public class ThreadId {
	private static final AtomicInteger nextId = new AtomicInteger(0);
	
	private static final ThreadLocal<Integer> threadId = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return nextId.getAndIncrement();
		}
	};
	
	public static int get() {
		return threadId.get();
	}
	
	
}
