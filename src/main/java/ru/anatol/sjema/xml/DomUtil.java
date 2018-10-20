/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.anatol.sjema.xml;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DomUtil {

    private DomUtil() {
    }

    //---------------------------------------------------------------------------

    /**
     * Создаёт DocumentBuilder.<br/>
     *
     * @return экземпляр DocumentBuilder
     * @throws DomException в случае ошибки
     */
    private static DocumentBuilder getBuilder() throws DomException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setValidating(false);
            return documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new DomException(ex);
        }
    }

    //---------------------------------------------------------------------------

    /**
     * Воссоздаёт документ из потока in с данными XML.<br/>
     *
     * @param in поток с данными XML
     * @return документ с данными XML
     * @throws DomException в случае ошибки
     */
    public static Document getDocument(InputStream in) throws DomException {
        try {
            return getBuilder().parse(in);
        } catch (SAXException ex) {
            throw new DomException(ex);
        } catch (IOException ex) {
            throw new DomException(ex);
        }
    }

    //---------------------------------------------------------------------------

    /**
     * Воссоздаёт документ из байтового массива xml с данными XML.<br/>
     *
     * @param xml байтовый массив с данными XML
     * @return документ с данными XML
     * @throws DomException в случае ошибки
     */
    public static Document getDocument(byte[] xml) throws DomException {
        try (InputStream inputStream = new ByteArrayInputStream(xml)) {
            return getDocument(inputStream);
        } catch (IOException ex) {
            throw new DomException(ex);
        }
    }

    //---------------------------------------------------------------------------

    /**
     * Воссоздаёт документ из строки xml с данными XML.<br/>
     *
     * @param xml строка с данными XML в формате UTF-8
     * @return документ с данными XML
     * @throws DomException в случае ошибки
     */
    public static Document getDocument(String xml) throws DomException {
        return getDocument(xml.getBytes(StandardCharsets.UTF_8));
    }

    //---------------------------------------------------------------------------

    /**
     * Создаёт пустой документ.<br/>
     *
     * @return пустой документ
     * @throws DomException
     */
    public static Document createDocument() throws DomException {
        return getBuilder().newDocument();
    }

    //---------------------------------------------------------------------------
    public static Node findNodeList(String nodeName, NodeList nodeList) throws DomException {
        Objects.requireNonNull(nodeName, "nodeName is null");
        Node nodeFound = null;
        for (int q = 0; q < nodeList.getLength(); q++) {
            Node node = nodeList.item(q);
            if (node == null) {
                return null;
            }
            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            String localName = node.getLocalName();
            if (localName != null && localName.equals(nodeName)) {
                return node;
            }
            if (node.hasChildNodes()) {
                nodeFound = findNodeList(nodeName, node.getChildNodes());
            }
            if (nodeFound != null) {
                break;
            }
        }
        return nodeFound;
    }

    //---------------------------------------------------------------------------
    public static Node findFirstNode(String nodeName, NodeList nodeList) throws DomException {
        Node nodeFound = null;
        for (int q = 0; q < nodeList.getLength(); q++) {
            Node node = nodeList.item(q);
            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            if (node.getLocalName().equals(nodeName)) {
                return node;
            }
            if (node.hasChildNodes()) {
                nodeFound = findNodeList(nodeName, node.getChildNodes());
            }
            if (nodeFound != null) {
                break;
            }
        }
        return nodeFound;
    }

    //---------------------------------------------------------------------------
    public static Node findLastNode(String nodeName, NodeList nodeList) throws DomException {
        Node nodeFound = null;
        for (int q = 0; q < nodeList.getLength(); q++) {
            Node node = nodeList.item(q);
            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            if (node.getLocalName().equals(nodeName)) {
                return node;
            }
            if (node.hasChildNodes()) {
                nodeFound = findNodeList(nodeName, node.getChildNodes());
            }
            if (nodeFound != null) {
                break;
            }
        }
        return nodeFound;
    }

    //---------------------------------------------------------------------------
    public static Node firstChild(Node source, boolean required) throws DomException {
        Objects.requireNonNull(source, "source is null");
        if (source.hasChildNodes()) {
            NodeList nodeList = source.getChildNodes();
            for (int q = 0; q < nodeList.getLength(); q++) {
                Node node = nodeList.item(q);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    return node;
                }
            }
        }
        if (required) {
            throw new DomException("source has not have child node");
        }
        return null;
    }

    //---------------------------------------------------------------------------
    public static Node getNode(Document doc, String nodeName) throws DomException {
        return findFirstNode(nodeName, doc.getChildNodes());
    }

    //---------------------------------------------------------------------------
    public static String getNodeValue(Node node) throws DomException {
        if (node == null) {
            return null;
        }
        return node.getTextContent();
    }

    //---------------------------------------------------------------------------
    public static String getNodeValue(Document doc, String nodeName) throws DomException {
        try {
            if (!doc.hasChildNodes()) {
                return null;//???
            }
            Node node = findFirstNode(nodeName, doc.getChildNodes());
            if (node == null) {
                return null;
            }
            return node.getTextContent();
        } catch (DOMException ex) {
            throw new DomException(ex);
        }
    }

    //---------------------------------------------------------------------------
    public static String getNodeValue(Node rootNode, String nodeName) throws DomException {
        try {
            if (!rootNode.hasChildNodes()) {
                return null;//???
            }
            Node node = findFirstNode(nodeName, rootNode.getChildNodes());
            if (node == null) {
                return null;
            }
            return node.getTextContent();
        } catch (DOMException ex) {
            throw new DomException(nodeName + " not found because " + ex.getMessage(), ex);
        }
    }

    //---------------------------------------------------------------------------
    public static List<Node> getNodeList(String nodeName, Node node) throws DomException {
        //warning("getNodeList", "Function is under development");
        Objects.requireNonNull(nodeName, "nodeName is null");
        Objects.requireNonNull(nodeName, "nodeName is empty");
        Objects.requireNonNull(node, "node is null");
        try {
            ArrayList<Node> nodes = new ArrayList<Node>();
            gettingNodeList(nodes, nodeName, node);
            return nodes;
        } catch (Exception ex) {
            throw new DomException(ex);
        }
    }

    //---------------------------------------------------------------------------
    private static void gettingNodeList(List<Node> nodes, String nodeName, Node node) throws DomException {
//        try {
//   if(node.getLocalName().equals(nodeName))nodes.add(node);
        if (!node.hasChildNodes()) {
            return;
        }
        NodeList nodeList = node.getChildNodes();
        for (int q = 0; q < nodeList.getLength(); q++) {
            Node subNode = nodeList.item(q);
            String name = subNode.getLocalName();
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (name != null && name.equals(nodeName)) {
                nodes.add(subNode);
            }
            if (subNode.hasChildNodes()) {
                gettingNodeList(nodes, nodeName, subNode);
            }
        }
//        } catch (Exception ex) {
//            throw new DomException(ex);
//        }
    }

    //---------------------------------------------------------------------------
    public static List<String> getNodeValueList(Document doc, String nodeName) throws DomException {
//        warning("getNodeValue", "Function is under development");
        try {
            NodeList nodeList = doc.getChildNodes();
            Node node = findNodeList(nodeName, nodeList);
            if (node == null) {
                return null;
            }
            return null;//node.getTextContent();
        } catch (DOMException ex) {
            throw new DomException(ex);
        }
    }
