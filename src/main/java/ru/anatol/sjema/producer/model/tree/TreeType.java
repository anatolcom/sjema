package ru.anatol.sjema.producer.model.tree;

import ru.anatol.sjema.producer.model.temp.TempIdentifier;

import java.util.List;

public class TreeType {

    public enum Mode {
        SIMPLE,
        COMPLEX
    }

    private final TempIdentifier identifier;
    private String name;
    private String namespace;
    private Mode mode;
    private List<String> annotations;

    public TreeType(TempIdentifier identifier) {
        this.identifier = identifier;
    }

    public TempIdentifier getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }
}
