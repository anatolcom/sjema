package ru.anatol.sjema.printer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ObjectPrinter implements Printer {

    private static class Entry {
        private final String key;
        private final Printer value;

        public Entry(String key, Printer value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public Printer getValue() {
            return value;
        }
    }

    private List<Entry> entries = new ArrayList<>();

    private List<String> comments = new ArrayList<>();

    public ObjectPrinter() {
    }

    public void put(String key, Printer value) {
        entries.add(new Entry(key, value));
    }

    public void put(String key, Boolean value) {
        entries.add(new Entry(key, new BooleanPrinter(value)));
    }

    public void put(String key, String value) {
        entries.add(new Entry(key, new StringPrinter(value)));
    }

    public void put(String key, Number value) {
        entries.add(new Entry(key, new NumberPrinter(value)));
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    @Override
    public String print() {
        StringBuilder itemBuilder = new StringBuilder();
        itemBuilder.append("{");
        for (Entry entry : entries) {
            itemBuilder.append(new StringPrinter(entry.getKey()).print()).append(":").append(entry.getValue().print());
        }
        itemBuilder.append("}");
        return itemBuilder.toString();
    }

    @Override
    public void print(PrintStream printStream, int indent) {
        printStream.println("{");
        int last = entries.size() - 1;
        for (int q = 0; q <= last; q++) {
            Entry entry = entries.get(q);

            printStream.print(TempModelPrinter.indent(indent + 1));
            new StringPrinter(entry.getKey()).print(printStream, indent + 1);

            printStream.print(" : ");
            entry.getValue().print(printStream, indent + 1);

            if (q < last) printStream.print(",");

            printStream.println();
        }

        if (!comments.isEmpty()) {
            for (String comment : comments) {
                printStream.print(TempModelPrinter.indent(indent + 1));
                printStream.print("// ");
                printStream.println(comment);
            }
        }

        printStream.print(TempModelPrinter.indent(indent));
        printStream.print("}");
    }

}
