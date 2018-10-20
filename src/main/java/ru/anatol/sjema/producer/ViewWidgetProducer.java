package ru.anatol.sjema.producer;

import ru.anatol.sjema.model.BaseType;
import ru.anatol.sjema.model.view.ViewMapper;
import ru.anatol.sjema.model.view.ViewWidget;

public interface ViewWidgetProducer {

    ViewWidget produce(String namespace, String typeId) throws ProducerException;

}
