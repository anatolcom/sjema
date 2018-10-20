package ru.anatol.sjema.mapper.standard.xmlDateTimeMapper;


import ru.anatol.sjema.mapper.Mapper;
import ru.anatol.sjema.mapper.MapperException;
import ru.anatol.sjema.mapper.MapperParams;
import ru.anatol.sjema.mapper.Producer;

public class XmlDateFormatProducer implements Producer {

    @Override
    public String name() {
        return XmlDateFormatConst.NAME;
    }

    @Override
    public Mapper produce(MapperParams params) throws MapperException {
        XmlDateFormatMapper.Mode mode = XmlDateFormatMapper.Mode.getByValue(params.getString(XmlDateFormatConst.PARAM_MODE, true));
        Integer rawOffset = params.getInt(XmlDateFormatConst.PARAM_TIME_ZONE_RAW_OFFSET, false);
        String timeZoneId = params.getString(XmlDateFormatConst.PARAM_TIME_ZONE_ID, false);

        if (rawOffset != null) {
            return new XmlDateFormatMapper(mode, rawOffset);
        }
        if (timeZoneId != null) {
            return new XmlDateFormatMapper(mode, timeZoneId);
        }
        return new XmlDateFormatMapper(mode);
    }

}
