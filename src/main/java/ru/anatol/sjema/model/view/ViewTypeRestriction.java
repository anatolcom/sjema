package ru.anatol.sjema.model.view;

import java.util.List;
import java.util.Map;

public class ViewTypeRestriction {

    public enum WhiteSpace {

        PRESERVE("preserve"),
        COLLAPSE("collapse"),
        REPLACE("replace");

        private final String value;

        WhiteSpace(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static WhiteSpace getByValue(String value) {
            for (WhiteSpace whiteSpace : WhiteSpace.values()) {
                if (whiteSpace.value.equals(value)) {
                    return whiteSpace;
                }
            }
            throw new IllegalArgumentException("unknown whiteSpace value \"" + value + "\"");
        }
    }

    private Map<String, String> enumeration;
    private WhiteSpace whiteSpace;
    private List<ViewTypeRestrictionPattern> patterns;
    private Long length;
    private Long minLength;
    private Long maxLength;
    private Long maxInclusive;
    private Long minInclusive;
    private Long minExclusive;
    private Long maxExclusive;
    private Integer totalDigits;
    private Integer fractionDigits;

    public ViewTypeRestriction() {
    }

    public Map<String, String> getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(Map<String, String> enumeration) {
        this.enumeration = enumeration;
    }

    public WhiteSpace getWhiteSpace() {
        return whiteSpace;
    }

    public void setWhiteSpace(WhiteSpace whiteSpace) {
        this.whiteSpace = whiteSpace;
    }

    public List<ViewTypeRestrictionPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<ViewTypeRestrictionPattern> patterns) {
        this.patterns = patterns;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getMinLength() {
        return minLength;
    }

    public void setMinLength(Long minLength) {
        this.minLength = minLength;
    }

    public Long getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Long maxLength) {
        this.maxLength = maxLength;
    }

    public Long getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(Long maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public Long getMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(Long minInclusive) {
        this.minInclusive = minInclusive;
    }

    public Long getMinExclusive() {
        return minExclusive;
    }

    public void setMinExclusive(Long minExclusive) {
        this.minExclusive = minExclusive;
    }

    public Long getMaxExclusive() {
        return maxExclusive;
    }

    public void setMaxExclusive(Long maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    public Integer getTotalDigits() {
        return totalDigits;
    }

    public void setTotalDigits(Integer totalDigits) {
        this.totalDigits = totalDigits;
    }

    public Integer getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(Integer fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

}
