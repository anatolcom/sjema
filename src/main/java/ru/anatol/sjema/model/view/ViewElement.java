package ru.anatol.sjema.model.view;

import ru.anatol.sjema.producer.model.temp.TempIdentifier;

public class ViewElement {

    /**
     * Промежуточный идентификатор.
     */
    private TempIdentifier identifier;
    /**
     * Путь элемента - как правило соответсвует имени тега, который описывает элемент.
     */
    private String path;
    /**
     * Создавать путь, даже если значение отсутствует.
     */
    private boolean createEmpty = ViewConst.ELEMENT_CREATE_EMPTY_DEFAULT;
    /**
     * Идентификатор типа - ссылка на тип.
     */
    private String typeId;
    /**
     * Промежуточный идентификатор.
     */
    private TempIdentifier typeIdentifier;
    /**
     * Название элемента отображаемое пользователю.
     */
    private String caption;
    /**
     * Расширенное описание поля.
     */
    private String description;
    /**
     * Обязательность заполнения данных.
     */
    private boolean required = ViewConst.ELEMENT_REQUIRED_DEFAULT;
    /**
     * Описание повторяемости елемента.
     */
    private ViewElementRepeatable repeatable;
    /**
     * Значение элемента по умолчанию.
     */
    private String defaultValue;
    /**
     * Доступность для редактирования.
     */
    private boolean readOnly = ViewConst.ELEMENT_READ_ONLY_DEFAULT;

    public ViewElement() {
    }

    public ViewElement(ViewElement element) {
        this.identifier = element.identifier;
        this.path = element.path;
        this.createEmpty = element.createEmpty;
        this.typeId = element.typeId;
        this.typeIdentifier = element.typeIdentifier;
        this.caption = element.caption;
        this.description = element.description;
        this.required = element.required;
        this.repeatable = element.repeatable;
        this.defaultValue = element.defaultValue;
        this.readOnly = element.readOnly;
    }

    public TempIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(TempIdentifier identifier) {
        this.identifier = identifier;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCreateEmpty() {
        return createEmpty;
    }

    public void setCreateEmpty(boolean createEmpty) {
        this.createEmpty = createEmpty;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public TempIdentifier getTypeIdentifier() {
        return typeIdentifier;
    }

    public void setTypeIdentifier(TempIdentifier typeIdentifier) {
        this.typeIdentifier = typeIdentifier;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public ViewElementRepeatable getRepeatable() {
        return repeatable;
    }

    public void setRepeatable(ViewElementRepeatable repeatable) {
        this.repeatable = repeatable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

}
