package ru.anatol.sjema.model.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataNode implements Iterable<DataItem> {

    private final List<DataItem> items;

    public DataNode() {
        this.items = new ArrayList<>();
    }

    public DataNode(int size) {
        this.items = new ArrayList<>(size);
    }

    public List<DataItem> getItems() {
        return items;
    }

    public void add(DataItem item) {
        items.add(item);
    }

    @Override
    public Iterator<DataItem> iterator() {
        return items.iterator();
    }
}
