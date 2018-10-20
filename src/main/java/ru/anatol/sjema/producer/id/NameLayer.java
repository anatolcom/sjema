package ru.anatol.sjema.producer.id;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class NameLayer {

    private Map<String, IdLayer> map = new HashMap<>();

    public IdLayer getIdLayer(String name) {
        //TODO нужно ли учитывать регистр ???
        return map.get(name.toLowerCase());
    }

    public void regIdLayer(String name, IdLayer idLayer) {
        //TODO нужно ли учитывать регистр ???
        map.put(name.toLowerCase(), idLayer);
    }

    public Set<String> getNameSet() {
        return map.keySet();
    }
}
