package ru.anatol.sjema.producer.model.tree;

public class TreeSchema {
    public enum Mode {
        ROOT,
        INCLUDE,
        IMPORT,
        REDEFINE
    }

    private String name;
    private String hash;
    private Mode mode;
    private String targetNamespace;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }
}
