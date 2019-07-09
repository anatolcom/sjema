package ru.anatol.sjema.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.anatol.sjema.xml.path.XPath;
import ru.anatol.sjema.xml.path.constant.Constant;
import ru.anatol.sjema.xml.path.constant.ConstantInteger;
import ru.anatol.sjema.xml.path.constant.ConstantNodeName;
import ru.anatol.sjema.xml.path.constant.ConstantString;
import ru.anatol.sjema.xml.path.function.Function;
import ru.anatol.sjema.xml.path.function.FunctionNodeName;
import ru.anatol.sjema.xml.path.operation.Operation;
import ru.anatol.sjema.xml.path.operation.Path;
import ru.anatol.sjema.xml.path.operator.Operator;
import ru.anatol.sjema.xml.path.operator.OperatorEquals;
import ru.anatol.sjema.xml.path.parser.ParserException;
import ru.anatol.sjema.xml.path.parser.PathParser;
import ru.anatol.sjema.xml.path.parser.PathPrinter;
import ru.anatol.sjema.xml.path.step.Axis;
import ru.anatol.sjema.xml.path.step.Step;
import ru.anatol.sjema.xml.path.step.StepContext;
import ru.anatol.sjema.xml.path.step.StepRoot;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DomProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomProcessor.class);

    public enum CreateMode {
        ALWAYS,
        IF_NOT_EXISTS;
    }

    private final Namespaces namespaces;

    public DomProcessor(Namespaces namespaces) {
        this.namespaces = namespaces;
    }

    public Namespaces getNamespaces() {
        return namespaces;
    }

    public Node getNode(Node sourceNode, String path, boolean required) throws PathException {
        return XPathUtil.getNode(sourceNode, path, namespaces, required);
    }

    public String getValue(Node sourceNode, String path, boolean required) throws PathException {
        return XPathUtil.getValue(sourceNode, path, namespaces, required);
    }

    public String getString(Node sourceNode, String path, boolean required) throws PathException {
        return XPathUtil.getString(sourceNode, path, namespaces, required);
    }

    public List<Node> getNodes(Node sourceNode, String path, boolean required) throws PathException {
        return XPathUtil.getNodes(sourceNode, path, namespaces, required);
    }

    public List<String> getValues(Node sourceNode, String path, boolean required) throws PathException {
        return XPathUtil.getValues(sourceNode, path, namespaces, required);
    }

    public Node forceNode(Node target, String path, CreateMode createMode) throws PathException {
        try {
            final PathParser parser = new PathParser(path, namespaces);
            return forceNode(target, parser.read(), createMode);
        } catch (ParserException ex) {
            throw new PathException("path \"" + path + "\" can not be parsed, because: " + ex.getMessage(), ex);
        }
    }

    private Node forceNode(Node target, XPath path, CreateMode createMode) throws PathException {
        return processOperation(target, path.getOperation(), createMode);
    }


    private Node processOperation(Node target, Operation operation, CreateMode createMode) throws PathException {
        Objects.requireNonNull(operation);

        if (operation instanceof Path) {
            return processPath(target, (Path) operation, createMode);
        }

        if (operation instanceof Operator) {
            processOperator(target, (Operator) operation, createMode);
            return target;
        }

        throw new UnsupportedOperationException(operation.getClass().getSimpleName());
    }

    private String processConstant(Constant constant) {

        if (constant instanceof ConstantInteger) {
            ConstantInteger constantInteger = (ConstantInteger) constant;
            return Integer.toString(constantInteger.getValue());
        }

        if (constant instanceof ConstantString) {
            ConstantString constantString = (ConstantString) constant;
            return constantString.getValue();
        }

        throw new UnsupportedOperationException(constant.getClass().getSimpleName());
    }

    private void processOperator(Node target, Operator operator, CreateMode createMode) throws PathException {
        if (operator instanceof OperatorEquals) {
            OperatorEquals operatorEquals = (OperatorEquals) operator;
            Operation operandA = operatorEquals.getOperandA();
            Operation operandB = operatorEquals.getOperandB();
            if ((operandA instanceof Path) && (operandB instanceof Constant)) {
                Node node = processPath(target, (Path) operandA, createMode);
                node.setTextContent(processConstant((Constant) operandB));
                return;
            }
            if ((operandA instanceof Constant) && (operandB instanceof Path)) {
                Node node = processPath(target, (Path) operandB, createMode);
                node.setTextContent(processConstant((Constant) operandA));
                return;
            }
        }
        throw new UnsupportedOperationException(operator.getClass().getSimpleName());
    }

    private Node processPath(Node target, Path path, CreateMode createMode) throws PathException {
        try {
            Node node = target;
            for (Step step : path.getSteps()) {
                if (step instanceof StepRoot) {
                    node = processStepRoot(node);
                    continue;
                }
                if (step instanceof StepContext) {
                    node = processStepContext(node, (StepContext) step, createMode);
                    continue;
                }
            }
            return node;
        } catch (DomException ex) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PathPrinter.printOperation(new PrintStream(outputStream), path);
            throw new PathException("path \"" + outputStream.toString() + "\" can not be resolved for node: \"" + target.getNodeName() + "\", because: " + ex.getMessage(), ex);
        }
    }

    private Node processStepRoot(Node node) {
        if (node instanceof Document) {
            return node;
        }
        return node.getOwnerDocument();
    }

    private Node processStepContext(Node node, StepContext stepContext, CreateMode createMode) throws DomException, PathException {
        node = processAxisFunction(node, stepContext.getAxis(), stepContext.getFunction(), createMode);
        if (stepContext.getConditions() != null) {
            for (Operation operation : stepContext.getConditions()) {
                forceCondition(node, operation, createMode);
            }
        }
        return node;
    }

    private Node processAxisFunction(Node node, Axis axis, Function function, CreateMode createMode) throws DomException, PathException {
        switch (axis) {
            case PARENT:
                return processAxisParent(node);
            case SELF:
                return node;
            case CHILD:
                return processAxisChild(node, function, createMode);
            case ATTRIBUTE:
                return processAxisAttribute(node, function);
            default:
                throw new UnsupportedOperationException("unknown axis: " + axis.name());
        }
    }

    private Node processAxisParent(Node node) throws DomException, PathException {
        if (node.getParentNode() == null) {
            throw new PathException("parent node not exists");
        }
        return node.getParentNode();
    }

    private Node processAxisChild(Node node, Function function, CreateMode createMode) throws DomException, PathException {
        if (!(function instanceof FunctionNodeName)) {
            throw new PathException("unsupported function " + function.name());
        }
        final FunctionNodeName functionNodeName = (FunctionNodeName) function;
        final ConstantNodeName nodeName = functionNodeName.getNodeName();
        final String namespaceUri = getNamespaceURI(nodeName.getPrefix());
        switch (createMode) {
            case ALWAYS:
                return appendElementNS(node, namespaceUri, nodeName.getPrefix(), nodeName.getName());
            case IF_NOT_EXISTS:
                final List<Node> nodeList = findChildElementsNS(node, namespaceUri, nodeName.getName());
                if (nodeList != null && !nodeList.isEmpty()) {
                    return nodeList.get(nodeList.size() - 1);
                } else {
                    return appendElementNS(node, namespaceUri, nodeName.getPrefix(), nodeName.getName());
                }
            default:
                throw new PathException("unknown create mode " + createMode);
        }
    }

    private Node processAxisAttribute(Node node, Function function) throws DomException, PathException {
        if (!(function instanceof FunctionNodeName)) {
            throw new PathException("unsupported function " + function.name());
        }
        final FunctionNodeName functionNodeName = (FunctionNodeName) function;
        final ConstantNodeName nodeName = functionNodeName.getNodeName();
        final String namespaceUri = getNamespaceURI(nodeName.getPrefix());
        Attr attr = findAttrNS(node, namespaceUri, nodeName.getName());
        if (attr != null) {
            return attr;
        }
        return appendAttrNS(node, namespaceUri, nodeName.getName());
    }

    private String getNamespaceURI(String prefix) {
        if (prefix == null) {
            return null;
        }
        return namespaces.getNamespaceURI(prefix);
    }

    private void forceCondition(Node target, Operation operation, CreateMode createMode) throws PathException {
        processOperation(target, operation, createMode);
    }

    public static Attr appendAttrNS(Node target, String uri, String name) throws DomException, PathException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(name, "name is null");
        if (!(target instanceof Element)) {
            throw new PathException("target node for adding attribute is not element");
        }
        try {
            Document doc = target.getOwnerDocument();
            if (doc == null) {
                if (!(target instanceof Document)) {
                    throw new DomException("target document has not been getted");
                }
                doc = (Document) target;
            }
            Attr attr = doc.createAttributeNS(uri, name);
            ((Element) target).setAttributeNodeNS(attr);
            return attr;
        } catch (DOMException ex) {
            throw new DomException("append attr " + name + " error: " + ex.getMessage(), ex);
        }
    }

    public static Attr findAttrNS(Node target, String uri, String name) throws DomException, PathException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(name, "name is null");
        if (!(target instanceof Element)) {
            throw new PathException("target node for finding attribute is not element");
        }
        return ((Element) target).getAttributeNodeNS(uri, name);
    }

    public static Element appendElementNS(Node target, String uri, String prefix, String name) throws DomException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(name, "name is null");
        try {
            Document doc = target.getOwnerDocument();
            if (doc == null) {
                if (!(target instanceof Document)) {
                    throw new DomException("target document has not been getted");
                }
                doc = (Document) target;
            }
            final Element element = doc.createElementNS(uri, name);
            element.setPrefix(prefix);
            target.appendChild(element);
            return element;
        } catch (DOMException ex) {
            throw new DomException("append node " + uri + ":" + name + " error: " + ex.getMessage(), ex);
        }
    }

    public static NodeList findElementsNS(Node target, String uri, String name) throws DomException, PathException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(name, "name is null");
        if (target instanceof Element) {
            return ((Element) target).getElementsByTagNameNS(uri, name);
        }
        if (target instanceof Document) {
            return ((Document) target).getElementsByTagNameNS(uri, name);
        }
        throw new PathException("target node for finding elements is not document or element");
    }

    public static List<Node> findChildElementsNS(Node target, String uri, String name) throws DomException, PathException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(name, "name is null");
        final NodeList childNodes = target.getChildNodes();
        if (childNodes == null) {
            return Collections.EMPTY_LIST;
        }
        final List<Node> nodes = new ArrayList<>(childNodes.getLength());
        for (int index = 0; index < childNodes.getLength(); index++) {
            final Node node = childNodes.item(index);
            if (compare(name, node.getLocalName()) && compare(uri, node.getNamespaceURI())) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    private static boolean compare(String a, String b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }
}
