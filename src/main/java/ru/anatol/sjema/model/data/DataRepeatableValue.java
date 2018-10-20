package ru.anatol.sjema.model.data;


import java.util.ArrayList;
import java.util.List;

public class DataRepeatableValue implements DataItem {

    private final String name;
    private final List<DataValue> values;

    public DataRepeatableValue(String name) {
        this.name = name;
        this.values = new ArrayList<>();
    }

    public DataRepeatableValue(String name, int size) {
        this.name = name;
        this.values = new ArrayList<>(size);
    }

    @Override
    public String getName() {
        return name;
    }


    public List<DataValue> getValues() {
        return values;
    }
}
