package ru.anatol.sjema.printer;

import java.io.PrintStream;

public class BooleanPrinter implements Printer {

    private Boolean value;

    public BooleanPrinter() {
    }

    public BooleanPrinter(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public String print() {
        StringBuilder itemBuilder = new StringBuilder();
        if (value == null) {
            itemBuilder.append("null");
        } else {
            itemBuilder.append(Boolean.toString(value));
        }
        return itemBuilder.toString();
    }

    @Override
    public void print(PrintStream printStream, int indent) {
        printStream.print(print());
    }
}
