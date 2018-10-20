package ru.anatol.sjema.producer.id;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class NamespaceLayer {

    private Map<String, NameLayer> map = new HashMap<>();

    public NameLayer getNameLayer(String namespace) {
        return map.get(namespace);
    }

    public NameLayer getOrRegNameLayer(String namespace) {
        NameLayer nameLayer = map.get(namespace);
        if (nameLayer == null) {
            nameLayer = new NameLayer();
            map.put(namespace, nameLayer);
        }
        return nameLayer;
    }

    public Set<String> getNamespaceSet() {
        return map.keySet();
    }
}
