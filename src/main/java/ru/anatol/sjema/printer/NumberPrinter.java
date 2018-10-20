package ru.anatol.sjema.printer;

import java.io.PrintStream;

public class NumberPrinter implements Printer {

    private Number value;

    public NumberPrinter() {
    }

    public NumberPrinter(Number value) {
        this.value = value;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String print() {
        StringBuilder itemBuilder = new StringBuilder();
        if (value == null) {
            itemBuilder.append("null");
        } else {
            itemBuilder.append(value.toString());
        }
        return itemBuilder.toString();
    }

    @Override
    public void print(PrintStream printStream, int indent) {
        printStream.print(print());
    }
}
