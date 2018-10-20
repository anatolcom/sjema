/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.anatol.sjema.xml;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.*;


public class Namespaces implements NamespaceContext {

    private final Map<String, String> uriByPrefix = new HashMap<>();
    private final Map<String, Set> prefixesByUri = new HashMap<>();

    public Namespaces() {
        addNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        addNamespace(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    }

    public Namespaces(Namespaces namespace) {
        addNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        addNamespace(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        uriByPrefix.clear();
        uriByPrefix.putAll(namespace.uriByPrefix);
        prefixesByUri.clear();
        prefixesByUri.putAll(namespace.prefixesByUri);
    }

    public Namespaces(String prefix, String namespaceURI) {
        addNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        addNamespace(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        addNamespace(prefix, namespaceURI);
    }

    public final void addNamespace(String prefix, String namespaceURI) {
        uriByPrefix.put(prefix, namespaceURI);
        if (prefixesByUri.containsKey(namespaceURI)) {
            prefixesByUri.get(namespaceURI).add(prefix);
        } else {
            Set<String> set = new HashSet<>();
            set.add(prefix);
            prefixesByUri.put(namespaceURI, set);
        }
    }

    public final Set<String> getPrefixSet() {
        return uriByPrefix.keySet();
    }

    public final Set<String> getUriSet() {
        return prefixesByUri.keySet();
    }

    /**
     * добавляет Namespace.<br />
     *
     * @param xmlns пример значения: xmlns:rev="http://smev.gosuslugi.ru/rev111111"
     * @throws NamespaceException в случае ошибки
     */
    public final void addNamespace(String xmlns) throws NamespaceException {
        xmlns = xmlns.trim();
        if (!xmlns.startsWith("xmlns:")) {
            throw new NamespaceException("\"" + xmlns + "\"is not xmlns");
        }
        xmlns = xmlns.substring(6);
        int pos = xmlns.indexOf("=");
        String prefix = xmlns.substring(0, pos);
        String namespaceURI = xmlns.substring(pos + 2, xmlns.length() - 1);
        addNamespace(prefix, namespaceURI);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        Objects.requireNonNull(prefix, "prefix is null");
        if (!uriByPrefix.containsKey(prefix)) {
            return XMLConstants.NULL_NS_URI;
        }
        return (String) uriByPrefix.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        Iterator prefixes = getPrefixes(namespaceURI);
        if (!prefixes.hasNext()) {
            return null;
        }
        return (String) getPrefixes(namespaceURI).next();
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        Objects.requireNonNull(namespaceURI, "namespaceURI is null");
        if (!prefixesByUri.containsKey(namespaceURI)) {
            return Collections.EMPTY_SET.iterator();
        }
        return prefixesByUri.get(namespaceURI).iterator();
    }

}
