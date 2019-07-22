package eu.transkribus.core.misc;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.OperationCanceledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that extends Observable and will push any received updates to Observers of an instance.<br/>
 * The protected method updateStatus(Object o) will call setChanged() and notifyObservers(Object o).
 * 
 * @author philip
 *
 */
public abstract class APassthroughObservable extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(APassthroughObservable.class);
	
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
	protected void updateStatus(Object arg) throws RuntimeException, OperationCanceledException {
		this.updateStatus(null, arg);
	}
	
	/**
	 * passes updates to all Observers.<br/>
	 * Override in subclasses in order to react differently on received updates.
	 * 
	 * @param obj the sending Observable
	 * @param o the update
	 * @throws RuntimeException
	 * @throws OperationCanceledException
	 */
	protected void updateStatus(Observable obj, Object arg) throws RuntimeException, OperationCanceledException {
		logger.debug("{} passes observer update from {}: {}", 
				this.getClass().getSimpleName(), 
				(obj != null ? obj.getClass().getSimpleName() : obj), 
				arg
			);
		setChanged();
		notifyObservers(arg);
	}
	
	protected class PassthroughObserver implements Observer {
		public PassthroughObserver() {}

		public void update(Observable obj, Object arg) {
			updateStatus(obj, arg);
		}
	}
}
