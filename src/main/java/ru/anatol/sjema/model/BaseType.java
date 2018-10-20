package ru.anatol.sjema.model;

public enum BaseType {

    BOOLEAN("boolean"),
    NUMBER("number"),
    STRING("string");

    private final String value;

    BaseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BaseType getByValue(String value) {
        for (BaseType base : BaseType.values()) {
            if (base.value.equals(value)) {
                return base;
            }
        }
        throw new IllegalArgumentException("unknown base value \"" + value + "\"");
    }
}
