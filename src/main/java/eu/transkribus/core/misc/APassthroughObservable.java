package eu.transkribus.core.misc;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.OperationCanceledException;

/**
 * Abstract class that extends Observable and will push any received updates to Observers of an instance.<br/>
 * The protected method updateStatus(Object o) will call setChanged() and notifyObservers(Object o).
 * 
 * @author philip
 *
 */
public abstract class APassthroughObservable extends Observable {
	
	
	/**
	 * The instance of the PassthroughObserver to use in subclasses
	 */
	protected final PassthroughObserver passthroughObserver;
	
	protected APassthroughObservable() {
		this.passthroughObserver = new PassthroughObserver();
	}
	
	/**
	 * passes updates to all Observers.<br/>
	 * Override in subclasses in order to react differently on received updates.
	 * 
	 * @param o
	 * @throws RuntimeException
	 * @throws OperationCanceledException
	 */
	protected void updateStatus(Object o) throws RuntimeException, OperationCanceledException {
		setChanged();
		notifyObservers(o);
	}
	
	protected class PassthroughObserver implements Observer {
		public PassthroughObserver() {}

		public void update(Observable obj, Object arg) {
			if (arg instanceof String || arg instanceof Integer) {
				updateStatus(arg);
			}
		}
	}
}
