package ru.anatol.sjema.xml.path;

import ru.anatol.sjema.xml.path.operation.Operation;

public class XPath {

    private final Operation operation;

    public XPath(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }

}
