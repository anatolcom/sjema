package ru.anatol.sjema.model.view;

import java.util.Map;

public class ViewValidation {

    private Map<String, ViewValidationGroup> groupMap;

    public ViewValidation() {
    }

    public Map<String, ViewValidationGroup> getGroupMap() {
        return groupMap;
    }

    public void setGroupMap(Map<String, ViewValidationGroup> groupMap) {
        this.groupMap = groupMap;
    }
}
