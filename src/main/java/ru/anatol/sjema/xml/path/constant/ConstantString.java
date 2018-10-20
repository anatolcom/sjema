package ru.anatol.sjema.xml.path.constant;

public class ConstantString implements Constant {

    private final String value;

    public ConstantString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
