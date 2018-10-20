package ru.anatol.sjema.model.view;

import org.json.JSONObject;

import java.util.Map;

public class ViewModel {

    private String caption;
    private String description;
    private String targetNamespace;
    private Map<String, JSONObject> structure;
    private Map<String, String> namespaces;
    private Map<String, ViewElement> elements;
    private Map<String, ViewType> types;
    private Map<String, ViewMapper> mappers;
    private ViewValidation validation;

    public ViewModel() {
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

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public Map<String, JSONObject> getStructure() {
        return structure;
    }

    public void setStructure(Map<String, JSONObject> structure) {
        this.structure = structure;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public Map<String, ViewElement> getElements() {
        return elements;
    }

    public void setElements(Map<String, ViewElement> elements) {
        this.elements = elements;
    }

    public Map<String, ViewType> getTypes() {
        return types;
    }

    public void setTypes(Map<String, ViewType> types) {
        this.types = types;
    }

    public Map<String, ViewMapper> getMappers() {
        return mappers;
    }

    public void setMappers(Map<String, ViewMapper> mappers) {
        this.mappers = mappers;
    }

    public ViewValidation getValidation() {
        return validation;
    }

    public void setValidation(ViewValidation validation) {
        this.validation = validation;
    }

}
