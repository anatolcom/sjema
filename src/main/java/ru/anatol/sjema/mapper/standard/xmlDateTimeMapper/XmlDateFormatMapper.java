package ru.anatol.sjema.mapper.standard.xmlDateTimeMapper;

import org.w3c.dom.Node;
import ru.anatol.sjema.mapper.Mapper;
import ru.anatol.sjema.mapper.MapperException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

public class XmlDateFormatMapper implements Mapper {

    public enum Mode {
        DATE(XmlDateFormatConst.PARAM_MODE_DATE),
        TIME(XmlDateFormatConst.PARAM_MODE_TIME),
        DATE_TIME(XmlDateFormatConst.PARAM_MODE_DATE_TIME);

        private final String value;

        Mode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Mode getByValue(String value) {
            for (Mode mode : Mode.values()) {
                if (mode.value.equals(value)) {
                    return mode;
                }
            }
            throw new IllegalArgumentException("unknown mode value \"" + value + "\"");
        }
    }

    private final Mode mode;
    private final TimeZone timeZone;

    public XmlDateFormatMapper(Mode mode) {
        this.mode = mode;
        if (Mode.DATE.equals(mode)) {
            this.timeZone = null;
        } else {
            this.timeZone = TimeZone.getDefault();
        }
    }

    public XmlDateFormatMapper(Mode mode, int timeZoneRawOffset) {
        this.mode = mode;
        this.timeZone = new SimpleTimeZone(timeZoneRawOffset, "RawOffset" + timeZoneRawOffset);
    }

    public XmlDateFormatMapper(Mode mode, String timeZoneId) {
        this.mode = mode;
        this.timeZone = TimeZone.getTimeZone(timeZoneId);
    }

    @Override
    public String getValue(Node node) throws MapperException {
        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(node.getTextContent());
            return Long.toString(xmlGregorianCalendar.toGregorianCalendar().getTimeInMillis());
        } catch (DatatypeConfigurationException ex) {
            throw new MapperException("parsing date failure, because: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void setValue(Node node, String value) throws MapperException {
        try {
            XMLGregorianCalendar xmlGregorianCalendar = getXMLGregorianCalendar(Long.parseLong(value), mode);
            node.setTextContent(xmlGregorianCalendar.toXMLFormat());
        } catch (DatatypeConfigurationException ex) {
            throw new MapperException("parsing long value \"" + value + "\" failure, because: " + ex.getMessage(), ex);
        }
    }

    private int getTimezone() {
        if (timeZone == null) {
            return DatatypeConstants.FIELD_UNDEFINED;
        }
        return timeZone.getRawOffset() / 1000 / 60;
    }

    private XMLGregorianCalendar getXMLGregorianCalendar(Long millis, Mode mode) throws DatatypeConfigurationException {
        switch (mode) {
            case DATE:
                return getXMLGregorianCalendarDate(millis);
            case TIME:
                return getXMLGregorianCalendarTime(millis);
            case DATE_TIME:
                return getXMLGregorianCalendarDateTime(millis);
            default:
                throw new UnsupportedOperationException("unknown mode: " + mode);
        }
    }

    private XMLGregorianCalendar getXMLGregorianCalendarDate(Long millis) throws DatatypeConfigurationException {
        final GregorianCalendar calendar = createGregorianCalendar(millis);
        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(calendar.get(YEAR), calendar.get(MONTH) + 1, calendar.get(DAY_OF_MONTH), getTimezone());
    }

    private XMLGregorianCalendar getXMLGregorianCalendarTime(Long millis) throws DatatypeConfigurationException {
        final GregorianCalendar calendar = createGregorianCalendar(millis);
        return DatatypeFactory.newInstance().newXMLGregorianCalendarTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(MINUTE), calendar.get(SECOND), calendar.get(MILLISECOND), getTimezone());
    }

    private XMLGregorianCalendar getXMLGregorianCalendarDateTime(Long millis) throws DatatypeConfigurationException {
        final GregorianCalendar calendar = createGregorianCalendar(millis);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

    private GregorianCalendar createGregorianCalendar(Long millis) {
        final GregorianCalendar calendar;
        if (timeZone == null) {
            calendar = new GregorianCalendar();
        } else {
            calendar = new GregorianCalendar(timeZone);
        }
        calendar.setTimeInMillis(millis);
        return calendar;
    }
}
