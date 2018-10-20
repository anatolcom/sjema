package ru.anatol.sjema.xml.path.constant;

public class ConstantNodeName implements Constant {

    private final String prefix;
    private final String name;

    public ConstantNodeName(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

}
