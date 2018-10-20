package ru.anatol.sjema.model.view;

/**
 * Описание преобразования данных XML в данные внутреннего представления и обратно.
 */
public class ViewMapper {

    private String className;
    private ViewMapperParams params;

    public ViewMapper() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ViewMapperParams getParams() {
        return params;
    }

    public void setParams(ViewMapperParams params) {
        this.params = params;
    }
}
