package ru.anatol.sjema.producer;

import java.util.HashSet;
import java.util.Set;

public final class XsdConst {

    private XsdConst() {
    }

    public static final String PREFIX = "xsd";

    public static final String URI = "http://www.w3.org/2001/XMLSchema";

    /*
        https://www.w3.org/TR/xmlschema-2/#primitive-vs-derived

        3.2 Primitive datatypes
        3.2.1 string
        3.2.2 boolean
        3.2.3 decimal
        3.2.4 float
        3.2.5 double
        3.2.6 duration
        3.2.7 dateTime
        3.2.8 time
        3.2.9 date
        3.2.10 gYearMonth
        3.2.11 gYear
        3.2.12 gMonthDay
        3.2.13 gDay
        3.2.14 gMonth
        3.2.15 hexBinary
        3.2.16 base64Binary
        3.2.17 anyURI
        3.2.18 QName
        3.2.19 NOTATION
    */


    /**
     * Базовый тип для всех простых типов.
     */
    public static final String BASE_TYPE_ID = "anySimpleType";
    /**
     * Логический тип.
     */
    public static final String BOOLEAN_TYPE_ID = "boolean";
    /**
     * Строковый тип.
     */
    public static final String STRING_TYPE_ID = "string";

    /**
     * Число со знаком размером в 1 байт.
     */
    public static final String BYTE_TYPE_ID = "byte";
    /**
     * Число со знаком размером в 2 байта.
     */
    public static final String SHORT_TYPE_ID = "short";
    /**
     * Число со знаком размером в 4 байта.
     */
    public static final String INT_TYPE_ID = "int";
    /**
     * Число со знаком размером в 8 байт.
     */
    public static final String LONG_TYPE_ID = "long";

    /**
     * Число без знака размером в 1 байт.
     */
    public static final String UNSIGNED_BYTE_TYPE_ID = "unsignedByte";
    /**
     * Число без знака размером в 2 байта.
     */
    public static final String UNSIGNED_SHORT_TYPE_ID = "unsignedShort";
    /**
     * Число без знака размером в 4 байта.
     */
    public static final String UNSIGNED_INT_TYPE_ID = "unsignedInt";
    /**
     * Число без знака размером в 8 байт.
     */
    public static final String UNSIGNED_LONG_TYPE_ID = "unsignedLong";

    /**
     * Целое со знаком.
     * -9223372036854775808 - 9223372036854775807
     */
    public static final String INTEGER_TYPE_ID = "integer";
    /**
     * Положительное целое.
     * 1 - 9223372036854775807
     */
    public static final String POSITIVE_INTEGER_TYPE_ID = "positiveInteger";
    /**
     * Отрицательное целое.
     * -9223372036854775808 - -1
     */
    public static final String NEGATIVE_INTEGER_TYPE_ID = "negativeInteger";
    /**
     * Положительное целое включая 0.
     * 0 - 9223372036854775807
     */
    public static final String NON_NEGATIVE_INTEGER_TYPE_ID = "nonNegativeInteger";
    /**
     * Отрицательное целое включая 0.
     * -9223372036854775808 - 0
     */
    public static final String NON_POSITIVE_INTEGER_TYPE_ID = "nonPositiveInteger";

    /**
     * Число с плавающей запятой размером в 2 байта.
     */
    public static final String FLOAT_TYPE_ID = "float";
    /**
     * Число с плавающей запятой размером в 4 байт.
     */
    public static final String DOUBLE_TYPE_ID = "double";

    /**
     * Число со знаком.
     */
    public static final String DECIMAL_TYPE_ID = "decimal";
    /**
     * Денежнй тип.
     */
    public static final String CURRENCY_TYPE_ID = "currency";

    /**
     * Продолжительность.
     */
    public static final String DURATION_TYPE_ID = "duration";
    /**
     * Время.
     */
    public static final String TIME_TYPE_ID = "time";
    /**
     * Дата.
     */
    public static final String DATE_TYPE_ID = "date";
    /**
     * Дата и время.
     */
    public static final String DATE_TIME_TYPE_ID = "dateTime";
    /**
     * Год.
     */
    public static final String G_YEAR_TYPE_ID = "gYear";
    /**
     * Год и месяц.
     */
    public static final String G_YEAR_MONTH_TYPE_ID = "gYearMonth";
    /**
     * Месяц.
     */
    public static final String G_MONTH_TYPE_ID = "gMonth";
    /**
     * Месяц и день.
     */
    public static final String G_MONTH_DAY_TYPE_ID = "gMonthDay";
    /**
     * День.
     */
    public static final String G_DAY_TYPE_ID = "gDay";
    /**
     * Данные в формате URI.
     */
    public static final String ANY_URI_TYPE_ID = "anyURI";
    /**
     * Бинарные данные в шеснадцатиричном формате.
     */
    public static final String HEX_BINARY_TYPE_ID = "hexBinary";

