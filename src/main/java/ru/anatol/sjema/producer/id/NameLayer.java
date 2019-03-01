package ru.anatol.sjema.producer.id;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class NameLayer {

    private Map<String, IdLayer> map = new HashMap<>();

    public IdLayer getIdLayer(String name) {
        return map.get(name);
    }

    public void regIdLayer(String name, IdLayer idLayer) {
        map.put(name, idLayer);
    }

    public Set<String> getNameSet() {
        return map.keySet();
    }
}
