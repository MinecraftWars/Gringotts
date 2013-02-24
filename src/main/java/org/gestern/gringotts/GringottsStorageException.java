package org.gestern.gringotts;

public class GringottsStorageException extends RuntimeException {

    private static final long serialVersionUID = -7762154730712697492L;

    public GringottsStorageException() {
        super();
    }

    public GringottsStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public GringottsStorageException(String message) {
        super(message);
    }

    public GringottsStorageException(Throwable cause) {
        super(cause);
    }

}
