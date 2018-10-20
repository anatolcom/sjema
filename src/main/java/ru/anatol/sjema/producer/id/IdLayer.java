package ru.anatol.sjema.producer.id;

public class IdLayer {

    private final String id;
    private final Object object;

    public IdLayer(String id, Object object) {
        this.id = id;
        this.object = object;
    }

    public String getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }
}
