package org.tntstudio;

public class TNTRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1047661753383336353L;

	public TNTRuntimeException(String message) {
		super(message);
	}

	public TNTRuntimeException(Throwable t) {
		super(t);
	}

	public TNTRuntimeException(String message, Throwable t) {
		super(message, t);
	}
}
