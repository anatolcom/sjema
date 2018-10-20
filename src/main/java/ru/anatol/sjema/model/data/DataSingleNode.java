package ru.anatol.sjema.model.data;


public class DataSingleNode implements DataItem {

    private final String name;
    private DataNode node;

    public DataSingleNode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public DataNode getNode() {
        return node;
    }

    public void setNode(DataNode node) {
        this.node = node;
    }
}
