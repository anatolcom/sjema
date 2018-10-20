package ru.anatol.sjema.printer;

import org.json.JSONObject;

import java.io.PrintStream;

public class StringPrinter implements Printer {

    private String value;

    public StringPrinter() {
    }

    public StringPrinter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String print() {
        StringBuilder itemBuilder = new StringBuilder();
        if (value == null) {
            itemBuilder.append("null");
        } else {
//            itemBuilder.append("\"").append().append("\"");
            itemBuilder.append(normalize(value));
        }
        return itemBuilder.toString();
    }

    private String normalize(String value) {
        return JSONObject.quote(value);
    }

    @Override
    public void print(PrintStream printStream, int indent) {
        printStream.print(print());
    }
}
