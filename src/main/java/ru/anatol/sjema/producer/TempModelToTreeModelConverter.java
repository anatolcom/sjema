package ru.anatol.sjema.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.model.view.ViewConst;
import ru.anatol.sjema.producer.id.IdManager;
import ru.anatol.sjema.producer.model.temp.TempElement;
import ru.anatol.sjema.producer.model.temp.TempGroup;
import ru.anatol.sjema.producer.model.temp.TempIdentifier;
import ru.anatol.sjema.producer.model.temp.TempModel;
import ru.anatol.sjema.producer.model.temp.TempSchema;
import ru.anatol.sjema.producer.model.temp.TempType;
import ru.anatol.sjema.producer.model.tree.TreeAny;
import ru.anatol.sjema.producer.model.tree.TreeModel;
import ru.anatol.sjema.producer.model.tree.TreeNode;
import ru.anatol.sjema.producer.model.tree.TreeSchema;
import ru.anatol.sjema.producer.model.tree.TreeType;
import ru.anatol.sjema.xml.Namespaces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TempModelToTreeModelConverter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TempModelToTreeModelConverter.class);

    private static final String ELEMENT = "Element";
    private static final String TYPE = "Type";
    private static final String GROUP = "Group";

    private final IdManager idManager = new IdManager();
    private final Numerator idNumerator = new Numerator();
    private final Numerator nsNumerator = new Numerator();
    private Namespaces namespaces;

