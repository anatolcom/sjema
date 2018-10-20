package ru.anatol.sjema.mapper.standard.dateFormat;


import ru.anatol.sjema.mapper.Mapper;
import ru.anatol.sjema.mapper.MapperException;
import ru.anatol.sjema.mapper.MapperParams;
import ru.anatol.sjema.mapper.Producer;

public class DateFormatProducer implements Producer {

    @Override
    public String name() {
        return DateFormatConst.NAME;
    }

    @Override
    public Mapper produce(MapperParams params) throws MapperException {
        String format = params.getString(DateFormatConst.PARAM_FORMAT, true);
        Integer rawOffset = params.getInt(DateFormatConst.PARAM_TIME_ZONE_RAW_OFFSET, false);
        String timeZoneId = params.getString(DateFormatConst.PARAM_TIME_ZONE_ID, false);

        if (rawOffset != null) {
            return new DateFormatMapper(format, rawOffset);
        }
        if (timeZoneId != null) {
            return new DateFormatMapper(format, timeZoneId);
        }
        return new DateFormatMapper(format);
    }

}
