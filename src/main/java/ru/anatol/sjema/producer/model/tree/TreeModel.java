package ru.anatol.sjema.producer.model.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TreeModel {

    private String targetNamespace;
    private final Map<String, String> namespaces = new HashMap<>();
    private final Set<TreeSchema> schemas = new HashSet<>();
    private final List<TreeNode> nodes = new ArrayList<>();

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public Set<TreeSchema> getSchemas() {
        return schemas;
    }

    public List<TreeNode> getNodes() {
        return nodes;
    }
}
