package ru.anatol.sjema.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2001.xmlschema.Annotated;
import org.w3._2001.xmlschema.Annotation;
import org.w3._2001.xmlschema.Any;
import org.w3._2001.xmlschema.Appinfo;
import org.w3._2001.xmlschema.Attribute;
import org.w3._2001.xmlschema.AttributeGroup;
import org.w3._2001.xmlschema.AttributeGroupRef;
import org.w3._2001.xmlschema.ComplexContent;
import org.w3._2001.xmlschema.ComplexType;
import org.w3._2001.xmlschema.Documentation;
import org.w3._2001.xmlschema.Element;
import org.w3._2001.xmlschema.ExplicitGroup;
import org.w3._2001.xmlschema.ExtensionType;
import org.w3._2001.xmlschema.Facet;
import org.w3._2001.xmlschema.Field;
import org.w3._2001.xmlschema.Group;
import org.w3._2001.xmlschema.GroupRef;
import org.w3._2001.xmlschema.Import;
import org.w3._2001.xmlschema.Include;
import org.w3._2001.xmlschema.Keybase;
import org.w3._2001.xmlschema.Keyref;
import org.w3._2001.xmlschema.LocalElement;
import org.w3._2001.xmlschema.LocalSimpleType;
import org.w3._2001.xmlschema.NamedAttributeGroup;
import org.w3._2001.xmlschema.NamedGroup;
import org.w3._2001.xmlschema.Notation;
import org.w3._2001.xmlschema.OpenAttrs;
import org.w3._2001.xmlschema.Pattern;
import org.w3._2001.xmlschema.Redefine;
import org.w3._2001.xmlschema.RestrictionType;
import org.w3._2001.xmlschema.Schema;
import org.w3._2001.xmlschema.Selector;
import org.w3._2001.xmlschema.SimpleContent;
import org.w3._2001.xmlschema.SimpleType;
import org.w3._2001.xmlschema.TopLevelAttribute;
import org.w3._2001.xmlschema.TopLevelElement;
import org.w3._2001.xmlschema.TotalDigits;
import org.w3._2001.xmlschema.Union;
import org.w3._2001.xmlschema.WhiteSpace;
import org.w3._2001.xmlschema.Wildcard;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.producer.model.temp.TempElement;
import ru.anatol.sjema.producer.model.temp.TempElementRestriction;
import ru.anatol.sjema.producer.model.temp.TempFacets;
import ru.anatol.sjema.producer.model.temp.TempFacetsPattern;
import ru.anatol.sjema.producer.model.temp.TempGroup;
import ru.anatol.sjema.producer.model.temp.TempGroupRestriction;
import ru.anatol.sjema.producer.model.temp.TempIdentifier;
import ru.anatol.sjema.producer.model.temp.TempModel;
import ru.anatol.sjema.producer.model.temp.TempType;
import ru.anatol.sjema.producer.model.temp.TempTypeRestriction;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class XsdSchemaToTempModelConverter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XsdSchemaToTempModelConverter.class);

    private class XsdSchemas {

        private final XsdSchemaResolver resolver;
        private Set<String> schemaIds = new HashSet<>();

        public XsdSchemas(XsdSchemaResolver resolver) {
            this.resolver = resolver;
        }

        public boolean resolved(String schemaLocation, String parentSchemaId) throws ConverterException {
            String schemaId = resolver.getId(schemaLocation, parentSchemaId);
            return schemaIds.contains(schemaId);
        }

        public Schema resolve(String schemaLocation, String parentSchemaId) throws ConverterException {
            String schemaId = resolver.getId(schemaLocation, parentSchemaId);
            schemaIds.add(schemaId);
            return resolver.resolve(schemaLocation, parentSchemaId);
        }

        public String getSchemaId(String schemaLocation, String parentSchemaId) throws ConverterException {
            return resolver.getId(schemaLocation, parentSchemaId);
        }
    }

    public XsdSchemaToTempModelConverter() {
    }

    /**
     * Преобразование в TempModel.
     *
     * @param xsdSchemaResolver резолвер для получения схем
     * @param schemaLocation    расположение схемы
     * @return TempModel
     * @throws ConverterException
     */
    public TempModel convert(XsdSchemaResolver xsdSchemaResolver, String schemaLocation) throws ConverterException {
        try {
            XsdSchemas xsdSchemas = new XsdSchemas(xsdSchemaResolver);
            return toTempModel(xsdSchemas, schemaLocation, null);
        } catch (ConverterException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ConverterException(ex);
        }
    }

    /**
     * Преобразование в TempModel.
     *
     * @param xsdSchemas     резолвер для получения схем
     * @param schemaLocation расположение схемы
     * @return TempModel
     * @throws ConverterException
     */

    private TempModel toTempModel(XsdSchemas xsdSchemas, String schemaLocation, String parentSchemaId) throws ConverterException {
        Objects.requireNonNull(xsdSchemas, "xsdSchemas is null");
//        Objects.requireNonNull(schemaLocation, "schemaLocation is null");

        final String schemaId = xsdSchemas.getSchemaId(schemaLocation, parentSchemaId);
        final Schema schema = xsdSchemas.resolve(schemaLocation, parentSchemaId);
        Objects.requireNonNull(schema, "schema is null");

        TempModel model = new TempModel();

        model.setTargetNamespace(schema.getTargetNamespace());

        model.getConsts().put("lang", schema.getLang());
        model.setVersion(schema.getVersion());

        if (schema.getAttributeFormDefault() != null) {
            model.setAttributeFormDefault(TempElement.Form.valueOf(schema.getAttributeFormDefault().name()));
        } else {
            model.setAttributeFormDefault(TempElement.Form.UNQUALIFIED);
        }

        if (schema.getElementFormDefault() != null) {
            model.setElementFormDefault(TempElement.Form.valueOf(schema.getElementFormDefault().name()));
        } else {
            model.setElementFormDefault(TempElement.Form.QUALIFIED);
        }

        if (!schema.getBlockDefault().isEmpty()) {
            model.addComment("#BlockDefault : " + toString(schema.getBlockDefault()));
        }
        if (!schema.getFinalDefault().isEmpty()) {
            model.addComment("#FinalDefault : " + toString(schema.getFinalDefault()));
        }

//        if (schema.getOtherAttributes() != null) {
//            model.addComment("#OtherAttributes : " + toString(schema.getOtherAttributes()));
//        }

        List<OpenAttrs> includeOrImportOrRedefine = schema.getIncludeOrImportOrRedefine();

        for (OpenAttrs item : includeOrImportOrRedefine) {
            if (item instanceof Include) {
                readInclude(model, xsdSchemas, schemaId, (Include) item);
                continue;
            }
            if (item instanceof Import) {
                readImport(model, xsdSchemas, schemaId, (Import) item);
                continue;
            }
            if (item instanceof Redefine) {
                readRedefine(model, xsdSchemas, schemaId, (Redefine) item);
                continue;
            }
            if (item instanceof Annotation) {
                model.addComment("#Annotation : ???");
                continue;
            }
            model.addComment("#" + item.getClass().getSimpleName() + "___iir___???");
        }

        List<OpenAttrs> simpleTypeOrComplexTypeOrGroup = schema.getSimpleTypeOrComplexTypeOrGroup();

        for (OpenAttrs item : simpleTypeOrComplexTypeOrGroup) {

            if (item instanceof Attribute) {
                model.addElement(readAttribute(model, (Attribute) item));
                continue;
            }
            if (item instanceof Element) {
                model.addElement(readElement(model, (Element) item));
                continue;
            }

            if (item instanceof SimpleType) {
                model.addType(readSimpleType(model, (SimpleType) item));
                continue;
            }
            if (item instanceof ComplexType) {
                model.addType(readComplexType(model, (ComplexType) item));
                continue;
            }

            if (item instanceof NamedAttributeGroup) {
                model.addGroup(readNamedAttributeGroup(model, (NamedAttributeGroup) item));
                continue;
            }
            if (item instanceof NamedGroup) {
                model.addGroup(readNamedGroup(model, (NamedGroup) item));
                continue;
            }

            if (item instanceof Notation) {
                model.addComment("#Notation : ???");
                continue;
            }

            model.addComment("#" + item.getClass().getSimpleName() + "___stctg___???");
        }

        return model;
    }

    //---------------------------------------------------------------------------
    public void readInclude(TempModel model, XsdSchemas xsdSchemas, String parentSchemaId, Include xsdInclude) throws ConverterException {
        Objects.requireNonNull(xsdInclude, "xsdInclude is null");

        //List<String> annotation = readAnnotation(xsdInclude.getAnnotation());
        final String schemaLocation = xsdInclude.getSchemaLocation();
        final String namespace = model.getTargetNamespace();

        if (xsdSchemas.resolved(schemaLocation, parentSchemaId)) {
            return;
        }
        TempModel includeModel = toTempModel(xsdSchemas, schemaLocation, parentSchemaId);

        if (!includeModel.getTargetNamespace().equals(namespace)) {
            throw new ConverterException("include schema \"" + schemaLocation + "\" failure, because targetNamespace \"" + namespace + "\" mismatch targetNamespace \"" + includeModel.getTargetNamespace() + "\" of included schema");
        }

        LOGGER.debug("include schemaLocation: \"{}\"", schemaLocation);

        for (TempType type : includeModel.getTypeSet()) {
//            if (namespace.equals(type.getNameId().getNamespace())) {
            model.addType(type);
//            }
        }
        for (TempElement element : includeModel.getElementSet()) {
//            if (namespace.equals(element.getId().getNamespace())) {
            model.addElement(element);
//            }
        }
        for (TempGroup group : includeModel.getGroupSet()) {
//            if (namespace.equals(group.getNameId().getNamespace())) {
            model.addGroup(group);
//            }
        }
    }

    //---------------------------------------------------------------------------
    public void readImport(TempModel model, XsdSchemas xsdSchemas, String parentSchemaId, Import xsdImport) throws ConverterException {
        Objects.requireNonNull(xsdImport, "xsdImport is null");

        //List<String> annotation = readAnnotation(xsdImport.getAnnotation());
        final String schemaLocation = xsdImport.getSchemaLocation();
        final String namespace = xsdImport.getNamespace();

        if (xsdSchemas.resolved(schemaLocation, parentSchemaId)) {
            return;
        }
        TempModel importModel = toTempModel(xsdSchemas, schemaLocation, parentSchemaId);

        if (!importModel.getTargetNamespace().equals(namespace)) {
            throw new ConverterException("import schema \"" + schemaLocation + "\" failure, because import namespace \"" + namespace + "\" mismatch targetNamespace \"" + importModel.getTargetNamespace() + "\" of imported schema");
        }

        LOGGER.debug("import schemaLocation: \"{}\", namespace: \"{}\"", schemaLocation, namespace);

        for (TempType type : importModel.getTypeSet()) {
//            if (namespace.equals(type.getNameId().getNamespace())) {
            model.addType(type);
//            }
        }
        for (TempElement element : importModel.getElementSet()) {
//            if (namespace.equals(element.getId().getNamespace())) {
            model.addElement(element);
//            }
        }
        for (TempGroup group : importModel.getGroupSet()) {
//            if (namespace.equals(group.getNameId().getNamespace())) {
            model.addGroup(group);
//            }
        }
    }

    //---------------------------------------------------------------------------
    public void readRedefine(TempModel model, XsdSchemas xsdSchemas, String parentSchemaId, Redefine xsdRedefine) throws ConverterException {
        Objects.requireNonNull(xsdRedefine, "xsdRedefine = null");

        final String schemaLocation = xsdRedefine.getSchemaLocation();

        model.addComment("#Redefine schemaLocation: " + schemaLocation + " ???");

//        if (xsdSchemas.resolved(schemaLocation, parentSchemaId)) {
//            return;
//        }
//        TempModel redefineModel = toTempModel(xsdSchemas, schemaLocation, parentSchemaId);

        List<OpenAttrs> annotationOrSimpleTypeOrComplexType = xsdRedefine.getAnnotationOrSimpleTypeOrComplexType();

        for (OpenAttrs item : annotationOrSimpleTypeOrComplexType) {
            //    if (item instanceof SimpleType)
            //    {
            //    model.types.add(readSimpleType((SimpleType)item));
            //    continue;
            //    }
            //    if (item instanceof ComplexType)
            //    {
            //    model.types.add(readComplexType((ComplexType)item));
            //    continue;
            //    }
            //    if (item instanceof Element)
            //    {
            //    model.nodes.add(readElement((Element)item));
            //    continue;
            //    }
            //    if (item instanceof Attribute)
            //    {
            //    model.nodes.add(readAttribute((Attribute)item));
            //    continue;
            //    }
            //    if (item instanceof NamedAttributeGroup)
            //    {
            //    model.nodes.add(readNamedAttributeGroup((NamedAttributeGroup)item));
            //    continue;
            //    }//attributeGroup
            model.addComment("#" + item.getClass().getSimpleName() + "___astct___???");
        }
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование элемента в TempElement.
     *
     * @param element элемент
     * @return TempElement
     */
    public TempElement readElement(TempModel model, Element element) throws ConverterException {
        Objects.requireNonNull(element, "element is null");

        final boolean topLevel = element instanceof TopLevelElement;

        final TempElement tempElement = new TempElement(TempElement.Mode.ELEMENT);

        final String elementId;
        if (element.getId() != null) {
            elementId = element.getId();
        } else {
            if (topLevel) {
                elementId = element.getName();
            } else {
                elementId = null;
            }
        }

        tempElement.setId(createIdentifier(TempIdentifier.Mode.ELEMENT, elementId, model.getTargetNamespace()));
        tempElement.setNameId(createIdentifier(TempIdentifier.Mode.ELEMENT_NAME, element.getName(), model.getTargetNamespace()));

        tempElement.setAnnotation(readAnnotation(element.getAnnotation()));

        tempElement.setRoot(topLevel);

        if (element.getDefault() != null) {
            tempElement.setConstant(false);
            tempElement.setValue(element.getDefault());
        }
        if (element.getFixed() != null) {
            tempElement.setConstant(true);
            tempElement.setValue(element.getDefault());
        }

        tempElement.setNillable(element.isNillable());
        tempElement.setAbstract(element.isAbstract());
        tempElement.setFinal(element.getFinal());

        //Атрибуты type и ref являются взаимоисключающими.
        if (element.getRef() != null && element.getType() != null) {
            throw new ConverterException("element: " + tempElement.getNameId() + " contains type and ref, which are mutually exclusive");
        }
        if (element.getRef() != null) {
            tempElement.setRefId(readIdentifier(TempIdentifier.Mode.ELEMENT, element.getRef()));
        }
        if (element.getType() != null) {
            tempElement.setTypeId(readIdentifier(TempIdentifier.Mode.TYPE_NAME, element.getType()));
        }

        if (!element.getBlock().isEmpty()) {
            tempElement.addComment("#Block : " + toString(element.getBlock()));
        }
        if (element.getForm() != null) {
            tempElement.setForm(TempElement.Form.valueOf(element.getForm().name()));
        }
        if (element.getSubstitutionGroup() != null) {
            tempElement.addComment("#SubstitutionGroup : " + toString(element.getSubstitutionGroup()));
        }

        tempElement.setRestriction(new TempElementRestriction());
        tempElement.getRestriction().setMinOccurs(element.getMinOccurs().toString());
        tempElement.getRestriction().setMaxOccurs(element.getMaxOccurs().toString());

        if (element.getSimpleType() != null) {
            TempType type = readSimpleType(model, element.getSimpleType());
            tempElement.setTypeId(model.addType(type));
        }

        if (element.getComplexType() != null) {
            TempType type = readComplexType(model, element.getComplexType());
            tempElement.setTypeId(model.addType(type));
        }

        tempElement.addComment(readIdentityConstraint(element.getIdentityConstraint()));//????????????????????

        return tempElement;
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование атрибута в TempElement.
     *
     * @param attribute атрибут
     * @return TempElement
     */
    private TempElement readAttribute(TempModel model, Attribute attribute) {
        Objects.requireNonNull(attribute, "attribute is null");

        final boolean topLevel = attribute instanceof TopLevelAttribute;

        final TempElement tempElement = new TempElement(TempElement.Mode.ATTRIBUTE);

        final String attributeId;
        if (attribute.getId() != null) {
            attributeId = attribute.getId();
        } else {
            if (topLevel) {
                attributeId = attribute.getName();
            } else {
                attributeId = null;
            }
        }

        tempElement.setId(createIdentifier(TempIdentifier.Mode.ELEMENT, attributeId, model.getTargetNamespace()));
        tempElement.setNameId(createIdentifier(TempIdentifier.Mode.ELEMENT_NAME, attribute.getName(), model.getTargetNamespace()));

        tempElement.setAnnotation(readAnnotation(attribute.getAnnotation()));

        tempElement.setRoot(topLevel);

        if (attribute.getDefault() != null) {
            tempElement.setConstant(false);
            tempElement.setValue(attribute.getDefault());
        }
        if (attribute.getFixed() != null) {
            tempElement.setConstant(true);
            tempElement.setValue(attribute.getFixed());
        }

        tempElement.setNillable(false);//???
        tempElement.setAbstract(false);//???
        tempElement.setFinal(null);//???

        if (attribute.getRef() != null) {
            tempElement.setRefId(readIdentifier(TempIdentifier.Mode.ELEMENT, attribute.getRef()));
        }
        if (attribute.getType() != null) {
            tempElement.setTypeId(readIdentifier(TempIdentifier.Mode.TYPE_NAME, attribute.getType()));
        }

        if (attribute.getForm() != null) {
            tempElement.setForm(TempElement.Form.valueOf(attribute.getForm().name()));
        }

        tempElement.setRestriction(new TempElementRestriction());
        if ("required".equals(attribute.getUse())) {
            tempElement.getRestriction().setMinOccurs("1");
        } else {
            tempElement.getRestriction().setMinOccurs("0");
        }
        tempElement.getRestriction().setMaxOccurs("1");

        if (attribute.getSimpleType() != null) {
            TempType type = readSimpleType(model, attribute.getSimpleType());
            tempElement.setTypeId(model.addType(type));
        }

        return tempElement;
    }

    //---------------------------------------------------------------------------

    /**
     * TODO не понятно как работать с листом
     *
     * @param list список
     * @return TempElement
     */
    private TempElement toList(TempModel model, org.w3._2001.xmlschema.List list) {
        Objects.requireNonNull(list, "list is null");

        final TempElement tempElement = new TempElement(TempElement.Mode.LIST);

        tempElement.setId(createIdentifier(TempIdentifier.Mode.ELEMENT, list.getId(), model.getTargetNamespace()));
        tempElement.setNameId(createIdentifier(TempIdentifier.Mode.ELEMENT_NAME, null, model.getTargetNamespace()));

        tempElement.setAnnotation(readAnnotation(list.getAnnotation()));

        tempElement.setRoot(false);

        if (list.getItemType() != null) {
            tempElement.setTypeId(readIdentifier(TempIdentifier.Mode.TYPE_NAME, list.getItemType()));
        }

        tempElement.setRestriction(new TempElementRestriction());//???
        //TODO проверить значения по умолчанию
        tempElement.getRestriction().setMinOccurs("0");//???
        tempElement.getRestriction().setMaxOccurs("unbounded");//???

        if (list.getSimpleType() != null) {
            TempType type = readSimpleType(model, list.getSimpleType());
            tempElement.setTypeId(model.addType(type));
        }

        return tempElement;
    }

    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------

    private TempGroup newContentGroup(TempModel model, String name) {

        final TempGroup contentGroup = new TempGroup(TempGroup.Mode.CONTENT);
        contentGroup.setId(createIdentifier(TempIdentifier.Mode.GROUP, null, model.getTargetNamespace()));
        contentGroup.setNameId(createIdentifier(TempIdentifier.Mode.GROUP_NAME, null, model.getTargetNamespace()));

        contentGroup.setName(name);
        contentGroup.setAnnotation(null);

        contentGroup.setRestriction(new TempGroupRestriction());
        //TODO проверить значения по умолчанию
        contentGroup.getRestriction().setMinOccurs("0");
        contentGroup.getRestriction().setMaxOccurs("1");

        return contentGroup;
    }

    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------

    /**
     * Преобразование простого содержимого в TempGroup.
     *
     * @param simpleContent простое содержимое
     * @return TempGroup
     */
    public TempGroup readSimpleContent(TempModel model, SimpleContent simpleContent) throws ConverterException {
        Objects.requireNonNull(simpleContent, "simpleContent is null");

        TempGroup tempGroup = new TempGroup(TempGroup.Mode.SIMPLE_CONTENT);

        tempGroup.setId(createIdentifier(TempIdentifier.Mode.GROUP, simpleContent.getId(), model.getTargetNamespace()));
        tempGroup.setNameId(createIdentifier(TempIdentifier.Mode.GROUP_NAME, null, model.getTargetNamespace()));

        tempGroup.setName(null);
        tempGroup.setAnnotation(readAnnotation(simpleContent.getAnnotation()));

        tempGroup.setRestriction(new TempGroupRestriction());
        //TODO проверить значения по умолчанию
        tempGroup.getRestriction().setMinOccurs("0");
        tempGroup.getRestriction().setMaxOccurs("1");
        if (simpleContent.getRestriction() != null) {
            TempType type = readRestrictionType(model, simpleContent.getRestriction());
            tempGroup.getRestriction().setTypeId(model.addType(type));
        }

        if (simpleContent.getExtension() != null) {
            TempType type = readExtensionType(model, simpleContent.getExtension());
            tempGroup.setExtensionId(model.addType(type));
        }

        return tempGroup;
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование составного содержимого в TempGroup.
     *
     * @param complexContent составное содержимое
     * @return TempGroup
     */
    public TempGroup readComplexContent(TempModel model, ComplexContent complexContent) throws ConverterException {
        Objects.requireNonNull(complexContent, "complexContent is null");

        TempGroup tempGroup = new TempGroup(TempGroup.Mode.COMPLEX_CONTENT);

        tempGroup.setId(createIdentifier(TempIdentifier.Mode.GROUP, complexContent.getId(), model.getTargetNamespace()));
        tempGroup.setNameId(createIdentifier(TempIdentifier.Mode.GROUP_NAME, null, model.getTargetNamespace()));

        tempGroup.setName(null);
        tempGroup.setAnnotation(readAnnotation(complexContent.getAnnotation()));

        tempGroup.setRestriction(new TempGroupRestriction());
        //TODO проверить значения по умолчанию
        tempGroup.getRestriction().setMinOccurs("0");
        tempGroup.getRestriction().setMaxOccurs("1");
        if (complexContent.getRestriction() != null) {
            TempType type = readRestrictionType(model, complexContent.getRestriction());
            tempGroup.getRestriction().setTypeId(model.addType(type));
        }

        if (complexContent.getExtension() != null) {
            TempType type = readExtensionType(model, complexContent.getExtension());
            tempGroup.setExtensionId(model.addType(type));
        }
        if (complexContent.isMixed() != null) tempGroup.addComment("#Mixed : " + complexContent.isMixed());

        return tempGroup;
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование именованной группы атрибутов в TempGroup.
     *
     * @param namedAttributeGroup именованная группа атрибутов
     * @return TempGroup
     */
    private TempGroup readNamedAttributeGroup(TempModel model, NamedAttributeGroup namedAttributeGroup) {
        Objects.requireNonNull(namedAttributeGroup, "namedAttributeGroup is null");
        return readAttributeGroup(model, namedAttributeGroup);
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование ссылки на группу атрибутов в TempGroup.
     *
     * @param attributeGroupRef ссылка на группу атрибутов
     * @return TempGroup
     */
    private TempGroup readAttributeGroupRef(TempModel model, AttributeGroupRef attributeGroupRef) {
        Objects.requireNonNull(attributeGroupRef, "attributeGroupRef is null");
        return readAttributeGroup(model, attributeGroupRef);
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование группы атрибутов в TempGroup.
     *
     * @param attributeGroup группа атрибутов
     * @return TempGroup
     */
    private TempGroup readAttributeGroup(TempModel model, AttributeGroup attributeGroup) {

        TempGroup tempGroup = new TempGroup(TempGroup.Mode.NAMED_ATTRIBUTE_GROUP);

        tempGroup.setId(createIdentifier(TempIdentifier.Mode.GROUP, attributeGroup.getId(), model.getTargetNamespace()));
        tempGroup.setNameId(createIdentifier(TempIdentifier.Mode.GROUP_NAME, attributeGroup.getName(), model.getTargetNamespace()));

        tempGroup.setName(attributeGroup.getName());
        tempGroup.setAnnotation(readAnnotation(attributeGroup.getAnnotation()));

        tempGroup.setRestriction(new TempGroupRestriction());
        //TODO проверить значения по умолчанию
        tempGroup.getRestriction().setMinOccurs("0");
        tempGroup.getRestriction().setMaxOccurs("1");

        if (attributeGroup.getRef() != null) {
            tempGroup.setRefId(readIdentifier(TempIdentifier.Mode.GROUP_NAME, attributeGroup.getRef()));
        }

        if (attributeGroup.getAttributeOrAttributeGroup() != null
                && !attributeGroup.getAttributeOrAttributeGroup().isEmpty()) {
            TempGroup tAttributesGroup = readAttributeOrAttributeGroup(model, attributeGroup.getAttributeOrAttributeGroup());
            tempGroup.getIds().add(model.addGroup(tAttributesGroup));
        }

        if (attributeGroup.getAnyAttribute() != null) {
            tempGroup.addComment("#AnyAttribute : " + toString(attributeGroup.getAnyAttribute()));
        }

        return tempGroup;
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование группы атрибутов в TempGroup.
     *
     * @param annotatedList группа атрибутов
     * @return TempGroup
     */
    private TempGroup readAttributeOrAttributeGroup(TempModel model, List<Annotated> annotatedList) {
        Objects.requireNonNull(annotatedList, "annotatedList is null");

        TempGroup tempGroup = new TempGroup(TempGroup.Mode.ATTRIBUTES);

        tempGroup.setId(createIdentifier(TempIdentifier.Mode.GROUP, null, model.getTargetNamespace()));
        tempGroup.setNameId(createIdentifier(TempIdentifier.Mode.GROUP_NAME, null, model.getTargetNamespace()));

        tempGroup.setName(null);
        tempGroup.setAnnotation(null);

        tempGroup.setRestriction(new TempGroupRestriction());
        //TODO проверить значения по умолчанию
        tempGroup.getRestriction().setMinOccurs("0");
        tempGroup.getRestriction().setMaxOccurs("1");

        for (Annotated item : annotatedList) {
            if (item instanceof Attribute) {
                TempElement tElement = readAttribute(model, (Attribute) item);
                tempGroup.getIds().add(model.addElement(tElement));
                continue;
            }
            if (item instanceof AttributeGroupRef) {
                TempGroup tGroup = readAttributeGroupRef(model, (AttributeGroupRef) item);
                tempGroup.getIds().add(model.addGroup(tGroup));
                continue;
            }
            tempGroup.addComment("#" + item.getClass().getSimpleName() + "___aoag___???");
        }
        return tempGroup;
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование явной группы в TempGroup.
     *
     * @param mode          режим
     * @param explicitGroup явная группа
     * @return TempGroup
     */
    public TempGroup readExplicitGroup(TempModel model, TempGroup.Mode mode, ExplicitGroup explicitGroup) throws ConverterException {
        Objects.requireNonNull(explicitGroup, "explicitGroup is null");

        return readTempGroup(model, mode, explicitGroup);
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование именованной группы в TempGroup.
     *
     * @param namedGroup именованная группа
     * @return TempGroup
     */
    private TempGroup readNamedGroup(TempModel model, NamedGroup namedGroup) throws ConverterException {
        Objects.requireNonNull(namedGroup, "namedGroup is null");

        return readTempGroup(model, TempGroup.Mode.NAMED_GROUP, namedGroup);
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование ссылочной группы в TempGroup.
     *
     * @param groupRef ссылочная группа
     * @return TempGroup
     */
    public TempGroup readGroupRef(TempModel model, GroupRef groupRef) throws ConverterException {
        Objects.requireNonNull(groupRef, "groupRef is null");

        return readTempGroup(model, TempGroup.Mode.GROUP_REF, groupRef);
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование группы в TempGroup.
     *
     * @param mode  режим
     * @param group группа
     * @return TempGroup
     */
    public TempGroup readTempGroup(TempModel model, TempGroup.Mode mode, Group group) throws ConverterException {
        Objects.requireNonNull(group, "group is null");

        final TempGroup tempGroup = new TempGroup(mode);

        tempGroup.setId(createIdentifier(TempIdentifier.Mode.GROUP, group.getId(), model.getTargetNamespace()));
        tempGroup.setNameId(createIdentifier(TempIdentifier.Mode.GROUP_NAME, group.getName(), model.getTargetNamespace()));

        tempGroup.setName(group.getName());
        tempGroup.setAnnotation(readAnnotation(group.getAnnotation()));

        tempGroup.setRestriction(new TempGroupRestriction());
        tempGroup.getRestriction().setMinOccurs(group.getMinOccurs().toString());
        tempGroup.getRestriction().setMaxOccurs(group.getMaxOccurs().toString());

        if (group.getRef() != null) {
            tempGroup.setRefId(readIdentifier(TempIdentifier.Mode.GROUP_NAME, group.getRef()));
        }

        if (group.getParticle() != null) {
            for (Object item : group.getParticle()) {
                if (item instanceof JAXBElement) {
                    final JAXBElement element = (JAXBElement) item;
                    final Object value = element.getValue();

                    if (value instanceof LocalElement) {
                        final TempElement tElement = readElement(model, (LocalElement) value);
                        tempGroup.getIds().add(model.addElement(tElement));
                        continue;
                    }
                    if (value instanceof ExplicitGroup) {
                        final TempGroup.Mode subGroupMode = TempGroup.Mode.valueOf(element.getName().getLocalPart().toUpperCase());
                        final TempGroup subGroup = readExplicitGroup(model, subGroupMode, (ExplicitGroup) value);
                        tempGroup.getIds().add(model.addGroup(subGroup));
                        continue;
                    }
                    if (value instanceof GroupRef) {
                        final TempGroup groupRef = readGroupRef(model, (GroupRef) value);
                        tempGroup.getIds().add(model.addGroup(groupRef));
                        continue;
                    }

                    tempGroup.addComment(element.getName().getLocalPart() + " : " + element.getValue().getClass().getSimpleName() + "___???");
                    continue;
                }
//            if ("Any".equals(item.getClass().getSimpleName())) {
                if (item instanceof Any) {
                    //Any any = (Any)item;
                    tempGroup.addComment("#Any : ???");
                    continue;
                }
                tempGroup.addComment("#Particle : " + item.toString() + " - " + item.getClass().getSimpleName() + "___???");
            }

        }

        return tempGroup;
    }

    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------

    /**
     * Преобразование простого типа в TempType.
     *
     * @param simpleType простой тип
     * @return TempType.
     */
    public TempType readSimpleType(TempModel model, SimpleType simpleType) {
        Objects.requireNonNull(simpleType, "simpleType is null");

        final TempType tempType = new TempType(TempType.Mode.SIMPLE);

        tempType.setId(createIdentifier(TempIdentifier.Mode.TYPE, simpleType.getId(), model.getTargetNamespace()));
        tempType.setNameId(createIdentifier(TempIdentifier.Mode.TYPE_NAME, simpleType.getName(), model.getTargetNamespace()));

        if (simpleType.getName() != null) {
            tempType.setName(simpleType.getName());
        } else {
            tempType.setName("simpleType");
        }
        tempType.setAnnotation(readAnnotation(simpleType.getAnnotation()));

        if (!simpleType.getFinal().isEmpty()) {
            tempType.addComment("#Final : " + toString(simpleType.getFinal()));
        }

        tempType.setRestriction(new TempTypeRestriction());
        if (simpleType.getRestriction() != null) {
            tempType.getRestriction().setAnnotation(readAnnotation(simpleType.getRestriction().getAnnotation()));
            if (simpleType.getRestriction().getBase() != null) {
                tempType.getRestriction().setBaseId(readIdentifier(TempIdentifier.Mode.TYPE_NAME, simpleType.getRestriction().getBase()));
            }
            if (simpleType.getRestriction().getSimpleType() != null) {
                TempType baseType = readSimpleType(model, simpleType.getRestriction().getSimpleType());
                tempType.getRestriction().setBaseId(model.addType(baseType));
            }
            if (simpleType.getRestriction().getFacets() != null && !simpleType.getRestriction().getFacets().isEmpty()) {
                tempType.getRestriction().setFacets(readFacets(simpleType.getRestriction().getFacets()));
            }
        }

        if (simpleType.getList() != null) {
            toList(model, simpleType.getList());
        }
        //The union element defines a simple type as a collection (union) of values from specified simple data types.
        if (simpleType.getUnion() != null) {
            tempType.setUnion(readUnion(model, simpleType.getUnion()));
        }

        return tempType;
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование комплексного типа в TempType.
     *
     * @param complexType комплексный тип
     * @return TempType
     */
    public TempType readComplexType(TempModel model, ComplexType complexType) throws ConverterException {
        Objects.requireNonNull(complexType, "complexType is null");

        TempType tType = new TempType(TempType.Mode.COMPLEX);

        tType.setId(createIdentifier(TempIdentifier.Mode.TYPE, complexType.getId(), model.getTargetNamespace()));
        tType.setNameId(createIdentifier(TempIdentifier.Mode.TYPE_NAME, complexType.getName(), model.getTargetNamespace()));

        if (complexType.getName() != null) {
            tType.setName(complexType.getName());
        } else {
            tType.setName("complexType");
        }
        tType.setAnnotation(readAnnotation(complexType.getAnnotation()));

        if (!complexType.getBlock().isEmpty()) {
            tType.addComment("#Block : " + toString(complexType.getBlock()));
        }
        if (!complexType.getFinal().isEmpty()) {
            tType.addComment("#Final : " + toString(complexType.getFinal()));
        }
        tType.addComment("#Abstract : " + complexType.isAbstract());
        tType.addComment("#Mixed : " + complexType.isMixed());

        TempGroup contentGroup = newContentGroup(model, complexType.getName());

        if (complexType.getAttributeOrAttributeGroup() != null
                && !complexType.getAttributeOrAttributeGroup().isEmpty()) {
            TempGroup attributesGroup = readAttributeOrAttributeGroup(model, complexType.getAttributeOrAttributeGroup());
            contentGroup.getIds().add(model.addGroup(attributesGroup));
        }
        //  if (complexType.getOtherAttributes() != null) type.addOtherAttributes(readOtherAttributes(complexType.getOtherAttributes()));

        if (complexType.getSimpleContent() != null) {
            TempGroup group = readSimpleContent(model, complexType.getSimpleContent());
            contentGroup.getIds().add(model.addGroup(group));
        }
        if (complexType.getComplexContent() != null) {
            TempGroup group = readComplexContent(model, complexType.getComplexContent());
            contentGroup.getIds().add(model.addGroup(group));
        }

        if (complexType.getAll() != null) {
            TempGroup group = readExplicitGroup(model, TempGroup.Mode.ALL, complexType.getAll());
            contentGroup.getIds().add(model.addGroup(group));
        }
        if (complexType.getChoice() != null) {
            TempGroup group = readExplicitGroup(model, TempGroup.Mode.CHOICE, complexType.getChoice());
            contentGroup.getIds().add(model.addGroup(group));
        }
        if (complexType.getSequence() != null) {
            TempGroup group = readExplicitGroup(model, TempGroup.Mode.SEQUENCE, complexType.getSequence());
            contentGroup.getIds().add(model.addGroup(group));
        }

        if (complexType.getGroup() != null) {
            TempGroup group = readGroupRef(model, complexType.getGroup());
            contentGroup.getIds().add(model.addGroup(group));
        }

        if (!contentGroup.getIds().isEmpty()) {
            tType.setContentId(model.addGroup(contentGroup));
        }

        return tType;
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование расширенного типа в TempType.
     *
     * @param extensionType расширенный тип
     * @return TempType
     */
    public TempType readExtensionType(TempModel model, ExtensionType extensionType) throws ConverterException {
        Objects.requireNonNull(extensionType, "extensionType is null");

        TempType tType = new TempType(TempType.Mode.EXTENSION);

        tType.setId(createIdentifier(TempIdentifier.Mode.TYPE, extensionType.getId(), model.getTargetNamespace()));
        tType.setNameId(createIdentifier(TempIdentifier.Mode.TYPE_NAME, null, model.getTargetNamespace()));

        tType.setName("extensionType");
        tType.setAnnotation(readAnnotation(extensionType.getAnnotation()));

        tType.setRestriction(new TempTypeRestriction());
        if (extensionType.getBase() != null) {
            tType.getRestriction().setBaseId(readIdentifier(TempIdentifier.Mode.TYPE_NAME, extensionType.getBase()));
        }

        TempGroup contentGroup = newContentGroup(model, null);

        if (extensionType.getAttributeOrAttributeGroup() != null
                && !extensionType.getAttributeOrAttributeGroup().isEmpty()) {
            TempGroup tAttributesGroup = readAttributeOrAttributeGroup(model, extensionType.getAttributeOrAttributeGroup());
            contentGroup.getIds().add(model.addGroup(tAttributesGroup));
        }

        if (extensionType.getAll() != null) {
            TempGroup group = readExplicitGroup(model, TempGroup.Mode.ALL, extensionType.getAll());
            contentGroup.getIds().add(model.addGroup(group));
        }
        if (extensionType.getChoice() != null) {
            TempGroup group = readExplicitGroup(model, TempGroup.Mode.CHOICE, extensionType.getChoice());
            contentGroup.getIds().add(model.addGroup(group));
        }
        if (extensionType.getSequence() != null) {
            TempGroup group = readExplicitGroup(model, TempGroup.Mode.SEQUENCE, extensionType.getSequence());
            contentGroup.getIds().add(model.addGroup(group));
        }
        if (extensionType.getGroup() != null) {
            TempGroup group = readGroupRef(model, extensionType.getGroup());
            contentGroup.getIds().add(model.addGroup(group));
        }

        if (!contentGroup.getIds().isEmpty()) {
            tType.setContentId(model.addGroup(contentGroup));
        }

        if (extensionType.getAnyAttribute() != null) {
            tType.addComment("#AnyAttribute : " + toString(extensionType.getAnyAttribute()));
        }

        return tType;
    }

    //---------------------------------------------------------------------------

    /**
     * Преобразование ограниченного типа в TempType.
     *
     * @param restrictionType ограниченный тип
     * @return TempType
     */
    public TempType readRestrictionType(TempModel model, RestrictionType restrictionType) throws ConverterException {
        Objects.requireNonNull(restrictionType, "restrictionType is null");

        TempType tType = new TempType(TempType.Mode.RESTRICTION);

        tType.setId(createIdentifier(TempIdentifier.Mode.TYPE, restrictionType.getId(), model.getTargetNamespace()));
        tType.setNameId(createIdentifier(TempIdentifier.Mode.TYPE_NAME, null, model.getTargetNamespace()));

        tType.setName("restrictionType");
        tType.setAnnotation(readAnnotation(restrictionType.getAnnotation()));

        tType.setRestriction(new TempTypeRestriction());
        if (restrictionType.getBase() != null) {
            tType.getRestriction().setBaseId(readIdentifier(TempIdentifier.Mode.TYPE_NAME, restrictionType.getBase()));
        }
        if (restrictionType.getSimpleType() != null) {
            TempType baseType = readSimpleType(model, restrictionType.getSimpleType());
            tType.getRestriction().setBaseId(model.addType(baseType));
        }
        if (restrictionType.getFacets() != null && !restrictionType.getFacets().isEmpty()) {
            tType.getRestriction().setFacets(readFacets(restrictionType.getFacets()));
        }

        TempGroup contentGroup = newContentGroup(model, null);

        if (restrictionType.getAttributeOrAttributeGroup() != null
                && !restrictionType.getAttributeOrAttributeGroup().isEmpty()) {
            TempGroup attributesGroup = readAttributeOrAttributeGroup(model, restrictionType.getAttributeOrAttributeGroup());
            contentGroup.getIds().add(model.addGroup(attributesGroup));
        }

        if (restrictionType.getAll() != null) {
            TempGroup group = readExplicitGroup(model, TempGroup.Mode.ALL, restrictionType.getAll());
            contentGroup.getIds().add(model.addGroup(group));
        }
        if (restrictionType.getChoice() != null) {
            TempGroup group = readExplicitGroup(model, TempGroup.Mode.CHOICE, restrictionType.getChoice());
            contentGroup.getIds().add(model.addGroup(group));
        }
        if (restrictionType.getSequence() != null) {
            TempGroup group = readExplicitGroup(model, TempGroup.Mode.SEQUENCE, restrictionType.getSequence());
            contentGroup.getIds().add(model.addGroup(group));
        }
        if (restrictionType.getGroup() != null) {
            TempGroup group = readGroupRef(model, restrictionType.getGroup());
            contentGroup.getIds().add(model.addGroup(group));
        }

        if (!contentGroup.getIds().isEmpty()) {
            tType.setContentId(model.addGroup(contentGroup));
        }

        if (restrictionType.getAnyAttribute() != null) {
            tType.addComment("#AnyAttribute : " + toString(restrictionType.getAnyAttribute()));
        }
        return tType;
    }

    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------

    /**
     * Преобразование объединения в список TempIdentifier.
     *
     * @param union объединение
     * @return список TempIdentifier
     */
    public List<TempIdentifier> readUnion(TempModel model, Union union) {
        Objects.requireNonNull(union, "union is null");

        List<TempIdentifier> tempIdentifiers = new ArrayList<>(union.getSimpleType().size());
        if (union.getMemberTypes() != null) {
            for (QName memberType : union.getMemberTypes()) {
                tempIdentifiers.add(readIdentifier(TempIdentifier.Mode.TYPE_NAME, memberType));
            }
        }
        if (union.getSimpleType() != null) {
            for (LocalSimpleType simpleType : union.getSimpleType()) {
                TempType tempType = readSimpleType(model, simpleType);
                tempIdentifiers.add(model.addType(tempType));
            }
        }
        return tempIdentifiers;
    }

    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------

    public static String readIdentityConstraint(List<Object> list) {
        if (list.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (Object item : list) {
            if (sb.length() > 0) sb.append(", ");
            if (item instanceof Keyref)//???
            {
                sb.append("Keyref");
                continue;
            }
            if (item instanceof JAXBElement)//???
            {
                JAXBElement element = (JAXBElement) item;
                sb.append(toString(element.getName()));

                Keybase value = (Keybase) element.getValue();
                sb.append(", name:\"").append(value.getName()).append("\"");

                Selector selector = value.getSelector();
                sb.append(", selector:\"").append(selector.getXpath()).append("\"");

                for (Field field : value.getField()) {
                    sb.append(", field:\"").append(field.getXpath()).append("\"");
                }
                continue;
            }
            sb.append("#").append(item.getClass().getSimpleName()).append("___ic___???");
        }

        //  Keyref keyref;
        //  JAXBElement key;
        //  JAXBElement unique;
        return "#IdentityConstraint [ " + sb.toString() + " ]";

    }

    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------

/*    public static List<Attr> readOtherAttributes(Map<QName, String> otherAttributes) {
        if (otherAttributes == null) throw new IllegalArgumentException("otherAttributes = null");
        List<Attr> attrs = new ArrayList<Attr>();
        while (otherAttributes.entrySet().iterator().hasNext()) {
            Map.Entry<QName, String> item = otherAttributes.entrySet().iterator().next();
            Attr attr = new Attr();
            attr.dataType.assign(getType(item.getKey()));
            attr.value = item.getValue();
            attrs.add(attr);
        }
        return attrs;
    }
*/
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------

    private TempIdentifier createIdentifier(TempIdentifier.Mode mode, String name, String namespace) {
        if (name == null) {
            name = UUID.randomUUID().toString();
        }
        return new TempIdentifier(mode, namespace, name);
    }

    //---------------------------------------------------------------------------

    private TempIdentifier readIdentifier(TempIdentifier.Mode mode, QName qName) {
        Objects.requireNonNull(qName, "qName is null");

        return new TempIdentifier(mode, qName.getNamespaceURI(), qName.getLocalPart());
    }

    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------

    private List<String> readAnnotation(Annotation annotation) {
//        Objects.requireNonNull(annotation, "annotation is null");
        if (annotation == null) {
            return null;
        }

        List<String> documentations = new ArrayList<>(annotation.getAppinfoOrDocumentation().size());

        for (Object item : annotation.getAppinfoOrDocumentation()) {
            if (item instanceof Documentation) {
                String documentation = readDocumentation((Documentation) item);
                if (documentation != null && !documentation.isEmpty()) {
                    documentations.add(documentation);
                }
                continue;
            }
            if (item instanceof Appinfo) {
                String appinfo = readAppinfo((Appinfo) item);
                if (appinfo != null && !appinfo.isEmpty()) {
                    documentations.add(appinfo);
                }
                continue;
            }

            documentations.add(" #Annotation : " + item.getClass().getSimpleName());
        }
        return documentations;
    }

    //---------------------------------------------------------------------------

    private String readDocumentation(Documentation documentation) {
        Objects.requireNonNull(documentation, "documentation is null");

        List<String> list = new ArrayList<>(documentation.getContent().size());
        for (Object item : documentation.getContent()) {
            if (item instanceof String) {
                list.add(((String) item).trim());
                continue;
            }
            list.add("#Documentation : " + item.getClass().getSimpleName());
        }
        return String.join(", ", list);
    }

    //---------------------------------------------------------------------------

    private String readAppinfo(Appinfo appinfo) {
        Objects.requireNonNull(appinfo, "appinfo is null");

        List<String> list = new ArrayList<>(appinfo.getContent().size() + 1);
        list.add(appinfo.getSource());
        for (Object item : appinfo.getContent()) {
            if (item instanceof String) {
                list.add(((String) item).trim());
                continue;
            }
            list.add("#Appinfo : " + item.getClass().getSimpleName());
        }
        return String.join(", " + list);
    }

    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    private TempFacets readFacets(List<Object> facets) {
        Objects.requireNonNull(facets, "facets is null");
        /*
        @XmlElementRef(name = "enumeration", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "maxInclusive", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "minExclusive", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "length", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "totalDigits", namespace = "http://www.w3.org/2001/XMLSchema", type = TotalDigits.class, required = false),
        @XmlElementRef(name = "whiteSpace", namespace = "http://www.w3.org/2001/XMLSchema", type = WhiteSpace.class, required = false),
        @XmlElementRef(name = "maxLength", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "pattern", namespace = "http://www.w3.org/2001/XMLSchema", type = Pattern.class, required = false),
        @XmlElementRef(name = "minInclusive", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "minLength", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "fractionDigits", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "maxExclusive", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class, required = false)
         */
        TempFacets tempFacets = new TempFacets();

        Map<String, String> enumerationMap = new HashMap<>();
        Map<String, String> facetsMap = new HashMap<>();

        for (Object item : facets) {

            if (item instanceof JAXBElement) {
                JAXBElement element = (JAXBElement) item;
                Facet facet = (Facet) element.getValue();

                String facetName = element.getName().getLocalPart();

                if ("enumeration".equals(facetName)) {
                    String caption = null;
                    List<String> annotation = readAnnotation(facet.getAnnotation());
                    if (annotation != null) {
                        caption = String.join(", ", annotation).trim();
                    }
                    enumerationMap.put(facet.getValue(), caption);
                    continue;
                }

                facetsMap.put(facetName, facet.getValue());

                continue;
            }

            if (item instanceof TotalDigits) {
                facetsMap.put("totalDigits", ((TotalDigits) item).getValue());
                continue;
            }

            if (item instanceof WhiteSpace) {
                String whiteSpace = ((WhiteSpace) item).getValue();
                if (whiteSpace != null) {
                    tempFacets.setWhiteSpace(TempFacets.WhiteSpace.valueOf(whiteSpace.trim().toUpperCase()));
                }
                continue;
            }

            if (item instanceof Pattern) {
                if (tempFacets.getPatterns() == null) {
                    tempFacets.setPatterns(new ArrayList<>());
                }
                tempFacets.getPatterns().add(toTempFacetsPattern((Pattern) item));
                continue;
            }

            tempFacets.addComment("#Facets : " + item.getClass().getSimpleName());
        }

        if (!enumerationMap.isEmpty()) {
            tempFacets.setEnumeration(enumerationMap);
        }
        if (!facetsMap.isEmpty()) {
            tempFacets.setFacets(facetsMap);
        }

        return tempFacets;
    }


    //---------------------------------------------------------------------------

    private TempFacetsPattern toTempFacetsPattern(Pattern pattern) {
        TempFacetsPattern tempFacetsPattern = new TempFacetsPattern();

        tempFacetsPattern.setPattern(pattern.getValue());
        List<String> annotation = readAnnotation(pattern.getAnnotation());
        if (annotation != null) {
            tempFacetsPattern.setDescription(String.join(", ", annotation).trim());
        }

        return tempFacetsPattern;
    }

    //---------------------------------------------------------------------------

    private static String toString(List<String> list) {
        Objects.requireNonNull(list, "list is null");
        return new StringBuilder()
                .append("[").append(String.join(",", list)).append("]")
                .toString();
    }

    private static String toString(QName qName) {
        Objects.requireNonNull(qName, "qName is null");
        return new StringBuilder()
                .append("{").append(qName.getNamespaceURI()).append("}").append(qName.getLocalPart())
//                .append(qName.getPrefix()).append(":").append(qName.getLocalPart())
                .toString();
    }

    private static String toString(Wildcard wildcard) {
        Objects.requireNonNull(wildcard, "wildcard is null");
        return new StringBuilder()
                .append(wildcard.getProcessContents()).append("_").append(toString(wildcard.getNamespace())).append("_???")
                .toString();
    }

    private static String toString(Map<QName, String> otherAttributes) {
        Objects.requireNonNull(otherAttributes, "otherAttributes is null");
        List<String> list = new ArrayList<>();
        for (Map.Entry<QName, String> entry : otherAttributes.entrySet()) {
            list.add(toString(entry.getKey()) + " - " + entry.getValue());
        }
        return toString(list);
    }

}
