package ru.anatol.sjema.model.view;

public class ViewElementRepeatable {

    private String path;
    private int min = ViewConst.ELEMENT_REPEATABLE_MIN_DEFAULT;
    private int max = ViewConst.ELEMENT_REPEATABLE_MAX_DEFAULT;
    private String caption;
    private String addCaption;
    private String removeCaption;

    public ViewElementRepeatable() {
    }

    public int getMin() {
        return min;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getAddCaption() {
        return addCaption;
    }

    public void setAddCaption(String addCaption) {
        this.addCaption = addCaption;
    }

    public String getRemoveCaption() {
        return removeCaption;
    }

    public void setRemoveCaption(String removeCaption) {
        this.removeCaption = removeCaption;
    }
}
