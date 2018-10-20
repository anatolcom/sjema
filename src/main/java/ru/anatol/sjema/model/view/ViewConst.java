package ru.anatol.sjema.model.view;

public class ViewConst {

    public static final String UNBOUNDED = "unbounded";
    public static final int UNBOUNDED_VALUE = Integer.MAX_VALUE;

    public static final String VERSION = "version";
    public static final String VERSION_VALUE = "1.0";
    public static final String CAPTION = "caption";
    public static final String DESCRIPTION = "description";

    public static final String NAMESPACES = "namespaces";
    public static final String TARGET_NAMESPACE = "targetNamespace";

    public static final String STRUCTURE = "structure";

    public static final String ELEMENTS = "elements";
    public static final String ELEMENT_PATH = "path";
    public static final String ELEMENT_CREATE_EMPTY = "createEmpty";
    public static final boolean ELEMENT_CREATE_EMPTY_DEFAULT = false;
    public static final String ELEMENT_TYPE_ID = "typeId";
    public static final String ELEMENT_CAPTION = "caption";
    public static final String ELEMENT_DESCRIPTION = "description";
    public static final String ELEMENT_REQUIRED = "required";
    public static final boolean ELEMENT_REQUIRED_DEFAULT = false;
    public static final String ELEMENT_REPEATABLE = "repeatable";
    public static final String ELEMENT_REPEATABLE_PATH = "path";
    public static final String ELEMENT_REPEATABLE_MIN = "min";
    public static final int ELEMENT_REPEATABLE_MIN_DEFAULT = 0;
    public static final String ELEMENT_REPEATABLE_MAX = "max";
    public static final int ELEMENT_REPEATABLE_MAX_DEFAULT = UNBOUNDED_VALUE;
    public static final String ELEMENT_REPEATABLE_CAPTION = "caption";
    public static final String ELEMENT_REPEATABLE_ADD_CAPTION = "addCaption";
    public static final String ELEMENT_REPEATABLE_REMOVE_CAPTION = "removeCaption";
    public static final String ELEMENT_READ_ONLY = "readOnly";
    public static final boolean ELEMENT_READ_ONLY_DEFAULT = false;
    public static final String ELEMENT_DEFAULT_VALUE = "default";

    public static final String TYPES = "types";
    public static final String TYPE_DESCRIPTION = "description";
    public static final String TYPE_RESTRICTION = "restriction";
    public static final String TYPE_BASE = "base";
    public static final String TYPE_RESTRICTION_ENUMERATION = "enumeration";
    public static final String TYPE_RESTRICTION_PATTERNS = "patterns";
    public static final String TYPE_RESTRICTION_PATTERNS_PATTERN = "pattern";
    public static final String TYPE_RESTRICTION_PATTERNS_DESCRIPTION = "description";
    public static final String TYPE_RESTRICTION_WHITE_SPACE = "whiteSpace";
    public static final String TYPE_RESTRICTION_LENGTH = "length";
    public static final String TYPE_RESTRICTION_MIN_LENGTH = "minLength";
    public static final String TYPE_RESTRICTION_MAX_LENGTH = "maxLength";
    public static final String TYPE_RESTRICTION_MAX_INCLUSIVE = "maxInclusive";
    public static final String TYPE_RESTRICTION_MIN_INCLUSIVE = "minInclusive";
    public static final String TYPE_RESTRICTION_MIN_EXCLUSIVE = "minExclusive";
    public static final String TYPE_RESTRICTION_MAX_EXCLUSIVE = "maxExclusive";
    public static final String TYPE_RESTRICTION_TOTAL_DIGITS = "totalDigits";
    public static final String TYPE_RESTRICTION_FRACTION_DIGITS = "fractionDigits";
    public static final String TYPE_WIDGET = "widget";
    public static final String TYPE_WIDGET_NAME = "name";
    public static final String TYPE_WIDGET_PARAMS = "params";

    public static final String TYPE_MAPPER_ID = "mapperId";
    public static final String TYPE_CONTENT = "content";
    public static final String TYPE_CONTENT_MODE = "mode";
    public static final String TYPE_CONTENT_MODE_SEQUENCE = "sequence";
    public static final String TYPE_CONTENT_MODE_CHOICE = "choice";
    public static final String TYPE_CONTENT_ELEMENT_IDS = "elementIds";
    public static final String TYPE_CONTENT_XML_ORDER = "xmlOrder";

    public static final String MAPPERS = "mappers";
    public static final String MAPPER_CLASS = "class";
    public static final String MAPPER_PARAMS = "params";

    public static final String VALIDATION = "validation";
    public static final String VALIDATION_RULE = "rule";
    public static final String VALIDATION_MESSAGE = "message";

    private ViewConst() {
    }

    /**
     * Получение числового значения min.
     *
     * @param min строковое значение
     * @return числовое значение
     */
    public static Integer getMin(Object min) {
        if (min == null) {
            return ELEMENT_REPEATABLE_MIN_DEFAULT;
        }
        if (min instanceof Number) {
            return ((Number) min).intValue();
        }
        return Integer.parseUnsignedInt((String) min);
    }

    /**
     * Получение числового значения max.
     *
     * @param max строковое значение
     * @return числовое значение
     */
    public static int getMax(Object max) {
        if (max == null) {
            return ELEMENT_REPEATABLE_MAX_DEFAULT;
        }
        if (max instanceof Number) {
            return ((Number) max).intValue();
        }
        if (ViewConst.UNBOUNDED.equals(max)) {
            return UNBOUNDED_VALUE;
        }
        return Integer.parseUnsignedInt((String) max);
    }

    /**
     * Получение числового значения minOccurs.
     *
     * @param minOccurs строковое значение
     * @return числовое значение
     */
    public static int getMinOccurs(String minOccurs) {
        if (minOccurs == null) {
            return 1;
        }
        return Integer.parseUnsignedInt(minOccurs);
    }

    /**
     * Получение числового значения maxOccurs.
     *
     * @param maxOccurs строковое значение
     * @return числовое значение
     */
    public static int getMaxOccurs(String maxOccurs) {
        if (maxOccurs == null) {
            return 1;
        }
        if (ViewConst.UNBOUNDED.equals(maxOccurs)) {
            return Integer.MAX_VALUE;
        }
        return Integer.parseUnsignedInt(maxOccurs);
    }

    /**
     * Получение логического значения createEmpty.
     *
     * @param required объект
     * @return логическое значение
     */
    public static boolean getCreateEmpty(Boolean required) {
        if (required == null) {
            return ViewConst.ELEMENT_CREATE_EMPTY_DEFAULT;
        }
        return required.booleanValue();
    }

    /**
     * Получение логического значения required.
     *
     * @param required объект
     * @return логическое значение
     */
    public static boolean getRequired(Boolean required) {
        if (required == null) {
            return ViewConst.ELEMENT_REQUIRED_DEFAULT;
        }
        return required.booleanValue();
    }

    /**
     * Получение логическое значения readOnly.
     *
     * @param readOnly объект
     * @return логическое значение
     */
    public static boolean getReadOnly(Boolean readOnly) {
        if (readOnly == null) {
            return ViewConst.ELEMENT_READ_ONLY_DEFAULT;
        }
        return readOnly.booleanValue();
    }


}
