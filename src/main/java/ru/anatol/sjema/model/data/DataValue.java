package ru.anatol.sjema.model.data;

import ru.anatol.sjema.model.BaseType;

public class DataValue {

    private final BaseType baseType;
    private String value;

    public DataValue(BaseType baseType, String value) {
        this.baseType = baseType;
        this.value = value;
    }

    public BaseType getBaseType() {
        return baseType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
