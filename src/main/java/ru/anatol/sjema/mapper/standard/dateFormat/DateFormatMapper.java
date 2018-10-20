package ru.anatol.sjema.mapper.standard.dateFormat;

import org.w3c.dom.Node;
import ru.anatol.sjema.mapper.Mapper;
import ru.anatol.sjema.mapper.MapperException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class DateFormatMapper implements Mapper {

    private final SimpleDateFormat format;

    public DateFormatMapper(String format) {
        this.format = new SimpleDateFormat(format);
    }

    public DateFormatMapper(String format, int timeZoneRawOffset) {
        this.format = new SimpleDateFormat(format);
        this.format.setTimeZone(new SimpleTimeZone(timeZoneRawOffset, "RawOffset" + timeZoneRawOffset));
    }

    public DateFormatMapper(String format, String timeZoneId) {
        this.format = new SimpleDateFormat(format);
        this.format.setTimeZone(TimeZone.getTimeZone(timeZoneId));
    }

    @Override
    public String getValue(Node node) throws MapperException {
        try {
            final Date date = format.parse(node.getTextContent());
            return Long.toString(date.getTime());
        } catch (ParseException ex) {
            throw new MapperException("parsing date with format \"" + format.toPattern() + "\" failure, because: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void setValue(Node node, String value) throws MapperException {
        try {
            final Date date = new Date(Long.parseLong(value));
            node.setTextContent(format.format(date));
        } catch (NumberFormatException ex) {
            throw new MapperException("parsing long value \"" + value + "\" failure, because: " + ex.getMessage(), ex);
        }
    }

}
