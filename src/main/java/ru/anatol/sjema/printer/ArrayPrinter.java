package ru.anatol.sjema.printer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArrayPrinter implements Printer {

    private static class Entry {
        private final Printer value;

        public Entry(Printer value) {
            this.value = value;
        }

        public Printer getValue() {
            return value;
        }
    }

    private List<Entry> entries = new ArrayList<>();

    private List<String> comments = new ArrayList<>();

    public ArrayPrinter() {
    }

    public ArrayPrinter(Collection<String> collection) {
        if (collection == null) {
            return;
        }
        for (String item : collection) {
            entries.add(new Entry(new StringPrinter(item)));
        }
    }

    public void put(Printer value) {
        entries.add(new Entry(value));
    }

    public void put(String value) {
        entries.add(new Entry(new StringPrinter(value)));
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    @Override
    public String print() {
        StringBuilder itemBuilder = new StringBuilder();
        itemBuilder.append("[");
        for (Entry entry : entries) {
            itemBuilder.append(entry.getValue().print());
        }
        itemBuilder.append("]");
        return itemBuilder.toString();
    }

    @Override
    public void print(PrintStream printStream, int indent) {
        printStream.println("[");

        int last = entries.size() - 1;
        for (int q = 0; q <= last; q++) {
            Entry entry = entries.get(q);

            printStream.print(TempModelPrinter.indent(indent + 1));
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
        printStream.print("]");
    }

}
