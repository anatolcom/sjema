package ru.anatol.sjema.producer.model.temp;

import java.util.List;
import java.util.Map;

public class TempFacets extends TempComment {

    public enum WhiteSpace {
        PRESERVE,
        COLLAPSE,
        REPLACE
    }

//    private TempIdentifier baseId;
//    private List<String> annotation;

    private Map<String, String> enumeration;
    private Map<String, String> facets;
    private WhiteSpace whiteSpace;
    private List<TempFacetsPattern> patterns;


    public TempFacets() {
    }

//    public List<String> getAnnotation() {
//        return annotation;
//    }
//
//    public void setAnnotation(List<String> annotation) {
//        this.annotation = annotation;
//    }

    public Map<String, String> getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(Map<String, String> enumeration) {
        this.enumeration = enumeration;
    }

    public Map<String, String> getFacets() {
        return facets;
    }

    public void setFacets(Map<String, String> facets) {
        this.facets = facets;
    }

    public WhiteSpace getWhiteSpace() {
        return whiteSpace;
    }

    public void setWhiteSpace(WhiteSpace whiteSpace) {
        this.whiteSpace = whiteSpace;
    }

    public List<TempFacetsPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<TempFacetsPattern> patterns) {
        this.patterns = patterns;
    }
}
