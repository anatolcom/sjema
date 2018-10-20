package ru.anatol.sjema.producer.model.temp;

import java.util.*;

public class TempModel extends TempComment {

    private String version;
    private String targetNamespace;
    private final Set<TempElement> elements = new HashSet<>();
    private final Set<TempType> types = new HashSet<>();
    private final Set<TempGroup> groups = new HashSet<>();
    private Map<String, String> consts = new HashMap<>();

    private TempElement.Form attributeFormDefault;
    private TempElement.Form elementFormDefault;

    public TempModel() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public TempIdentifier addElement(TempElement element) {
        Objects.requireNonNull(element, "element is null");
        Objects.requireNonNull(element.getNameId(), "nameId of element is null");
        elements.add(element);
        return element.getId();
    }

    public Set<TempElement> getElementSet() {
        return elements;
    }

    public TempIdentifier addType(TempType type) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(type.getNameId(), "nameId of type is null");
        types.add(type);
        return type.getNameId();
    }

    public Set<TempType> getTypeSet() {
        return types;
    }

    public TempIdentifier addGroup(TempGroup group) {
        Objects.requireNonNull(group, "group is null");
        Objects.requireNonNull(group.getNameId(), "nameId of group is null");
        groups.add(group);
        return group.getNameId();
    }

    public Set<TempGroup> getGroupSet() {
        return groups;
    }

    public Map<String, String> getConsts() {
        return consts;
    }

    public TempElement.Form getAttributeFormDefault() {
        return attributeFormDefault;
    }

    public void setAttributeFormDefault(TempElement.Form attributeFormDefault) {
        this.attributeFormDefault = attributeFormDefault;
    }

    public TempElement.Form getElementFormDefault() {
        return elementFormDefault;
    }

    public void setElementFormDefault(TempElement.Form elementFormDefault) {
        this.elementFormDefault = elementFormDefault;
    }
}
