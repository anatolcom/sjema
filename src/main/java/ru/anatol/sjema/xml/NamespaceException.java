package ru.anatol.sjema.xml;

public class NamespaceException extends Exception {

    public NamespaceException(String message) {
        super(message);
    }

    public NamespaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NamespaceException(Throwable cause) {
        super(cause);
    }
}