    public static final String NORMALIZED_STRING_ID = "normalizedString";
    public static final String TOKEN_TYPE_ID = "token";
    public static final String LANGUAGE_TYPE_ID = "language";

    public static final String ID_TYPE_ID = "ID";

    public static final String NAME_TYPE_ID = "Name";
    public static final String NC_NAME_TYPE_ID = "NCName";


    /**
     * Бинарные данные в формате Base64.
     */
    public static final String BASE_64_BINARY_TYPE_ID = "base64Binary";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ssXXX";

    public static final Set<String> typeIdSet;
    public static final Set<String> numberTypeIdSet;

    static {
        typeIdSet = getTypeIdSet();
        numberTypeIdSet = getNumberTypeIdSet();
    }

    private static Set<String> getTypeIdSet() {
        Set<String> set = new HashSet<>();
        set.add(BOOLEAN_TYPE_ID);

        set.add(STRING_TYPE_ID);

        set.add(BYTE_TYPE_ID);
        set.add(SHORT_TYPE_ID);
        set.add(INT_TYPE_ID);
        set.add(LONG_TYPE_ID);

        set.add(UNSIGNED_BYTE_TYPE_ID);
        set.add(UNSIGNED_SHORT_TYPE_ID);
        set.add(UNSIGNED_INT_TYPE_ID);
        set.add(UNSIGNED_LONG_TYPE_ID);

        set.add(POSITIVE_INTEGER_TYPE_ID);
        set.add(NEGATIVE_INTEGER_TYPE_ID);
        set.add(NON_NEGATIVE_INTEGER_TYPE_ID);
        set.add(NON_POSITIVE_INTEGER_TYPE_ID);

        set.add(FLOAT_TYPE_ID);
        set.add(DOUBLE_TYPE_ID);

        set.add(INTEGER_TYPE_ID);
        set.add(DECIMAL_TYPE_ID);
        set.add(CURRENCY_TYPE_ID);

        set.add(DURATION_TYPE_ID);

        set.add(TIME_TYPE_ID);
        set.add(DATE_TYPE_ID);
        set.add(DATE_TIME_TYPE_ID);

        set.add(G_YEAR_TYPE_ID);
        set.add(G_YEAR_MONTH_TYPE_ID);
        set.add(G_MONTH_TYPE_ID);
        set.add(G_MONTH_DAY_TYPE_ID);
        set.add(G_DAY_TYPE_ID);

        set.add(ANY_URI_TYPE_ID);

        set.add(HEX_BINARY_TYPE_ID);
        set.add(BASE_64_BINARY_TYPE_ID);

        set.add(NORMALIZED_STRING_ID);
        set.add(TOKEN_TYPE_ID);
        set.add(LANGUAGE_TYPE_ID);

        set.add(ID_TYPE_ID);

        set.add(NAME_TYPE_ID);
        set.add(NC_NAME_TYPE_ID);

        return set;
    }

    private static Set<String> getNumberTypeIdSet() {
        Set<String> set = new HashSet<>();

        set.add(BYTE_TYPE_ID);
        set.add(SHORT_TYPE_ID);
        set.add(INT_TYPE_ID);
        set.add(LONG_TYPE_ID);

        set.add(UNSIGNED_BYTE_TYPE_ID);
        set.add(UNSIGNED_SHORT_TYPE_ID);
        set.add(UNSIGNED_INT_TYPE_ID);
        set.add(UNSIGNED_LONG_TYPE_ID);

        set.add(POSITIVE_INTEGER_TYPE_ID);
        set.add(NEGATIVE_INTEGER_TYPE_ID);
        set.add(NON_NEGATIVE_INTEGER_TYPE_ID);
        set.add(NON_POSITIVE_INTEGER_TYPE_ID);

        set.add(FLOAT_TYPE_ID);
        set.add(DOUBLE_TYPE_ID);

        set.add(INTEGER_TYPE_ID);
        set.add(DECIMAL_TYPE_ID);

        return set;
    }

    public static boolean isBooleanTypeId(String typeId) {
        return XsdConst.BOOLEAN_TYPE_ID.equals(typeId);
    }

    public static boolean isStringTypeId(String typeId) {
        return XsdConst.STRING_TYPE_ID.equals(typeId);
    }

    public static boolean isNumberTypeId(String typeId) {
        return numberTypeIdSet.contains(typeId);
    }

    public static boolean isDateTimeTypeId(String typeId) {
        return XsdConst.DATE_TIME_TYPE_ID.equals(typeId);
    }

    public static boolean isDateTypeId(String typeId) {
        return XsdConst.DATE_TYPE_ID.equals(typeId);
    }

    public static boolean isTimeTypeId(String typeId) {
        return XsdConst.TIME_TYPE_ID.equals(typeId);
    }

}