//    private Map<TempIdentifier, TempElement> tempElementMap = new HashMap<>();
//    private Map<TempIdentifier, TempType> tempTypeMap = new HashMap<>();
//    private Map<TempIdentifier, TempGroup> tempGroupMap = new HashMap<>();

    private TempModel tempModel;

    private class Context {
        public final Context parent;
        public final TempIdentifier identifier;

        public Context(Context parent, TempIdentifier identifier) {
            this.parent = parent;
            this.identifier = identifier;
        }
    }

    public TreeModel convert(TempModel tempModel) throws ConverterException {
        try {
            return toTreeModel(tempModel);
        } catch (ConverterException ex) {
            idManager.print();
            throw ex;
        } catch (Exception ex) {
            idManager.print();
            throw new ConverterException(ex);
        }
    }

    private TreeModel toTreeModel(TempModel tempModel) throws ConverterException {

        this.tempModel = tempModel;

        final TreeModel treeModel = new TreeModel();

        namespaces = getNamespaces(tempModel.getTargetNamespace());

        treeModel.setTargetNamespace(tempModel.getTargetNamespace());

        final Set<TempIdentifier> rootTempElementIdentifiers = new HashSet<>();

        for (TempElement tempElement : tempModel.getElementSet()) {
            final String elementName = tempElement.getNameId().getName();
            final String elementId = idNumerator.getNumbered(elementName + ELEMENT);
            idManager.registerId(tempElement.getId(), TempIdentifier.Mode.ELEMENT, elementId, tempElement);

            if (tempElement.isRoot()
                    && tempModel.getTargetNamespace().equals(tempElement.getNameId().getNamespace())
                    && TempElement.Mode.ELEMENT.equals(tempElement.getMode())) {
                rootTempElementIdentifiers.add(tempElement.getId());
            }

//            tempElementMap.put(tempElement.getId(), tempElement);
        }
        for (TempType tempType : tempModel.getTypeSet()) {
            final String typeId;
            if (tempType.getName() == null) {
                typeId = idNumerator.getNumbered("unnamed" + TYPE);
            } else if (tempType.getName().endsWith(TYPE)) {
                typeId = idNumerator.getNumbered(tempType.getName());
            } else {
                typeId = idNumerator.getNumbered(tempType.getName() + TYPE);
            }
            idManager.registerId(tempType.getNameId(), TempIdentifier.Mode.TYPE_NAME, typeId, tempType);

//            tempTypeMap.put(tempType.getNameId(), tempType);
        }
        for (TempGroup tempGroup : tempModel.getGroupSet()) {
            idManager.registerId(tempGroup.getNameId(), TempIdentifier.Mode.GROUP_NAME, null, tempGroup);

//            tempGroupMap.put(tempGroup.getNameId(), tempGroup);
        }

        for (TempSchema tempSchema : tempModel.getSchemas()) {
            treeModel.getSchemas().add(toTreeSchema(tempSchema));
        }

        for (TempIdentifier identifier : rootTempElementIdentifiers) {
            treeModel.getNodes().add(toTreeNode(new Context(null, identifier)));
        }

        //Добавление нэймспэйсов
        for (String prefix : namespaces.getPrefixSet()) {
            treeModel.getNamespaces().put(prefix, namespaces.getNamespaceURI(prefix));
        }

        return treeModel;
    }

    private TreeSchema toTreeSchema(TempSchema tempSchema) {
        final TreeSchema treeSchema = new TreeSchema();
        treeSchema.setName(tempSchema.getName());
        treeSchema.setHash(tempSchema.getHash());
        treeSchema.setMode(toTreeSchemaMode(tempSchema.getMode()));
        treeSchema.setTargetNamespace(tempSchema.getTargetNamespace());
        return treeSchema;
    }

    private TreeSchema.Mode toTreeSchemaMode(TempSchema.Mode mode) {
        if (mode == null) {
            return null;
        }
        switch (mode) {
            case ROOT:
                return TreeSchema.Mode.ROOT;
            case INCLUDE:
                return TreeSchema.Mode.INCLUDE;
            case IMPORT:
                return TreeSchema.Mode.IMPORT;
            case REDEFINE:
                return TreeSchema.Mode.REDEFINE;
            default:
                throw new UnsupportedOperationException("unknown mode: " + mode);
        }
    }

    /**
     * Инициализация стандартных типов данных.
     *
     * @param targetNamespace целевой нэймспэйс
     */
    private Namespaces getNamespaces(String targetNamespace) throws ConverterException {

        final Namespaces namespaces = new Namespaces("tns", targetNamespace);

        final List<String> xmlTypes = new ArrayList<>();
        xmlTypes.addAll(XmlConst.typeIdSet);
        for (String name : xmlTypes) {
            final TempIdentifier typeIdentifier = new TempIdentifier(TempIdentifier.Mode.TYPE_NAME, XmlConst.URI, name);
            final TempType tempType = new TempType(TempType.Mode.SIMPLE);
            tempType.setName(name);
            tempType.setNameId(typeIdentifier);
            idManager.registerId(typeIdentifier, TempIdentifier.Mode.TYPE_NAME, name, tempType);
        }

        namespaces.addNamespace(XsdConst.PREFIX, XsdConst.URI);
        final List<String> xsdTypes = new ArrayList<>();
        xsdTypes.add(XsdConst.BASE_TYPE_ID);
        if (!XsdConst.URI.equals(targetNamespace)) {
            xsdTypes.addAll(XsdConst.typeIdSet);
        }
        for (String name : xsdTypes) {
            final TempIdentifier typeIdentifier = new TempIdentifier(TempIdentifier.Mode.TYPE_NAME, XsdConst.URI, name);
            final TempType tempType = new TempType(TempType.Mode.SIMPLE);
            tempType.setName(name);
            tempType.setNameId(typeIdentifier);
            idManager.registerId(typeIdentifier, TempIdentifier.Mode.TYPE_NAME, name, tempType);
        }

        return namespaces;
    }

    /**
     * Получение группы по идентификатору.
     *
     * @param identifier идентификатор
     * @param required   обязательность
     * @return группа
     * @throws ConverterException
     */
    private TempGroup getTempGroup(TempIdentifier identifier, boolean required) throws ConverterException {
        final TempGroup tempGroup = (TempGroup) idManager.getObject(identifier);
        if (tempGroup == null && required) {
            throw new ConverterException("temp group with id " + identifier + " not found");
        }
        if (tempGroup.getRefId() != null) {
            try {
                return getTempGroup(tempGroup.getRefId(), required);
            } catch (ConverterException ex) {
                throw new ConverterException("getting ref " + tempGroup.getRefId() + " for temp group with id " + identifier + " failure, because: " + ex.getMessage(), ex);
            }
        }

        return tempGroup;
    }

    /**
     * Получение типа по идентификатору.
     *
     * @param identifier идентификатор
     * @param required   обязательность
     * @return тип
     * @throws ConverterException
     */
    private TempType getTempType(TempIdentifier identifier, boolean required) throws ConverterException {
        TempType tempType = (TempType) idManager.getObject(identifier);
        if (tempType == null && required) {
            throw new ConverterException("temp type with id " + identifier + " not found");
        }
        return tempType;
    }

    /**
     * Получение элемент по идентификатору.
     *
     * @param identifier идентификатор
     * @param required   обязательность
     * @return тип
     * @throws ConverterException
     */
    private TempElement getTempElement(TempIdentifier identifier, boolean required) throws ConverterException {
        final TempElement tempElement = (TempElement) idManager.getObject(identifier);
        if (tempElement == null && required) {
            throw new ConverterException("temp element with id " + identifier + " not found");
        }
        if (tempElement.getRefId() != null) {
            try {
                return getTempElement(tempElement.getRefId(), required);
            } catch (ConverterException ex) {
                throw new ConverterException("getting ref " + tempElement.getRefId() + " for temp element with id " + identifier + " failure, because: " + ex.getMessage(), ex);
            }
        }
        return tempElement;
    }

    /**
     * @param tempType
     * @return
     */
    private boolean isSimpleType(TempType tempType) {
        switch (tempType.getMode()) {
            case SIMPLE:
                return true;
            case ATTRIBUTE_GROUP:
                return true;
        }
        return false;
    }

    private TreeNode toTreeNode(Context context) throws ConverterException {
        final TempElement tempElement = getTempElement(context.identifier, true);

        final TempType tempType = getTempType(tempElement);

        final TreeNode treeNode = new TreeNode(tempElement.getId());


        final String nodeName;
        final String nodeNamespace;
        if (tempElement.getNameId() != null) {
            nodeName = tempElement.getNameId().getName();
            nodeNamespace = tempElement.getNameId().getNamespace();
        } else {
            nodeName = null;
            nodeNamespace = null;
        }

        treeNode.setName(nodeName);
        treeNode.setNamespace(nodeNamespace);
        treeNode.setMode(toTreeNodeMode(tempElement));
        treeNode.setType(getTreeType(tempType));
        treeNode.setPath(generatePath(tempElement.getMode(), tempElement.getForm(), nodeNamespace, nodeName));
        if (tempElement.getAnnotation() != null) {
            treeNode.setAnnotations(tempElement.getAnnotation());
        }

        //если тип имеет содержимое
        if (tempType != null && tempType.getContentId() != null) {
            treeNode.getNodes().addAll(toElementIds(context, tempType.getContentId()));
        }

        return treeNode;
    }

    private TreeType getTreeType(TempType tempType) {
        final TreeType treeType;
        if (tempType != null) {
            final String typeName = tempType.getName();
            final String typeNamespace;
            if (tempType.getNameId() != null) {
                typeNamespace = tempType.getNameId().getNamespace();
            } else {
                typeNamespace = null;
            }
            treeType = new TreeType(tempType.getId());
            treeType.setName(typeName);
            treeType.setNamespace(typeNamespace);
            treeType.setMode(toTreeTypeMode(tempType));
            treeType.setAnnotations(tempType.getAnnotation());
        } else {
            treeType = null;
        }
        return treeType;
    }

    private TempType getTempType(TempElement tempElement) throws ConverterException {
        if (tempElement.getTypeId() != null) {
            return getTempType(tempElement.getTypeId(), true);
        }
        if (tempElement.getRefId() != null) {
            final TempElement refTempElement = getTempElement(tempElement.getRefId(), true);
            return getTempType(refTempElement.getTypeId(), true);
        }
        return null;
    }

    private TreeType.Mode toTreeTypeMode(TempType tempType) {
        if (isSimpleType(tempType)) {
            return TreeType.Mode.SIMPLE;
        }
        return TreeType.Mode.COMPLEX;
    }

    private TreeNode.Mode toTreeNodeMode(TempElement tempElement) {
        switch (tempElement.getMode()) {
            case ELEMENT:
                return TreeNode.Mode.ELEMENT;
            case ATTRIBUTE:
                return TreeNode.Mode.ATTRIBUTE;
            default:
                throw new UnsupportedOperationException("unsupported mode " + tempElement.getMode().name());
        }
    }

    private TreeNode.Mode toTreeNodeMode(TempGroup tempGroup) {
        switch (tempGroup.getMode()) {
            case ANY:
                return TreeNode.Mode.ELEMENT;
            case ANY_ATTRIBUTES:
                return TreeNode.Mode.ATTRIBUTE;
            default:
                throw new UnsupportedOperationException("unsupported mode " + tempGroup.getMode().name());
        }
    }

    private String generatePath(TempElement.Mode mode, TempElement.Form form, String namespace, String name) {
        StringBuilder pathBuilder = new StringBuilder();
        switch (mode) {
            case ATTRIBUTE:
                pathBuilder.append("@");
                if (form == null) {
                    form = tempModel.getAttributeFormDefault();
                }
                break;
            case ELEMENT:
                if (form == null) {
                    form = tempModel.getElementFormDefault();
                }
                break;
            default:
                throw new UnsupportedOperationException("unsupported mode " + mode.name());
        }

        if (TempElement.Form.QUALIFIED.equals(form)) {
            String prefix = namespaces.getPrefix(namespace);
            if (prefix == null) {
                prefix = nsNumerator.getNumbered("ns");
                namespaces.addNamespace(prefix, namespace);
            }
            pathBuilder.append(prefix).append(":");
        }
        pathBuilder.append(name);
        return pathBuilder.toString();
    }

    private List<TreeNode> toElementIds(Context context, TempIdentifier identifier) throws ConverterException {
        Objects.requireNonNull(identifier, "identifier is null");

        final List<TreeNode> elementIds = new ArrayList<>();
        switch (identifier.getMode()) {
            case ELEMENT:
                final TempElement tempElement = getTempElement(identifier, true);

                elementIds.add(toTreeNode(new Context(context, tempElement.getId())));
                break;
            case GROUP_NAME:
                final TempGroup tempGroup = getTempGroup(identifier, true);
                if (tempGroup.getExtensionId() != null) {
                    TempType tempType = getTempType(tempGroup.getExtensionId(), true);
                    if (isSimpleType(tempType)) {
                        elementIds.addAll(toSimpleTypeElementIds(context, tempGroup.getExtensionId()));
                    } else {
                        elementIds.addAll(toComplexTypeElementIds(context, tempGroup.getExtensionId()));
                    }
                }
                if (tempGroup.getRestriction() != null && tempGroup.getRestriction().getTypeId() != null) {
                    TempType tempType = getTempType(tempGroup.getRestriction().getTypeId(), true);
                    if (isSimpleType(tempType)) {
                        elementIds.addAll(toSimpleTypeElementIds(context, tempGroup.getRestriction().getTypeId()));
                    } else {
                        elementIds.addAll(toComplexTypeElementIds(context, tempGroup.getRestriction().getTypeId()));
                    }
                }
                if (tempGroup.getIds() != null && !tempGroup.getIds().isEmpty()) {
                    if (TempGroup.Mode.CHOICE.equals(tempGroup.getMode())) {
//                        elementIds.add(toChoiceElementId(tempGroup));
                    } else if (tempGroup.getRestriction() != null
                            && ViewConst.getMaxOccurs(tempGroup.getRestriction().getMaxOccurs()) > 1) {
//                        elementIds.add(toRepeatableElementId(tempGroup));
                    } else if (TempGroup.Mode.NAMED_GROUP.equals(tempGroup.getMode())
                            || TempGroup.Mode.GROUP_REF.equals(tempGroup.getMode())) {
//                        elementIds.add(toGroupElementId(tempGroup));
                    } else {
                        elementIds.addAll(toSequenceElementIds(context, tempGroup.getIds()));
                    }
                } else {
                    if (TempGroup.Mode.ANY.equals(tempGroup.getMode())) {
                        elementIds.add(toAnyElementId(tempGroup));
                    } else if (TempGroup.Mode.ANY_ATTRIBUTES.equals(tempGroup.getMode())) {
                        elementIds.add(toAnyElementId(tempGroup));
                    }
                }
                break;
            default:
                LOGGER.error("unsupported identifier mode: {}", identifier.getMode());
                throw new ConverterException("unsupported identifier mode: " + identifier.getMode());
        }
        return elementIds;
    }

    private TreeNode toAnyElementId(TempGroup tempGroup) {
        final String nodeName = null;
        final String nodeNamespace = null;

        final TreeAny any = new TreeAny();
        any.setProcessContents(tempGroup.getAny().getProcessContents());
        any.setNamespaces(tempGroup.getAny().getNamespaces());

        final TreeNode treeNode = new TreeNode(tempGroup.getId());
        treeNode.setName(nodeName);
        treeNode.setNamespace(nodeNamespace);
        treeNode.setMode(toTreeNodeMode(tempGroup));
        treeNode.setType(null);
        treeNode.setPath(null);
        treeNode.setAny(any);
        treeNode.setAnnotations(tempGroup.getAnnotation());

        return treeNode;
    }

    private List<TreeNode> toSimpleTypeElementIds(Context context, TempIdentifier baseId) throws ConverterException {

        List<TreeNode> treeNodes = new ArrayList<>();

        if (XsdConst.URI.equals(baseId.getNamespace())
                && (XsdConst.BASE_TYPE_ID.equals(baseId.getName()) || XsdConst.typeIdSet.contains(baseId.getName()))) {
            return treeNodes;
        }
        TempType tempType = getTempType(baseId, true);

        //если простой тип - UNION
//        if (isSimpleType(tempType) && tempType.getUnion() != null) {
//            treeNodes.add(toUnionElementId(tempType));
//        }

        //если тип имеет содержимое
        if (tempType.getContentId() != null) {
            treeNodes.addAll(toElementIds(context, tempType.getContentId()));
        }

        if (tempType.getReference() != null && tempType.getReference().getBaseId() != null) {
            if (isSimpleType(tempType)) {
                treeNodes.addAll(toSimpleTypeElementIds(context, tempType.getReference().getBaseId()));
            }
        }

        return treeNodes;
    }

    private List<TreeNode> toComplexTypeElementIds(Context context, TempIdentifier baseId) throws ConverterException {

        List<TreeNode> treeNodes = new ArrayList<>();

        if (baseId == null) {
            return treeNodes;
        }

        TempType tempType = getTempType(baseId, true);

        if (tempType.getReference() != null && tempType.getReference().getBaseId() != null) {
            if (!isSimpleType(tempType)) {
                if (TempType.Mode.EXTENSION.equals(tempType.getMode())) {
                    treeNodes.addAll(toComplexTypeElementIds(context, tempType.getReference().getBaseId()));
                }
                if (TempType.Mode.RESTRICTION.equals(tempType.getMode())) {
                    LOGGER.debug("RESTRICTION {}", baseId);
                }
            }
        }

        //если тип имеет содержимое
        if (tempType.getContentId() != null) {
            treeNodes.addAll(toElementIds(context, tempType.getContentId()));
        }

        return treeNodes;
    }

    private List<TreeNode> toSequenceElementIds(Context context, List<TempIdentifier> ids) throws ConverterException {
        List<TreeNode> treeNodes = new ArrayList<>(ids.size());
        for (TempIdentifier tempIdentifier : ids) {
            List<TreeNode> groupTreeNodes = toElementIds(context, tempIdentifier);
            if (groupTreeNodes != null) {
                treeNodes.addAll(groupTreeNodes);
            }
        }
        return treeNodes;
    }
}
