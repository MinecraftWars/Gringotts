package org.gestern.gringotts;

class GringottsConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -2916461691910235253L;

    public GringottsConfigurationException() {
        super();
    }

    public GringottsConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GringottsConfigurationException(String message) {
        super(message);
    }

    public GringottsConfigurationException(Throwable cause) {
        super(cause);
    }

}
