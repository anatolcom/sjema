package ru.anatol.sjema.producer.model.temp;

public class TempElementRestriction extends TempComment {
    private String minOccurs;
    private String maxOccurs;
//    private boolean _abstract;
//    private boolean nillable;

    public TempElementRestriction() {
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

//    public boolean isAbstract() {
//        return _abstract;
//    }
//
//    public void setAbstract(boolean _abstract) {
//        this._abstract = _abstract;
//    }
//
//    public boolean isNillable() {
//        return nillable;
//    }
//
//    public void setNillable(boolean nillable) {
//        this.nillable = nillable;
//    }
}
