package ru.anatol.sjema.producer.id;

import ru.anatol.sjema.producer.model.temp.TempIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class ModeLayer {

    private Map<TempIdentifier.Mode, NamespaceLayer> map = new HashMap<>();

    public NamespaceLayer getNamespaceLayer(TempIdentifier.Mode mode) {
        return map.get(mode);
    }

    public NamespaceLayer getOrRegNamespaceLayer(TempIdentifier.Mode mode) {
        NamespaceLayer namespaceLayer = map.get(mode);
        if (namespaceLayer == null) {
            namespaceLayer = new NamespaceLayer();
            map.put(mode, namespaceLayer);
        }
        return namespaceLayer;
    }

    public Set<TempIdentifier.Mode> getModeSet() {
        return map.keySet();
    }

}
