package ru.anatol.sjema.mapper;

import ru.anatol.sjema.model.view.ViewMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapperManager {

    private final Map<String, Producer> producerMap = new HashMap<>();

    public MapperManager() {
    }

    public void registration(Producer producer) {
        producerMap.put(producer.name(), producer);
    }

    public Set<String> getNames() {
        return producerMap.keySet();
    }

    public Mapper getMapper(ViewMapper viewMapper) throws MapperException {
        final Producer producer = producerMap.get(viewMapper.getClassName());
        if (producer == null) {
            throw new MapperException("mapper with name \"" + viewMapper.getClassName() + "\" not found");
        }
        final MapperParams mapperParams = new MapperParams(viewMapper.getParams().getMap());
        return producer.produce(mapperParams);
    }

}
