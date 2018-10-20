package ru.anatol.sjema.model.view;

import ru.anatol.sjema.model.BaseType;
import ru.anatol.sjema.producer.model.temp.TempIdentifier;

public class ViewType {

    /**
     * Промежуточный идентификатор.
     */
    private TempIdentifier identifier;

    /**
     * Расширенное описание поля.
     */
    private String description;
    /**
     * Базовый простой тип.
     */
    private BaseType base;
    private TempIdentifier baseIdentifier;
    /**
     * Содержимое комплексного типа.
     */
    private ViewContent content;
    /**
     * Описание ограничений.
     */
    private ViewTypeRestriction restriction;
    /**
     * описание преобразования данных XML в данные модели данных и и обратно.
     */
    private String mapperId;
    /**
     * Информация для виджета.
     */
    private ViewWidget widget;

    public ViewType() {
    }

    public TempIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(TempIdentifier identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BaseType getBase() {
        return base;
    }

    public void setBase(BaseType base) {
        this.base = base;
    }

    public TempIdentifier getBaseIdentifier() {
        return baseIdentifier;
    }

    public void setBaseIdentifier(TempIdentifier baseIdentifier) {
        this.baseIdentifier = baseIdentifier;
    }

    public ViewContent getContent() {
        return content;
    }

    public void setContent(ViewContent content) {
        this.content = content;
    }

    public ViewTypeRestriction getRestriction() {
        return restriction;
    }

    public void setRestriction(ViewTypeRestriction restriction) {
        this.restriction = restriction;
    }

    public String getMapperId() {
        return mapperId;
    }

    public void setMapperId(String mapperId) {
        this.mapperId = mapperId;
    }

    public ViewWidget getWidget() {
        return widget;
    }

    public void setWidget(ViewWidget widget) {
        this.widget = widget;
    }

    public boolean isSimple() {
        if (getContent() != null) {
            return false;
        }
        if (getBase() != null) {
            return true;
        }
        return false;
    }
}
