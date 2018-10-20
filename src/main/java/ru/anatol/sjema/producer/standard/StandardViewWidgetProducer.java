package ru.anatol.sjema.producer.standard;

import ru.anatol.sjema.model.BaseType;
import ru.anatol.sjema.model.view.ViewWidget;
import ru.anatol.sjema.producer.ViewWidgetProducer;
import ru.anatol.sjema.producer.XsdConst;

public class StandardViewWidgetProducer implements ViewWidgetProducer {

    @Override
    public ViewWidget produce(String namespace, String typeId) {
        if (XsdConst.URI.equals(namespace)) {
            if (XsdConst.DATE_TIME_TYPE_ID.equals(typeId)) {
                return new ViewWidget(typeId);
            }
            if (XsdConst.DATE_TYPE_ID.equals(typeId)) {
                return new ViewWidget(typeId);
            }
            if (XsdConst.TIME_TYPE_ID.equals(typeId)) {
                return new ViewWidget(typeId);
            }
        }
        return null;
    }
}
