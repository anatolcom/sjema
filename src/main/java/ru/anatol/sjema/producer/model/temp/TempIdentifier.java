package ru.anatol.sjema.producer.model.temp;

public class TempIdentifier {

    public enum Mode {
        TYPE,
        ELEMENT,
        GROUP,
        TYPE_NAME,
        ELEMENT_NAME,
        GROUP_NAME
    }

    private final Mode mode;
    private String namespace;
    private String name;

    public TempIdentifier(Mode mode, String namespace, String name) {
        this.mode = mode;
        this.namespace = namespace;
        this.name = name;
    }

    public Mode getMode() {
        return mode;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return mode.name() + " {" + namespace + "}" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TempIdentifier that = (TempIdentifier) o;

        if (getMode() != that.getMode()) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return getNamespace() != null ? getNamespace().equals(that.getNamespace()) : that.getNamespace() == null;
    }

    @Override
    public int hashCode() {
        int result = getMode() != null ? getMode().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getNamespace() != null ? getNamespace().hashCode() : 0);
        return result;
    }
}
