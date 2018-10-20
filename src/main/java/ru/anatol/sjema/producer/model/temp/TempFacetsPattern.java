package ru.anatol.sjema.producer.model.temp;

public class TempFacetsPattern extends TempComment {

    private String pattern;
    private String description;


    public TempFacetsPattern() {
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
