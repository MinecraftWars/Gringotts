package org.gestern.gringotts;

public class GringottsException extends RuntimeException {

    private static final long serialVersionUID = 476895381491480536L;

    public GringottsException() {
        super();
    }

    public GringottsException(String message, Throwable cause) {
        super(message, cause);
    }

    public GringottsException(String message) {
        super(message);
    }

    public GringottsException(Throwable cause) {
        super(cause);
    }
}
