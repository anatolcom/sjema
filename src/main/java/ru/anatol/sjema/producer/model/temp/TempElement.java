package ru.anatol.sjema.producer.model.temp;


import java.util.List;

public class TempElement extends TempComment {

    public enum Mode {
        ELEMENT,
        ATTRIBUTE,
        ATTRIBUTE_GROUP_REF,
        LIST;
    }

    public enum Form {
        /**
         * Элементы или атрибуты из целевого пространства имен должны быть дополнены префиксом пространства имен.
         */
        QUALIFIED,
        /**
         * Элементы или атрибуты из целевого пространства имен не нужно уточнять префиксом пространства имен.
         */
        UNQUALIFIED;
    }

    private final TempElement.Mode mode;
    /**
     * Уникаьный идентификатор элемента в рамках документа.
     */
    private TempIdentifier id;
    /**
     * Название объекта.
     */
    private TempIdentifier nameId;
    private TempIdentifier refId;
    private TempIdentifier typeId;
    private List<String> annotation;
    private TempElementRestriction restriction;

    /**
     * Корневой элемент;
     */
    private boolean root = false;

    /**
     * Константа.
     * Значение показывает, является ли элемент константой или переменной.
     * true - константа.
     * false - переменная.
     */
    private boolean constant = false;
    /**
     * Значение элемента.
     * если элемент не константа, то это занчение по умолчанию.
     * если элемент константа, то это постоянное значение элемента.
     */
    private String value;
    /**
     * Тип указания значения null.
     */
    private boolean nillable = false;
    /**
     * Абстрактность элемента.
     * <p>
     * Указывает, можно ли применять элемент в документе экземпляра.
     * Если значение равно true, элемент не может присутствовать в документе экземпляра.
     * На месте этого элемента должен быть другой, атрибут substitutionGroup
     * которого содержит квалифицированное имя (QName) данного элемента.
     * Ссылаться на данный элемент через атрибут substitutionGroup могут несколько элементов.
     * Значение по умолчанию false.
     */
    private boolean _abstract = false;

    /**
     * Финальность элемента.
     * <p>
     * Тип наследования.
     * Атрибут final задает значение по умолчанию атрибута final элемента element.
     * Значение может содержать строку #all или список, состоящий из строк extension и restriction.
     * <p>
     * расширение - Предотвращает использование вместо этого элемента элементов, унаследованных по расширению.
     * ограничение - Предотвращает использование вместо этого элемента элементов, унаследованных по ограничению.
     * #all - Предотвращает использование вместо этого элемента всех унаследованных элементов.
     * <p>
     * Запрещено, если содержащим не является элемент schema.
     */
    private List<String> _final;

    /**
     * Форма для элемента .
     * Значением по умолчанию является значение атрибута elementFormDefault элемента schema, содержащего этот атрибут.
     * Значение должно быть одной из следующих строк: «qualified» или «unqualified».
     * Если значение не квалифицировано, элемент не обязательно квалифицировать с помощью префикса пространства имен.
     * Если значение квалифицировано, элемент нужно квалифицировать с помощью префикса пространства имен.
     * Необязательно.
     */
    private Form form;

    public TempElement(TempElement.Mode mode) {
        this.mode = mode;
    }

    public TempElement.Mode getMode() {
        return mode;
    }

    public TempIdentifier getId() {
        return id;
    }

    public void setId(TempIdentifier id) {
        this.id = id;
    }

    public TempIdentifier getNameId() {
        return nameId;
    }

    public void setNameId(TempIdentifier nameId) {
        this.nameId = nameId;
    }

    public TempIdentifier getRefId() {
        return refId;
    }

    public void setRefId(TempIdentifier refId) {
        this.refId = refId;
    }

    public TempIdentifier getTypeId() {
        return typeId;
    }

    public void setTypeId(TempIdentifier typeId) {
        this.typeId = typeId;
    }

    public List<String> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<String> annotation) {
        this.annotation = annotation;
    }

    public TempElementRestriction getRestriction() {
        return restriction;
    }

    public void setRestriction(TempElementRestriction restriction) {
        this.restriction = restriction;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public boolean isAbstract() {
        return _abstract;
    }

    public void setAbstract(boolean _abstract) {
        this._abstract = _abstract;
    }

    public List<String> getFinal() {
        return _final;
    }

    public void setFinal(List<String> _final) {
        this._final = _final;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }
}
