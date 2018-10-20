package ru.anatol.sjema.converter;

import org.w3c.dom.Node;
import ru.anatol.sjema.model.view.ViewElement;

import java.util.LinkedList;
import java.util.List;

public class Context {

    /**
     * Родительский контекст.
     */
    private Context parent;
    /**
     * Текущий узел.
     */
    private Node node;
    /**
     * Идентификатор текущего элемента.
     */
    private String elementId;
    /**
     * Текущий элемент.
     */
    private ViewElement element;

    public Context(Context parent, Node node, String elementId, ViewElement element) {
        this.parent = parent;
        this.node = node;
        this.elementId = elementId;
        this.element = element;
    }

    public Context getParent() {
        return parent;
    }

    public Node getNode() {
        return node;
    }

    public String getElementId() {
        return elementId;
    }

    public ViewElement getElement() {
        return element;
    }

    public String getFullPath() {
        Context context = this;
        List<String> pathItems = new LinkedList<>();
        while (context != null) {
            pathItems.add(0, context.getElement().getPath());
            context = context.getParent();
        }
        return String.join("/", pathItems);
    }
}
