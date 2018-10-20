package ru.anatol.sjema.model.data;


public class DataSingleValue implements DataItem {

    private final String name;
    private DataValue value;

    public DataSingleValue(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public DataValue getValue() {
        return value;
    }

    public void setValue(DataValue value) {
        this.value = value;
    }
}
