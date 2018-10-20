package ru.anatol.sjema.model.view;

import java.util.List;

public class ViewValidationGroup {

    private List<ViewValidationError> errors;

    public ViewValidationGroup() {
    }

    public List<ViewValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ViewValidationError> errors) {
        this.errors = errors;
    }

}
