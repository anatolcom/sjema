package ru.anatol.sjema.xml;

public class DomException extends Exception {

    public DomException(String message) {
        super(message);
    }

    public DomException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomException(Throwable cause) {
        super(cause);
    }

}
