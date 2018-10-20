package ru.anatol.sjema.producer.model.temp;

public class TempGroupRestriction extends TempComment {

    private String minOccurs;
    private String maxOccurs;
    private TempIdentifier typeId;

    public TempGroupRestriction() {
    }

    public String getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(String minOccurs) {
        this.minOccurs = minOccurs;
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(String maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public TempIdentifier getTypeId() {
        return typeId;
    }

    public void setTypeId(TempIdentifier typeId) {
        this.typeId = typeId;
    }
}
