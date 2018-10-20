package ru.anatol.sjema.model.view;

public class ViewValidationError {

    private String rule;
    private String message;

    public ViewValidationError() {
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
