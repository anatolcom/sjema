package ru.anatol.sjema.producer.standard;

import ru.anatol.sjema.mapper.standard.xmlDateTimeMapper.XmlDateFormatConst;
import ru.anatol.sjema.model.BaseType;
import ru.anatol.sjema.model.view.ViewMapper;
import ru.anatol.sjema.model.view.ViewMapperParams;
import ru.anatol.sjema.producer.ViewMapperProducer;
import ru.anatol.sjema.producer.XsdConst;

public class StandardViewMapperProducer implements ViewMapperProducer {

    @Override
    public ViewMapper produce(String namespace, String typeId) {
        if (XsdConst.URI.equals(namespace)) {
            if (XsdConst.DATE_TIME_TYPE_ID.equals(typeId)) {
                return produceDateTimeMapper();
            }
            if (XsdConst.DATE_TYPE_ID.equals(typeId)) {
                return produceDateMapper();
            }
            if (XsdConst.TIME_TYPE_ID.equals(typeId)) {
                return produceTimeMapper();
            }
        }
        return null;
    }

    @Override
    public BaseType getBaseType(String namespace, String typeId) {
        if (XsdConst.URI.equals(namespace)) {
            if (XsdConst.DATE_TIME_TYPE_ID.equals(typeId)) {
                return BaseType.NUMBER;
            }
            if (XsdConst.DATE_TYPE_ID.equals(typeId)) {
                return BaseType.NUMBER;
            }
            if (XsdConst.TIME_TYPE_ID.equals(typeId)) {
                return BaseType.NUMBER;
            }
        }
        return null;
    }

    private ViewMapper produceDateTimeMapper() {
        final ViewMapper dateTimeMapper = new ViewMapper();
        dateTimeMapper.setClassName(XmlDateFormatConst.NAME);
        dateTimeMapper.setParams(new ViewMapperParams());
        dateTimeMapper.getParams().getMap().put(XmlDateFormatConst.PARAM_MODE, XmlDateFormatConst.PARAM_MODE_DATE_TIME);
        return dateTimeMapper;
    }

    private ViewMapper produceDateMapper() {
        final ViewMapper dateMapper = new ViewMapper();
        dateMapper.setClassName(XmlDateFormatConst.NAME);
        dateMapper.setParams(new ViewMapperParams());
        dateMapper.getParams().getMap().put(XmlDateFormatConst.PARAM_MODE, XmlDateFormatConst.PARAM_MODE_DATE);
        return dateMapper;
    }

    private ViewMapper produceTimeMapper() {
        final ViewMapper timeMapper = new ViewMapper();
        timeMapper.setClassName(XmlDateFormatConst.NAME);
        timeMapper.setParams(new ViewMapperParams());
        timeMapper.getParams().getMap().put(XmlDateFormatConst.PARAM_MODE, XmlDateFormatConst.PARAM_MODE_TIME);
        return timeMapper;
    }
}
