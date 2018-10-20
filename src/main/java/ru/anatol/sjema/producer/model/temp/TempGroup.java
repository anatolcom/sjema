package ru.anatol.sjema.producer.model.temp;


import java.util.ArrayList;
import java.util.List;

public class TempGroup extends TempComment {

    public enum Mode {
        ALL,
        SEQUENCE,
        CHOICE,
        GROUP_REF,
        NAMED_GROUP,
        ATTRIBUTES,
        NAMED_ATTRIBUTE_GROUP,
        CONTENT,
        SIMPLE_CONTENT,
        COMPLEX_CONTENT
    }

    private final Mode mode;
    private TempIdentifier id;
    private TempIdentifier nameId;
    private String name;
    private TempIdentifier refId;
    private List<String> annotation;
    private TempIdentifier extensionId;
    private TempGroupRestriction restriction;
    private List<TempIdentifier> ids;

    public TempGroup(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public TempIdentifier getId() {
        return id;
    }

    public void setId(TempIdentifier id) {
        this.id = id;
    }

    public TempIdentifier getNameId() {
        return nameId;
    }

    public void setNameId(TempIdentifier nameId) {
        this.nameId = nameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TempIdentifier getRefId() {
        return refId;
    }

    public void setRefId(TempIdentifier refId) {
        this.refId = refId;
    }

    public List<String> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<String> annotation) {
        this.annotation = annotation;
    }

    public TempIdentifier getExtensionId() {
        return extensionId;
    }

    public void setExtensionId(TempIdentifier extensionId) {
        this.extensionId = extensionId;
    }

    public TempGroupRestriction getRestriction() {
        return restriction;
    }

    public void setRestriction(TempGroupRestriction restriction) {
        this.restriction = restriction;
    }

    public List<TempIdentifier> getIds() {
        if (ids == null) {
            ids = new ArrayList<>();
        }
        return ids;
    }

    public void setIds(List<TempIdentifier> ids) {
        this.ids = ids;
    }
}
