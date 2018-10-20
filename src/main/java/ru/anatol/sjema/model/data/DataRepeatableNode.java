package ru.anatol.sjema.model.data;


import java.util.ArrayList;
import java.util.List;

public class DataRepeatableNode implements DataItem {

    private final String name;
    private final List<DataNode> nodes;

    public DataRepeatableNode(String name) {
        this.name = name;
        this.nodes = new ArrayList<>();
    }

    public DataRepeatableNode(String name, int size) {
        this.name = name;
        this.nodes = new ArrayList<>(size);
    }

    @Override
    public String getName() {
        return name;
    }

    public List<DataNode> getNodes() {
        return nodes;
    }

}
