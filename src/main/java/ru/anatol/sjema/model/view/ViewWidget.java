package ru.anatol.sjema.model.view;

import org.json.JSONObject;

public class ViewWidget {

    private String name;
    private JSONObject params;

    public ViewWidget() {
    }

    public ViewWidget(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }
}
