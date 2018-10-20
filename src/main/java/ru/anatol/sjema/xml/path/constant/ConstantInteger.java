package ru.anatol.sjema.xml.path.constant;

public class ConstantInteger implements Constant {

    private final int value;

    public ConstantInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
