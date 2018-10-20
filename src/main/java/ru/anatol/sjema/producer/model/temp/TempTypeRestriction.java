package ru.anatol.sjema.producer.model.temp;

import java.util.List;

public class TempTypeRestriction extends TempComment {

    private TempIdentifier baseId;
    private List<String> annotation;

    private TempFacets facets;

    public TempTypeRestriction() {
    }

    public TempIdentifier getBaseId() {
        return baseId;
    }

    public void setBaseId(TempIdentifier baseId) {
        this.baseId = baseId;
    }

    public List<String> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<String> annotation) {
        this.annotation = annotation;
    }

    public TempFacets getFacets() {
        return facets;
    }

    public void setFacets(TempFacets facets) {
        this.facets = facets;
    }
}
