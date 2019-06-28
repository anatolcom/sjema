package ru.anatol.sjema.producer.model.tree;

import java.util.List;

public class TreeAny {

    private String processContents;

    private List<String> namespaces;

    public String getProcessContents() {
        return processContents;
    }

    public void setProcessContents(String processContents) {
        this.processContents = processContents;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
    }
}
