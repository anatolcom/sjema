package ru.anatol.sjema.producer.model.temp;

import java.util.List;

public class TempType extends TempComment {

    public enum Mode {
        SIMPLE,
        COMPLEX,
        EXTENSION,
        RESTRICTION,
        ATTRIBUTE_GROUP
    }

    private final Mode mode;
    private TempIdentifier id;
    private TempIdentifier nameId;
    private String name;
    private List<String> annotation;
    private TempIdentifier contentId;
    private TempTypeRestriction restriction;
    private List<TempIdentifier> union;

    public TempType(Mode mode) {
        this.mode = mode;
    }

    public TempIdentifier getId() {
        return id;
    }

    public void setId(TempIdentifier id) {
        this.id = id;
    }

    public Mode getMode() {
        return mode;
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

    public List<String> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<String> annotation) {
        this.annotation = annotation;
    }

    public TempIdentifier getContentId() {
        return contentId;
    }

    public void setContentId(TempIdentifier contentId) {
        this.contentId = contentId;
    }

    public TempTypeRestriction getRestriction() {
        return restriction;
    }

    public void setRestriction(TempTypeRestriction restriction) {
        this.restriction = restriction;
    }

    public List<TempIdentifier> getUnion() {
        return union;
    }

    public void setUnion(List<TempIdentifier> union) {
        this.union = union;
    }
}
