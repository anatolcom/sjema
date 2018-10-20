package ru.anatol.sjema.producer;

import java.util.HashSet;
import java.util.Set;

public final class XmlConst {

    private XmlConst() {
    }

    public static final String PREFIX = "xml";

    public static final String URI = "http://www.w3.org/XML/1998/namespace";

    /*
        http://www.w3.org/XML/1998/namespace

        lang - Designed for identifying the human language used in the scope of the element to which it's attached.

        space - Designed to express whether or not the document's creator wishes white space to be considered
        as significant in the scope of the element to which it's attached.


        base - The XML Base specification (Second edition) describes a facility, similar to that of HTML BASE,
        for defining base URIs for parts of XML documents. It defines a single attribute, xml:base, and describes
        in detail the procedure for its use in processing relative URI refeferences.

        id - The xml:id specification defines a single attribute, xml:id, known to be of type ID independently
        of any DTD or schema.
    */


    /**
     * Язык.
     */
    public static final String LANG_TYPE_ID = "lang";
    /**
     * Пространство.
     */
    public static final String SPACE_TYPE_ID = "space";
    /**
     * Основа.
     */
    public static final String BASE_TYPE_ID = "base";
    /**
     * Идентификатор.
     */
    public static final String ID_TYPE_ID = "id";


    public static final Set<String> typeIdSet;

    static {
        typeIdSet = getTypeIdSet();
    }

    private static Set<String> getTypeIdSet() {
        final Set<String> set = new HashSet<>();
        set.add(LANG_TYPE_ID);
        set.add(SPACE_TYPE_ID);
        set.add(BASE_TYPE_ID);
        set.add(ID_TYPE_ID);
        return set;
    }

    public static boolean isTypeId(String typeId) {
        return typeIdSet.contains(typeId);
    }

}
