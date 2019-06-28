package ru.anatol.sjema.producer.model.tree;

import ru.anatol.sjema.producer.model.temp.TempIdentifier;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    public enum Mode {
        ELEMENT,
        ATTRIBUTE
    }

    private final TempIdentifier identifier;

    private String name;
    private String namespace;
    private Mode mode;
    private TreeType type;
    private List<String> annotations;
    private String path;
    private TreeAny any;
    private final List<TreeNode> nodes = new ArrayList<>();

    public TreeNode(TempIdentifier identifier) {
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

    public TreeType getType() {
        return type;
    }

    public void setType(TreeType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TreeAny getAny() {
        return any;
    }

    public void setAny(TreeAny any) {
        this.any = any;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public List<TreeNode> getNodes() {
        return nodes;
    }
}
