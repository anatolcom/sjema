package ru.anatol.sjema.printer;

import java.io.PrintStream;

public interface Printer {

    String print();

    void print(PrintStream printStream, int indent);

}
