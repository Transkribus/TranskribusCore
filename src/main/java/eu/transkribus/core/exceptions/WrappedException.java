package eu.transkribus.core.exceptions;

public class WrappedException extends RuntimeException {
	private static final long serialVersionUID = -293766358611838374L;

	public WrappedException(Throwable t) {
		super(t);
		if (t == null) {
			throw new RuntimeException("Throwable cannot be null in WrappedException!");
		}
	}
}
