package ru.anatol.sjema.producer.model.temp;

import java.util.List;

public class TempGroupAny {

    /**
     * processContents	Optional. Specifies how the XML processor should handle validation against the elements specified by this any element. Can be set to one of the following:
     * strict - the XML processor must obtain the schema for the required namespaces and validate the elements (this is default)
     * lax - same as strict but; if the schema cannot be obtained, no errors will occur
     * skip - The XML processor does not attempt to validate any elements from the specified namespaces
     */
    private String processContents;

    /**
     * namespace	Optional. Specifies the namespaces containing the elements that can be used. Can be set to one of the following:
     * ##any - elements from any namespace is allowed (this is default)
     * ##other - elements from any namespace that is not the namespace of the parent element can be present
     * ##local - elements must come from no namespace
     * ##targetNamespace - elements from the namespace of the parent element can be present
     * List of {URI references of namespaces, ##targetNamespace, ##local} - elements from a space-delimited list of the namespaces can be present
     */
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
