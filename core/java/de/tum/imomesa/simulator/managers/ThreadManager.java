package de.tum.imomesa.simulator.managers;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import de.tum.imomesa.model.Element;
import de.tum.imomesa.simulator.threads.AbstractThread;

public class ThreadManager {
	
	// Static
	
	private static ThreadManager INSTANCE = new ThreadManager();

	public static ThreadManager getInstance() {
		return INSTANCE;
	}
	
	// Dynamic

	private CopyOnWriteArraySet<AbstractThread<?>> threads;
	private ConcurrentHashMap<AbstractThread<?>, AbstractThread<?>> threadToThreads;
	private ConcurrentHashMap<AbstractThread<?>, List<Element>> threadToObservations;
	private boolean running;
	
	private ThreadManager() {
		threads = new CopyOnWriteArraySet<>();
		threadToThreads = new ConcurrentHashMap<>();
		threadToObservations = new ConcurrentHashMap<>();
		running = false;
	}
	
	public synchronized void register(AbstractThread<?> thread) {
		if (!hasThread(thread)) {
			threads.add(thread);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void unregister(AbstractThread<?> thread) {
		if (hasThread(thread) && isUnblocked(thread)) {
			threads.remove(thread);
			unblockThread(thread);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void blockObservation(AbstractThread<?> thread, List<Element> context) throws InterruptedException {
		if (!hasBlockedObservation(thread)) {
			threadToObservations.put(thread, context);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void blockThread(AbstractThread<?> thread, AbstractThread<?> otherThread) throws InterruptedException {
		if (!hasBlockedThread(thread)) {
			threadToThreads.put(thread, otherThread);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void unblockObservation(AbstractThread<?> thread, List<Element> context) {
		if (blocksObservation(thread, context)) {
			threadToObservations.remove(thread);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public synchronized void unblockThread(AbstractThread<?> thread, AbstractThread<?> otherThread) {
		if (blocksThread(thread, otherThread)) {
			threadToThreads.remove(thread);
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}

	public synchronized void unblockObservation(List<Element> context) {
		for (AbstractThread<?> thread : threads) {
			if (blocksObservation(thread, context)) {
				unblockObservation(thread, context);
			}
		}
	}

	public synchronized void unblockThread(AbstractThread<?> otherThread) {
		for (AbstractThread<?> thread : threads) {
			if (blocksThread(thread, otherThread)) {
				unblockThread(thread, otherThread);
			}
		}
	}
	
	public synchronized <T extends AbstractThread<?>> void joinThread(AbstractThread<?> thread, T otherThread) throws InterruptedException {
		if (hasThread(otherThread)) {
			blockThread(thread, otherThread);
		}
		while (hasBlockedThread(thread)) {
			wait();
		}
	}
	
	public synchronized <T extends AbstractThread<?>> void joinThreads(AbstractThread<?> thread, Set<T> otherThreads) throws InterruptedException {
		for (AbstractThread<?> otherThread : otherThreads) {
			joinThread(thread, otherThread);
		}
	}
	
	public synchronized void join() throws InterruptedException {
		while (hasUnblocked()) {
			for (AbstractThread<?> thread : threads) {
				if (isBlocked(thread)) {
					System.out.print("Blocked thread " + thread + ": ");
					if (hasBlockedObservation(thread)) {
						System.out.print(getBlockedObservation(thread));
					}
					if (hasBlockedObservation(thread) && hasBlockedThread(thread)) {
						System.out.print(", ");
					}
					if (hasBlockedThread(thread)) {
						System.out.print(getBlockedThread(thread));
					}
					System.out.println();
				}
				else {
					System.out.println("Unblocked thread " + thread);
				}
			}
			wait();
			System.out.println("end");
		}
		return;
	}
	
	// Non-thread-safe relevant
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean getRunning() {
		return running;
	}
	
	public boolean hasBlocked() {
		for (AbstractThread<?> thread : threads) {
			if (isBlocked(thread)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasUnblocked() {
		for (AbstractThread<?> thread : threads) {
			if (isUnblocked(thread)) {
				return true;
			}
		}
		return false;
	}
	public boolean isUnblocked(AbstractThread<?> thread) {
		if (hasThread(thread)) {
			return !threadToThreads.containsKey(thread) && !threadToObservations.containsKey(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public boolean isBlocked(AbstractThread<?> thread) {
		if (hasThread(thread)) {
			return threadToThreads.containsKey(thread) || threadToObservations.containsKey(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public boolean hasBlockedObservation(AbstractThread<?> thread) {
		if (hasThread(thread)) {
			return threadToObservations.containsKey(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	public boolean hasBlockedThread(AbstractThread<?> thread) {
		if (hasThread(thread)) {
			return threadToThreads.containsKey(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public List<Element> getBlockedObservation(AbstractThread<?> thread) {
		if (hasThread(thread) && hasBlockedObservation(thread)) {
			return threadToObservations.get(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public AbstractThread<?> getBlockedThread(AbstractThread<?> thread) {
		if (hasThread(thread) && hasBlockedThread(thread)) {
			return threadToThreads.get(thread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public boolean blocksObservation(AbstractThread<?> thread, List<Element> context) {
		if (hasThread(thread)) {
			return threadToObservations.containsKey(thread) && threadToObservations.get(thread).equals(context);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public boolean blocksThread(AbstractThread<?> thread, AbstractThread<?> otherThread) {
		if (hasThread(thread)) {
			return threadToThreads.containsKey(thread) && threadToThreads.get(thread).equals(otherThread);
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	public boolean hasThread(AbstractThread<?> thread) {
		return threads.contains(thread);
	}
	
	public Set<AbstractThread<?>> getThreads() {
		return threads;
	}
	
}
