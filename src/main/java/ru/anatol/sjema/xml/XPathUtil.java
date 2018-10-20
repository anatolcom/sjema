/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.anatol.sjema.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class XPathUtil {

    private XPathUtil() {
    }

    private static Object evaluateExpression(String expression, Node source, NamespaceContext nsContexts, QName returnType) throws PathException {
        Objects.requireNonNull(expression, "expression is null");
        Objects.requireNonNull(source, "source is null");
        Objects.requireNonNull(returnType, "returnType is null");
        try {
            //expression = expression.replace("@", "/attribute::");
            XPath xpath = XPathFactory.newInstance().newXPath();
            if (nsContexts != null) {
                xpath.setNamespaceContext(nsContexts);
            }
            XPathExpression exp = xpath.compile(expression);
            return exp.evaluate(source, returnType);
        } catch (XPathExpressionException ex) {
            if (ex.getCause() == null) {
                throw new PathException("expression \"" + expression + "\" " + ex.getMessage(), ex);
            } else {
                throw new PathException("expression \"" + expression + "\" " + ex.getCause().getMessage(), ex);
            }
        }
    }

    /**
     * Выборка одного узла в соответствии с XPath выражением expression относительно узла source.
     *
     * @param source     узел, относительно которого производится поиск.
     * @param expression XPath выражение.
     * @param nsContexts нэймспэйс. может быть null.
     * @param required   true - узел обязательно должен быть, иначе исключение. false - узла может не
     *                   быть, вернётся null.
     * @return найденный узел. Если required - false, то может вернутся null.
     * @throws PathException
     */
    public static Node getNode(Node source, String expression, NamespaceContext nsContexts, boolean required) throws PathException {
        Node node = (Node) evaluateExpression(expression, source, nsContexts, XPathConstants.NODE);
        if (node == null && required) {
            throw new PathException("Node with expression \"" + expression + "\" not found");
        }
        return node;
    }

    /**
     * Выборка текста в соответствии с XPath выражением expression относительно узла source.
     *
     * @param source     узел, относительно которого производится поиск.
     * @param expression XPath выражение.
     * @param nsContexts нэймспэйс. может быть null.
     * @param required   true - узел обязательно должен быть, иначе исключение. false - узла может не
     *                   быть, вернётся null.
     * @return найденный текст. Если required - false, то может вернутся null.
     * @throws PathException
     */
    public static String getString(Node source, String expression, NamespaceContext nsContexts, boolean required) throws PathException {
        String value = (String) evaluateExpression(expression, source, nsContexts, XPathConstants.STRING);
        if (value == null && required) {
            throw new PathException("String with expression \"" + expression + "\" not found");
        }
        return value;
    }

    /**
     * Выборка одного узла в соответствии с XPath выражением expression относительно узла source.
     *
     * @param source     узел, относительно которого производится поиск.
     * @param expression XPath выражение.
     * @param required   true - узел обязательно должен быть, иначе исключение. false - узла может не
     *                   быть, вернётся null.
     * @return найденный узел. Если required - false, то может вернутся null.
     * @throws PathException
     */
    public static Node getNode(Node source, String expression, boolean required) throws PathException {
        return getNode(source, expression, null, required);
    }

    /**
     * Выборка нескольких узлов в соответствии с XPath выражением expression относительно узла source.
     *
     * @param source     узел, относительно которого производится поиск.
     * @param expression XPath выражение.
     * @param nsContexts нэймспэйс. может быть null.
     * @param required   true - обязательно хотябы 1 узел в списке, иначе исключение. false - узлов может
     *                   не быть вовсе, вернётся пустой список.
     * @return список найденных узлов. Никогда не возвращает null, даже при required - false.
     * @throws PathException
     */
    public static NodeList getNodeList(Node source, String expression, NamespaceContext nsContexts, boolean required) throws PathException {
        NodeList nodeList = (NodeList) evaluateExpression(expression, source, nsContexts, XPathConstants.NODESET);
        if (nodeList.getLength() == 0 && required) {
            throw new PathException("NodeList with expression \"" + expression + "\" not found");
        }
        return nodeList;
    }

    public static List<Node> getNodes(Node source, String expression, NamespaceContext nsContexts, boolean required) throws PathException {
        NodeList nodeList = (NodeList) evaluateExpression(expression, source, nsContexts, XPathConstants.NODESET);
        if (nodeList.getLength() == 0 && required) {
            throw new PathException("NodeList with expression \"" + expression + "\" not found");
        }
        List<Node> nodes = new ArrayList<>(nodeList.getLength());
        for (int index = 0; index < nodeList.getLength(); index++) {
            nodes.add(nodeList.item(index));
        }
        return nodes;
    }

    public static String getValue(Node source, String expression, NamespaceContext nsContexts, boolean required) throws PathException {
        Node node = getNode(source, expression, nsContexts, required);
        if (node == null) {
            return null;
        }
        return node.getTextContent();
    }

    public static List<String> getValues(Node source, String expression, NamespaceContext nsContexts, boolean required) throws PathException {
        NodeList nodeList = (NodeList) evaluateExpression(expression, source, nsContexts, XPathConstants.NODESET);
        if (nodeList.getLength() == 0 && required) {
            throw new PathException("NodeList with expression \"" + expression + "\" not found");
        }
        List<String> values = new ArrayList<>(nodeList.getLength());
        for (int index = 0; index < nodeList.getLength(); index++) {
            values.add(nodeList.item(index).getTextContent());
        }
        return values;
    }

/*
    public static String getValueAsString(Node source, String expression, NamespaceContext nsContexts, boolean required) throws PathException {
        return getValue(source, expression, nsContexts, required);
    }

    public static Integer getValueAsInteger(Node source, String expression, NamespaceContext nsContexts, boolean required) throws PathException {
        String value = getValue(source, expression, nsContexts, required);
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new PathException("value \"" + value + "\" is not a Integer");
        }
    }

    public static Long getValueAsLong(Node source, String expression, NamespaceContext nsContexts, boolean required) throws PathException {
        String value = getValue(source, expression, nsContexts, required);
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new PathException("value \"" + value + "\" is not a Long");
        }
    }

    public static Double getValueAsDouble(Node source, String expression, NamespaceContext nsContexts, boolean required) throws PathException {
        String value = getValue(source, expression, nsContexts, required);
        if (value == null) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new PathException("value \"" + value + "\" is not a Double");
        }
    }

    public static Date getValueAsDate(Node source, String expression, NamespaceContext nsContexts, boolean required, String format) throws PathException {
        String value = getValue(source, expression, nsContexts, required);
        if (value == null) {
            return null;
        }
        try {
            return AFN.dateFromFormat(value, format);
        } catch (AException ex) {
            throw new AException("value \"" + value + "\" is not a Date");
        }
    }
*/


    public static void setValue(Node source, String expression, String value, NamespaceContext nsContexts, boolean required) throws PathException {
        Node node = getNode(source, expression, nsContexts, required);
        if (node == null) {
            return;
        }
        node.setTextContent(value);
    }

    public static void setValue(Node source, String expression, String value, boolean required) throws PathException {
        setValue(source, expression, value, null, required);
    }

    /**
     * Установка множества значений по XPath выражениям
     *
     * @param doc        документ, в рамках ктоторого будет происходить установка значений
     * @param nsContexts контекст с неймспэсами и префиксами
     * @param map        ключь - XPath выражение, значение - устанавливаемое значение
     * @param required   обязательность
     * @return документ
     * @throws PathException
     */
    public static Document setValues(Document doc, NamespaceContext nsContexts, Map<String, String> map, boolean required) throws PathException {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            setValue(doc, entry.getKey(), entry.getValue(), nsContexts, required);
        }
        return doc;
    }

    /**
     * Установка множества значений по XPath выражениям
     *
     * @param node       узел, в рамках ктоторого будет происходить установка значений
     * @param nsContexts контекст с неймспэсами и префиксами
     * @param map        ключь - XPath выражение, значение - устанавливаемое значение
     * @param required   обязательность
     * @return узел
     * @throws PathException
     */
    public static Node setValues(Node node, NamespaceContext nsContexts, Map<String, String> map, boolean required) throws PathException {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            setValue(node, entry.getKey(), entry.getValue(), nsContexts, required);
        }
        return node;
    }

}
