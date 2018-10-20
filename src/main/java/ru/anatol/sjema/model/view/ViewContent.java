package ru.anatol.sjema.model.view;

import java.util.List;

public class ViewContent {

    public enum Mode {
        SEQUENCE,
        CHOICE
    }

    /**
     * режим
     */
    private Mode mode;
    private List<String> elementIds;
    private List<String> xmlOrder;

    public ViewContent() {
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public List<String> getElementIds() {
        return elementIds;
    }

    public void setElementIds(List<String> elementIds) {
        this.elementIds = elementIds;
    }

    public List<String> getXmlOrder() {
        return xmlOrder;
    }

    public void setXmlOrder(List<String> xmlOrder) {
        this.xmlOrder = xmlOrder;
    }
}
