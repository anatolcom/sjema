package ru.anatol.sjema.producer;

import ru.anatol.sjema.model.BaseType;
import ru.anatol.sjema.model.view.ViewMapper;

public interface ViewMapperProducer {

    ViewMapper produce(String namespace, String typeId) throws ProducerException;

    BaseType getBaseType(String namespace, String typeId);

}