//---------------------------------------------------------------------------

    /**
     * Получение значения атрибута с именем AttributeName в узле node.<br/>
     *
     * @param node          узел, в котором ищится атрибут
     * @param attributeName имя атрибута
     * @param required      обязательность наличия атрибута
     * @return Значение или null если атрибут ненайден и не обязателен
     * @throws DomException в случае если атрибут не найден но обязательно должен быть
     */
    public static String getAttribute(Node node, String attributeName, boolean required) throws DomException {
        Objects.requireNonNull(node, "node is null");
        Objects.requireNonNull(attributeName, "AttributeName is null");
        for (int q = 0; q < node.getAttributes().getLength(); q++) {
            Node attribute = node.getAttributes().item(q);
            if (attribute == null) {
                continue;
            }
            if (!attribute.getLocalName().equals(attributeName)) {
                continue;
            }
            return attribute.getNodeValue();
        }
        if (required) throw new DomException("Attribute with name \"" + attributeName + "\" not found");
        return null;
    }
//---------------------------------------------------------------------------

    /**
     * Установка значения value для атрибута с именем AttributeName в узле node.<br/>
     *
     * @param node          узел, в котором ищится атрибут
     * @param attributeName имя атрибута
     * @param value         устанавливаемое значение
     * @param required      обязательность наличия атрибута
     * @throws DomException в случае если атрибут не найден но обязательно должен быть
     */
    public static void setAttribute(Node node, String attributeName, String value, boolean required) throws DomException {
        Objects.requireNonNull(node, "node is null");
        Objects.requireNonNull(attributeName, "attributeName is null");
        Objects.requireNonNull(value, "value is null");
        for (int q = 0; q < node.getAttributes().getLength(); q++) {
            Node attribute = node.getAttributes().item(q);
            if (attribute == null) continue;
            if (!attribute.getLocalName().equals(attributeName)) continue;
            attribute.setNodeValue(value);
            return;
        }
        if (required) throw new DomException("Attribute with name \"" + attributeName + "\" not found");
    }

    //---------------------------------------------------------------------------
    public static String printFormat(Node node, int indent) throws DomException {
        Objects.requireNonNull(node, "node is null");
        return toFormat(new DOMSource(node), indent);
    }

    //---------------------------------------------------------------------------
    public static String printFormat(String xml, int indent) throws DomException {
        Objects.requireNonNull(xml, "xml is null");
        return toFormat(new StreamSource(new StringReader(xml)), indent);
    }

    //---------------------------------------------------------------------------
    public static String toFormat(Source source, int indent) throws DomException {
        try {
            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            if (indent > 0) {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(indent));
            }
            transformer.transform(source, new StreamResult(writer));
            return writer.toString();
        } catch (TransformerConfigurationException ex) {
            throw new DomException(ex);
        } catch (TransformerException ex) {
            throw new DomException(ex);
        }
    }

    //---------------------------------------------------------------------------
    public static boolean isEmpty(Node node) {
        return node.getChildNodes().getLength() == 0 && (node.getTextContent() == null || node.getTextContent().isEmpty());
    }

    //---------------------------------------------------------------------------
    public static void clearNode(Node node) {
        while (node.getChildNodes().getLength() > 0) {
            node.removeChild(node.getFirstChild());
        }
        node.setTextContent(null);
    }

    //---------------------------------------------------------------------------
    public static void removeNode(Node node) throws DomException {
        Node parentNode = node.getParentNode();
        if (parentNode == null) {
            throw new DomException("can not remove node, because node not have parent");
        }
        parentNode.removeChild(node);
        //node.getOwnerDocument().removeChild(node);
    }

    //---------------------------------------------------------------------------
    public static Element appendNode(Node target, String name) throws DomException {
        return appendNode(target, name, null);
    }

    //---------------------------------------------------------------------------
    public static Element appendNode(Node target, String name, String value) throws DomException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(name, "name is null");
        try {
            Document doc = target.getOwnerDocument();
            if (doc == null) {
                if (target instanceof Document) doc = (Document) target;
                else throw new DomException("target document has not been getted");
            }
            Element element = doc.createElement(name);
            if (value != null) element.setTextContent(value);
            target.appendChild(element);
            return element;
        } catch (DOMException ex) {
            throw new DomException("append node " + name + " error: " + ex.getMessage(), ex);
        }
    }

    //---------------------------------------------------------------------------
    public static Attr appendAttr(Element target, String name) throws DomException {
        return appendAttr(target, name, null);
    }

    //---------------------------------------------------------------------------
    public static Attr appendAttr(Element target, String name, String value) throws DomException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(name, "name is null");
        try {
            Document doc = target.getOwnerDocument();
            if (doc == null) {
                if (target instanceof Document) doc = (Document) target;
                else throw new DomException("target document has not been getted");
            }
            Attr attr = doc.createAttribute(name);
            if (value != null) attr.setTextContent(value);
            return target.setAttributeNodeNS(attr);
        } catch (DOMException ex) {
            throw new DomException("append attr " + name + " error: " + ex.getMessage(), ex);
        }
    }

    //---------------------------------------------------------------------------
    public static Element appendNodeNS(Node target, Namespaces ns, String name) throws DomException {
        return appendNodeNS(target, ns, name, null);
    }

    //---------------------------------------------------------------------------
    public static Element appendNodeNS(Node target, String uri, String name) throws DomException {
        return appendNodeNS(target, uri, name, null);
    }

    //---------------------------------------------------------------------------
    public static Element appendNodeNS(Node target, Namespaces ns, String name, String value) throws DomException {
        Objects.requireNonNull(name, "name is null");
        int index = name.indexOf(":");
        if (index == 0) throw new DomException("prefix is empty");
        String uri = "";
        if (index > 0) {
            String prefix = name.substring(0, index);
            uri = ns.getNamespaceURI(prefix);
        }
        return appendNodeNS(target, uri, name, value);
    }

    //---------------------------------------------------------------------------
    public static Element appendNodeNS(Node target, String uri, String name, String value) throws DomException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(uri, "uri is null");
        Objects.requireNonNull(name, "name is null");
        try {
            Document doc = target.getOwnerDocument();
            if (doc == null) {
                if (target instanceof Document) doc = (Document) target;
                else throw new DomException("target document has not been getted");
            }
            Element element = doc.createElementNS(uri, name);
            if (value != null) element.setTextContent(value);
            target.appendChild(element);
            return element;
        } catch (DOMException ex) {
            throw new DomException("append node " + uri + ":" + name + " error: " + ex.getMessage(), ex);
        }
    }

    //---------------------------------------------------------------------------
//    public static Attr appendAttrNS(Element target, Namespaces ns, String name, String value) throws DomException {
//        Objects.requireNonNull(name, "name is null");
//        int index = name.indexOf(":");
//        if (index == 0) throw new DomException("prefix is empty");
//        String uri = "";
//        if (index > 0) {
//            String prefix = name.substring(0, index);
//            uri = ns.getNamespaceURI(prefix);
//        }
//        return appendAttrNS(target, uri, name, value);
//    }

    //---------------------------------------------------------------------------
//    public static Attr appendAttrNS(Element target, Namespaces ns, String prefix, String name, String value) throws DomException {
//        Objects.requireNonNull(name, "name is null");
//        String uri = "";
//        if (prefix != null) {
//            uri = ns.getNamespaceURI(prefix);
//        }
//        return appendAttrNS(target, uri, name, value);
//    }

    //---------------------------------------------------------------------------
//    public static Attr appendAttrNS(Element target, Namespaces ns, String name) throws DomException {
//        return appendAttrNS(target, ns, name, null);
//    }

    //---------------------------------------------------------------------------
    public static Attr appendAttrNS(Element target, String uri, String name) throws DomException {
        return appendAttrNS(target, uri, name, null);
    }

    //---------------------------------------------------------------------------
    public static Attr appendAttrNS(Element target, String uri, String name, String value) throws DomException {
        Objects.requireNonNull(target, "target is null");
        Objects.requireNonNull(uri, "uri is null");
        Objects.requireNonNull(name, "name is null");
        try {
            Document doc = target.getOwnerDocument();
            if (doc == null) {
                if (target instanceof Document) doc = (Document) target;
                else throw new DomException("target document has not been getted");
            }
            Attr attr = doc.createAttributeNS(uri, name);
            if (value != null) attr.setTextContent(value);
            return target.setAttributeNodeNS(attr);
        } catch (DOMException ex) {
            throw new DomException("append attr " + uri + ":" + name + " error: " + ex.getMessage(), ex);
        }
    }

    //---------------------------------------------------------------------------
    public static void importNode(Document targetDoc, Node targetNode, Node imported) throws DomException {
        try {
            targetNode.appendChild(targetDoc.importNode(imported, true));
        } catch (DOMException ex) {
            throw new DomException(ex);
        }
    }

    //---------------------------------------------------------------------------
    public static void importNode(Element target, Node imported) throws DomException {
        try {
            Document doc = target.getOwnerDocument();
            if (doc == null) {
                if (target instanceof Document) doc = (Document) target;
                else throw new DomException("target document has not been getted");
            }
            target.appendChild(doc.importNode(imported, true));
        } catch (DOMException ex) {
            throw new DomException(ex);
        }
    }
    //---------------------------------------------------------------------------

    /**
     * Приведение XML в одну строку.
     *
     * @param xml данные XML
     * @return XML в одну строку.
     * @throws DomException
     */
    public static String inline(String xml) throws DomException {
        return xml.replaceAll(">(\\s*)<", "><");
    }

    //---------------------------------------------------------------------------

}
