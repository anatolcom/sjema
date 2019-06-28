package ru.anatol.sjema.producer;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.model.BaseType;
import ru.anatol.sjema.model.view.ViewConst;
import ru.anatol.sjema.model.view.ViewContent;
import ru.anatol.sjema.model.view.ViewElement;
import ru.anatol.sjema.model.view.ViewElementRepeatable;
import ru.anatol.sjema.model.view.ViewMapper;
import ru.anatol.sjema.model.view.ViewModel;
import ru.anatol.sjema.model.view.ViewType;
import ru.anatol.sjema.model.view.ViewTypeRestriction;
import ru.anatol.sjema.model.view.ViewTypeRestrictionPattern;
import ru.anatol.sjema.model.view.ViewWidget;
import ru.anatol.sjema.producer.id.IdManager;
import ru.anatol.sjema.producer.model.temp.TempElement;
import ru.anatol.sjema.producer.model.temp.TempElementRestriction;
import ru.anatol.sjema.producer.model.temp.TempFacets;
import ru.anatol.sjema.producer.model.temp.TempFacetsPattern;
import ru.anatol.sjema.producer.model.temp.TempGroup;
import ru.anatol.sjema.producer.model.temp.TempGroupRestriction;
import ru.anatol.sjema.producer.model.temp.TempIdentifier;
import ru.anatol.sjema.producer.model.temp.TempModel;
import ru.anatol.sjema.producer.model.temp.TempType;
import ru.anatol.sjema.producer.model.temp.TempTypeReference;
import ru.anatol.sjema.producer.standard.StandardViewMapperProducer;
import ru.anatol.sjema.producer.standard.StandardViewStructureProducer;
import ru.anatol.sjema.producer.standard.StandardViewWidgetProducer;
import ru.anatol.sjema.xml.Namespaces;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TempModelToViewModelConverter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TempModelToViewModelConverter.class);

    private static final String ELEMENT = "Element";
    private static final String TYPE = "Type";
    private static final String MAPPER = "Mapper";

    private static final String GROUP = "Group";
    private static final String GROUP_DEFAULT_CAPTION = null;

    private static final String CHOICE = "Choice";
    private static final String CHOICE_DEFAULT_CAPTION = "Вариант вводимых данных";

    private static final String UNION = "Union";
    private static final String UNION_DEFAULT_CAPTION = "Вариант вводимых данных";
    private static final String UNION_VARIANT = "Variant";
    private static final String UNION_VARIANT_DEFAULT_CAPTION = "Вариант";

    private static final String REPEATABLE = "Repeatable";
    private static final String REPEATABLE_DEFAULT_CAPTION = "Повторяющиеся данные";


    private static final String BOOLEAN_PATTERN = "true|false";
    private static final String NUMBER_PATTERN = "[\\-+]?[0-9]+";
    private static final String UNSIGNED_NUMBER_PATTERN = "[0-9]+";

    private static final TempIdentifier STRING_TYPE_ID = new TempIdentifier(TempIdentifier.Mode.TYPE_NAME, XsdConst.URI, XsdConst.STRING_TYPE_ID);

    private TempModel tempModel;
    private ViewModel viewModel;

    private final IdManager idManager = new IdManager();
    private final Numerator idNumerator = new Numerator();
    private final Numerator nsNumerator = new Numerator();
    private Namespaces namespaces;

    private final Map<TempIdentifier, TempIdentifier> baseTypeIdMap = new HashMap<>();
    private final Map<TempIdentifier, String> mapperIdMap = new HashMap<>();

    private ViewStructureProducer viewStructureProducer;
    private ViewMapperProducer viewMapperProducer;
    private ViewWidgetProducer viewWidgetProducer;

    public TempModelToViewModelConverter() {

    }

    public ViewStructureProducer getViewStructureProducer() {
        if (viewStructureProducer == null) {
            viewStructureProducer = new StandardViewStructureProducer();
        }
        return viewStructureProducer;
    }

    public void setViewStructureProducer(ViewStructureProducer viewStructureProducer) {
        this.viewStructureProducer = viewStructureProducer;
    }

    public ViewMapperProducer getViewMapperProducer() {
        if (viewMapperProducer == null) {
            viewMapperProducer = new StandardViewMapperProducer();
        }
        return viewMapperProducer;
    }

    public void setViewMapperProducer(ViewMapperProducer viewMapperProducer) {
        this.viewMapperProducer = viewMapperProducer;
    }

    public ViewWidgetProducer getViewWidgetProducer() {
        if (viewWidgetProducer == null) {
            viewWidgetProducer = new StandardViewWidgetProducer();
        }
        return viewWidgetProducer;
    }

    public void setViewWidgetProducer(ViewWidgetProducer viewWidgetProducer) {
        this.viewWidgetProducer = viewWidgetProducer;
    }

    public ViewModel convert(TempModel tempModel) throws ConverterException {
        try {
            viewStructureProducer = getViewStructureProducer();
            viewMapperProducer = getViewMapperProducer();
            viewWidgetProducer = getViewWidgetProducer();

            return toViewModel(tempModel);
        } catch (ConverterException ex) {
            idManager.print();
            throw ex;
        } catch (Exception ex) {
            idManager.print();
            throw new ConverterException(ex);
        }
    }

    private ViewModel toViewModel(TempModel tempModel) throws ConverterException, ProducerException {
        Objects.requireNonNull(tempModel);
        Objects.requireNonNull(tempModel.getElementSet());
        Objects.requireNonNull(tempModel.getTypeSet());
        Objects.requireNonNull(tempModel.getGroupSet());

        this.tempModel = tempModel;

        viewModel = new ViewModel();

        viewModel.setTargetNamespace(tempModel.getTargetNamespace());

        namespaces = getNamespaces(tempModel.getTargetNamespace());

        final Map<String, String> rootElementIdMap = new HashMap<>();

        // Инициализация идентификаторов элементов
        for (TempElement tempElement : tempModel.getElementSet()) {
            final String elementName = tempElement.getNameId().getName();
            final String elementId = idNumerator.getNumbered(elementName + ELEMENT);
            idManager.registerId(tempElement.getId(), TempIdentifier.Mode.ELEMENT, elementId, tempElement);
//            idManager.registerId(tElement.getNameId(), TempIdentifier.Mode.ELEMENT_NAME, elementId, tElement);
            if (tempElement.isRoot()
                    && viewModel.getTargetNamespace().equals(tempElement.getNameId().getNamespace())
                    && TempElement.Mode.ELEMENT.equals(tempElement.getMode())) {
                rootElementIdMap.put(elementName, elementId);
            }
        }

        // Инициализация идентификаторов типов
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


        // Инициализация идентификаторов групп
        for (TempGroup tempGroup : tempModel.getGroupSet()) {
            idManager.registerId(tempGroup.getNameId(), TempIdentifier.Mode.GROUP_NAME, null, tempGroup);
        }

        viewModel.setStructure(new HashMap<>());
        viewModel.setElements(new HashMap<>());
        viewModel.setTypes(new HashMap<>());
        viewModel.setMappers(new HashMap<>());

        // Добавление элементов
        for (TempElement tempElement : tempModel.getElementSet()) {
            final TempElement tElement = getTempElement(tempElement.getId(), true);
            final ViewElement viewElement = toViewElement(tElement);
            if (viewElement != null) {
                String id = idManager.getId(tElement.getId());
                viewModel.getElements().put(id, viewElement);
            }
        }

        // Добавление типов
        for (TempType tempType : tempModel.getTypeSet()) {
            final ViewType viewType = toViewType(tempType);
            if (viewType != null) {
                String id = idManager.getId(tempType.getNameId());
                viewModel.getTypes().put(id, viewType);
            }
        }

        //Добавление нэймспэйсов
        viewModel.setNamespaces(new HashMap<>());
        for (String prefix : namespaces.getPrefixSet()) {
            viewModel.getNamespaces().put(prefix, namespaces.getNamespaceURI(prefix));
        }

        // Добавление структуры
        if (viewStructureProducer != null) {
            for (Map.Entry<String, String> entry : rootElementIdMap.entrySet()) {
                final JSONObject structure = viewStructureProducer.produce(viewModel, entry.getValue(), entry.getKey());
                if (structure != null) {
                    viewModel.getStructure().put(entry.getValue(), structure);
                }
            }
        }

        return viewModel;
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
            idManager.registerId(typeIdentifier, TempIdentifier.Mode.TYPE_NAME, name, tempType);
        }

        return namespaces;
    }

    /**
     * Преобразование во ViewElement
     *
     * @param tempElement TempElement
     * @return ViewElement
     */
    private ViewElement toViewElement(TempElement tempElement) throws ConverterException {
        Objects.requireNonNull(tempElement);

        try {
            ViewElement viewElement = new ViewElement();
            viewElement.setIdentifier(tempElement.getId());
            if (tempElement.getTypeId() != null) {
                viewElement.setTypeId(idManager.getId(processXsdTypeId(tempElement.getTypeId())));
                viewElement.setTypeIdentifier(tempElement.getTypeId());
            }

            Objects.requireNonNull(tempElement.getMode(), "element mode is null");
            Objects.requireNonNull(tempElement.getNameId(), "element name is null");

            // Вычисление пути
            viewElement.setPath(getViewElementPath(tempElement));
            viewElement.setCreateEmpty(tempElement.isNillable());

            // Вычисление заголовка
            viewElement.setCaption(getCaption(tempElement.getAnnotation()));
            if (viewElement.getCaption() == null) {
                viewElement.setCaption(tempElement.getNameId().getName());
            }
            viewElement.setDescription(getDescription(tempElement.getAnnotation()));

            if (tempElement.getRestriction() != null) {
                viewElement.setRepeatable(toViewElementRepeatable(tempElement.getRestriction()));
                if (viewElement.getRepeatable() == null) {
                    viewElement.setRequired(toViewElementRequired(tempElement.getRestriction()));
                }
            }

            //только для чтения
            viewElement.setReadOnly(tempElement.isConstant());
            //значение
            viewElement.setDefaultValue(tempElement.getValue());

            return viewElement;
        } catch (Exception ex) {
            throw new ConverterException("convert element: " + tempElement.getNameId() + " failure, because: " + ex.getMessage(), ex);
        }
    }

    /**
     * Получение пути.
     *
     * @param tempElement
     * @return
     */
    private String getViewElementPath(TempElement tempElement) {
        TempIdentifier identifier = tempElement.getNameId();
        return generatePath(tempElement.getMode(), tempElement.getForm(), identifier.getNamespace(), identifier.getName());
    }

    /**
     * Генерация пути.
     *
     * @param mode      режим
     * @param namespace нэймсэйс
     * @param name      имя
     * @return
     */
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

    /**
     * Преобразование в required.
     *
     * @param tempElementRestriction TempElementRestriction
     * @return required
     */
    private boolean toViewElementRequired(TempElementRestriction tempElementRestriction) {
        Objects.requireNonNull(tempElementRestriction);
        return ViewConst.getMinOccurs(tempElementRestriction.getMinOccurs()) > 0;
    }

    /**
     * Преобразование в required.
     *
     * @param tempGroupRestriction TempGroupRestriction
     * @return required
     */
    private boolean toViewElementRequired(TempGroupRestriction tempGroupRestriction) {
        Objects.requireNonNull(tempGroupRestriction);
        return ViewConst.getMinOccurs(tempGroupRestriction.getMinOccurs()) > 0;
    }

    /**
     * Преобразование в ViewElementRepeatable.
     *
     * @param tempElementRestriction TempElementRestriction
     * @return ViewElementRepeatable
     */
    private ViewElementRepeatable toViewElementRepeatable(TempElementRestriction tempElementRestriction) {
        Objects.requireNonNull(tempElementRestriction);
        final int minOccurs = ViewConst.getMinOccurs(tempElementRestriction.getMinOccurs());
        final int maxOccurs = ViewConst.getMaxOccurs(tempElementRestriction.getMaxOccurs());
        if (maxOccurs == 1) {
            return null;
        }
        ViewElementRepeatable viewElementRepeatable = new ViewElementRepeatable();
        viewElementRepeatable.setMin(minOccurs);
        viewElementRepeatable.setMax(maxOccurs);
        return viewElementRepeatable;
    }

    /**
     * Преобразование в ViewElementRepeatable.
     *
     * @param tempGroupRestriction TempGroupRestriction
     * @return ViewElementRepeatable
     */
    private ViewElementRepeatable toViewElementRepeatable(TempGroupRestriction tempGroupRestriction) {
        Objects.requireNonNull(tempGroupRestriction);
        final int minOccurs = ViewConst.getMinOccurs(tempGroupRestriction.getMinOccurs());
        final int maxOccurs = ViewConst.getMaxOccurs(tempGroupRestriction.getMaxOccurs());
        if (maxOccurs == 1) {
            return null;
        }
        ViewElementRepeatable viewElementRepeatable = new ViewElementRepeatable();
        viewElementRepeatable.setMin(minOccurs);
        viewElementRepeatable.setMax(maxOccurs);
        return viewElementRepeatable;
    }

    /**
     * Преобразование в ViewType
     *
     * @param tempType TempType
     * @return ViewType
     */
    private ViewType toViewType(TempType tempType) throws ConverterException {
        Objects.requireNonNull(tempType);

        try {
            ViewType viewType = new ViewType();

            if (tempType.getNameId() != null) {
                viewType.setIdentifier(tempType.getNameId());
            } else {
                LOGGER.debug("nameId not set for TempType ({})", tempType.getId());
            }

            viewType.setDescription(getDescription(tempType.getAnnotation()));


            ViewContent viewContent = new ViewContent();
            viewContent.setMode(ViewContent.Mode.SEQUENCE);
            viewContent.setElementIds(new ArrayList<>());

            //если простой тип - UNION
            if (isSimpleType(tempType) && tempType.getUnion() != null) {
                viewContent.getElementIds().add(toUnionElementId(tempType));
            }

            //если тип имеет содержимое
            if (tempType.getContentId() != null) {
                viewContent.getElementIds().addAll(toElementIds(tempType.getContentId()));
            }

            //TODO simpleType с атрибутами это не простой тип
            if (tempType.getReference() != null && tempType.getReference().getBaseId() != null) {

                TempIdentifier baseId = tempType.getReference().getBaseId();
                if (isSimpleType(tempType)) {
                    viewContent.getElementIds().addAll(toSimpleTypeElementIds(baseId));
                    baseId = getStandardSimpleBaseId(baseId);
                }

                if (isSimpleType(tempType) && tempType.getUnion() == null) {
                    viewType.setRestriction(toViewTypeRestriction(tempType.getReference()));
                    viewType.setBase(getBaseType(baseId));
                    viewType.setBaseIdentifier(baseId);
                    viewType.setMapperId(getMapperId(baseId));
                    viewType.setWidget(getWidget(baseId));
                }

                //TODO возможно именно тут EXTENSION и RESTRICTION
                if (!isSimpleType(tempType)) {
                    switch (tempType.getMode()) {
                        case EXTENSION:
                            //TODO EXTENSION
                            LOGGER.debug("TempType is COMPLEX EXTENSION ({})", tempType.getId());
                            viewContent.getElementIds().addAll(toComplexTypeElementIds(baseId));
                            break;
                        case RESTRICTION:
                            //TODO RESTRICTION
                            LOGGER.debug("TempType is COMPLEX RESTRICTION ({})", tempType.getId());
                            break;
                        default:
                            viewType.setBase(getBaseType(baseId));
                            viewType.setBaseIdentifier(baseId);
                            viewType.setMapperId(getMapperId(baseId));
                            viewType.setWidget(getWidget(baseId));
                    }
                }
            }
//        viewType.setContent(toViewContent(tempType));

            if (viewContent.getElementIds() != null && !viewContent.getElementIds().isEmpty()) {
                viewType.setContent(viewContent);
            } else {
                if (!isSimpleType(tempType)) {
                    LOGGER.debug("viewType {} without content", viewType.getIdentifier());
                }
            }

            return viewType;
        } catch (Exception ex) {
            throw new ConverterException("convert type: " + tempType.getNameId() + " failure, because: " + ex.getMessage(), ex);
        }
    }

    private TempIdentifier getStandardSimpleBaseId(TempIdentifier baseId) throws ConverterException {
        if (XsdConst.URI.equals(baseId.getNamespace())
                && (XsdConst.BASE_TYPE_ID.equals(baseId.getName()) || XsdConst.typeIdSet.contains(baseId.getName()))) {
            return baseId;
        }

        final TempType tempType = getTempType(baseId, true);

        if (!isSimpleType(tempType)) {
            throw new ConverterException("baseIs is not simple type");
        }
        if (tempType.getReference() == null || tempType.getReference().getBaseId() == null) {
            return STRING_TYPE_ID;
        }
        return getStandardSimpleBaseId(tempType.getReference().getBaseId());
    }

    private List<String> toSimpleTypeElementIds(TempIdentifier typeIdentifier) throws ConverterException, ProducerException {

        List<String> elementIds = new ArrayList<>();

        if (XsdConst.URI.equals(typeIdentifier.getNamespace())
                && (XsdConst.BASE_TYPE_ID.equals(typeIdentifier.getName()) || XsdConst.typeIdSet.contains(typeIdentifier.getName()))) {
            return elementIds;
        }
        TempType tempType = getTempType(typeIdentifier, true);

        //если простой тип - UNION
        if (isSimpleType(tempType) && tempType.getUnion() != null) {
            elementIds.add(toUnionElementId(tempType));
        }

        //если тип имеет содержимое
        if (tempType.getContentId() != null) {
            elementIds.addAll(toElementIds(tempType.getContentId()));
        }

        if (tempType.getReference() != null && tempType.getReference().getBaseId() != null) {
            if (isSimpleType(tempType)) {
                elementIds.addAll(toSimpleTypeElementIds(tempType.getReference().getBaseId()));
            }
        }

        return elementIds;
    }

    private List<String> toComplexTypeElementIds(TempIdentifier typeIdentifier) throws ConverterException, ProducerException {

        List<String> elementIds = new ArrayList<>();

        if (typeIdentifier == null) {
            return elementIds;
        }

        TempType tempType = getTempType(typeIdentifier, true);

        if (tempType.getReference() != null && tempType.getReference().getBaseId() != null) {
            if (TempType.Mode.EXTENSION.equals(tempType.getMode())) {
                elementIds.addAll(toComplexTypeElementIds(tempType.getReference().getBaseId()));
            }
            if (TempType.Mode.RESTRICTION.equals(tempType.getMode())) {
                LOGGER.debug("RESTRICTION {}", typeIdentifier);
            }
        }

        //если тип имеет содержимое
        if (tempType.getContentId() != null) {
            elementIds.addAll(toElementIds(tempType.getContentId()));
        }

        return elementIds;
    }

    /**
     * Преобразование в ViewTypeRestriction
     *
     * @param tempTypeReference TempTypeRestriction
     * @return ViewTypeRestriction
     */
    private ViewTypeRestriction toViewTypeRestriction(TempTypeReference tempTypeReference) throws ConverterException {
        Objects.requireNonNull(tempTypeReference);
        Objects.requireNonNull(tempTypeReference.getBaseId());

        final TempType tempType = getTempType(tempTypeReference.getBaseId(), false);
        final String xsdTypeId = getXsdTypeId(tempTypeReference.getBaseId());
        ViewTypeRestriction viewTypeRestriction = null;
        if (tempType != null) {
            if (tempType.getReference() != null && tempType.getReference().getBaseId() != null) {
                viewTypeRestriction = toViewTypeRestriction(tempType.getReference());
            }
        } else {
            viewTypeRestriction = toBaseTypeRestriction(xsdTypeId);
        }

        if (tempTypeReference.getFacets() != null) {
            if (viewTypeRestriction == null) {
                viewTypeRestriction = new ViewTypeRestriction();
            }

            Map<String, String> enumeration = tempTypeReference.getFacets().getEnumeration();
            if (enumeration != null) {
                viewTypeRestriction.setEnumeration(new HashMap<>());
                for (Map.Entry<String, String> entry : enumeration.entrySet()) {
                    String caption = entry.getValue();
                    if (caption == null) {
                        caption = entry.getKey();
                    }
                    viewTypeRestriction.getEnumeration().put(entry.getKey(), caption);
                }
            }

            if (tempTypeReference.getFacets().getPatterns() != null && !tempTypeReference.getFacets().getPatterns().isEmpty()) {
                if (viewTypeRestriction.getPatterns() == null) {
                    viewTypeRestriction.setPatterns(new ArrayList<>(tempTypeReference.getFacets().getPatterns().size()));
                }
                for (TempFacetsPattern pattern : tempTypeReference.getFacets().getPatterns()) {
                    viewTypeRestriction.getPatterns().add(toViewTypeRestrictionPattern(pattern));
                }
            }

            TempFacets.WhiteSpace whiteSpace = tempTypeReference.getFacets().getWhiteSpace();
            if (whiteSpace != null) {
                viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.valueOf(whiteSpace.name()));
            }

            Map<String, String> facets = tempTypeReference.getFacets().getFacets();
            if (facets != null) {
                for (Map.Entry<String, String> entry : facets.entrySet()) {
                    readRestrictionFacet(viewTypeRestriction, xsdTypeId, entry.getKey(), entry.getValue());
                }
            }
        }

        return viewTypeRestriction;
    }

    private ViewTypeRestrictionPattern toViewTypeRestrictionPattern(TempFacetsPattern tempFacetsPattern) {
        Objects.requireNonNull(tempFacetsPattern);

        final ViewTypeRestrictionPattern viewPattern = new ViewTypeRestrictionPattern();
        viewPattern.setPattern(tempFacetsPattern.getPattern());
        viewPattern.setDescription(tempFacetsPattern.getDescription());
        return viewPattern;
    }

    private String getXsdTypeId(TempIdentifier baseId) {
        if (XsdConst.URI.equals(baseId.getNamespace())) {
            return baseId.getName();
        }
        return null;
    }

    private void readRestrictionFacet(ViewTypeRestriction viewTypeRestriction, String typeId, String facetName, String facetValue) throws ConverterException {
        try {
            switch (facetName) {
                case "length":
                    viewTypeRestriction.setLength(parseNumberAndTruncatedToLong(facetValue));
                    break;
                case "minLength":
                    viewTypeRestriction.setMinLength(parseNumberAndTruncatedToLong(facetValue));
                    break;
                case "maxLength":
                    viewTypeRestriction.setMaxLength(parseNumberAndTruncatedToLong(facetValue));
                    break;
                case "minInclusive":
                    if (XsdConst.isNumberTypeId(typeId)) {
                        viewTypeRestriction.setMinInclusive(parseNumberAndTruncatedToLong(facetValue));
                    }
                    break;
                case "maxInclusive":
                    if (XsdConst.isNumberTypeId(typeId)) {
                        viewTypeRestriction.setMaxInclusive(parseNumberAndTruncatedToLong(facetValue));
                    }
                    break;
                case "minExclusive":
                    if (XsdConst.isNumberTypeId(typeId)) {
                        viewTypeRestriction.setMinExclusive(parseNumberAndTruncatedToLong(facetValue));
                    }
                    break;
                case "maxExclusive":
                    if (XsdConst.isNumberTypeId(typeId)) {
                        viewTypeRestriction.setMaxExclusive(parseNumberAndTruncatedToLong(facetValue));
                    }
                    break;
                case "totalDigits":
                    if (XsdConst.isNumberTypeId(typeId)) {
                        viewTypeRestriction.setTotalDigits(parseNumberAndTruncatedToInt(facetValue));
                    }
                    break;
                case "fractionDigits":
                    if (XsdConst.isNumberTypeId(typeId)) {
                        viewTypeRestriction.setFractionDigits(parseNumberAndTruncatedToInt(facetValue));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("unknown facet \"" + facetName + "\"");
            }
        } catch (Exception ex) {
            throw new ConverterException("facet: \"" + facetName + "\" failure: " + ex.getMessage(), ex);
        }
    }

    /**
     * Преобразование в список id дочерних элементов
     *
     * @param identifier идентификатор ELEMENT или GROUP_NAME
     * @return id элемента
     * @throws ConverterException
     */
    private List<String> toElementIds(TempIdentifier identifier) throws ConverterException, ProducerException {
        if (identifier == null) {
            return null;
        }
        List<String> elementIds = new ArrayList<>();
        switch (identifier.getMode()) {
            case ELEMENT:
                final TempElement tempElement = getTempElement(identifier, true);
                elementIds.add(idManager.getId(tempElement.getId()));
                break;
            case GROUP_NAME:
                final TempGroup tempGroup = getTempGroup(identifier, true);
                if (tempGroup.getExtensionId() != null) {
                    TempType tempType = getTempType(tempGroup.getExtensionId(), true);
                    if (isSimpleType(tempType)) {
                        elementIds.addAll(toSimpleTypeElementIds(tempGroup.getExtensionId()));
                    } else {
                        elementIds.addAll(toComplexTypeElementIds(tempGroup.getExtensionId()));
                    }
                }
                if (tempGroup.getRestriction() != null && tempGroup.getRestriction().getTypeId() != null) {
                    TempType tempType = getTempType(tempGroup.getRestriction().getTypeId(), true);
                    if (isSimpleType(tempType)) {
                        elementIds.addAll(toSimpleTypeElementIds(tempGroup.getRestriction().getTypeId()));
                    } else {
                        elementIds.addAll(toComplexTypeElementIds(tempGroup.getRestriction().getTypeId()));
                    }
                }
                if (tempGroup.getIds() != null && !tempGroup.getIds().isEmpty()) {
                    if (TempGroup.Mode.CHOICE.equals(tempGroup.getMode())) {
                        elementIds.add(toChoiceElementId(tempGroup));
                    } else if (tempGroup.getRestriction() != null
                            && ViewConst.getMaxOccurs(tempGroup.getRestriction().getMaxOccurs()) > 1) {
                        elementIds.add(toRepeatableElementId(tempGroup));
                    } else if (TempGroup.Mode.NAMED_GROUP.equals(tempGroup.getMode())
                            || TempGroup.Mode.GROUP_REF.equals(tempGroup.getMode())) {
                        elementIds.add(toGroupElementId(tempGroup));
                    } else {
                        elementIds.addAll(toSequenceElementIds(tempGroup.getIds()));
                    }
                }
                break;
            default:
                LOGGER.error("unsupported identifier mode: {}", identifier.getMode());
                throw new ConverterException("unsupported identifier mode: " + identifier.getMode());
        }
        return elementIds;
    }

    /**
     * Преобразование в элемент, описывающий Union.
     *
     * @param tempType тип
     * @return id элемента
     * @throws ConverterException
     */
    private String toUnionElementId(TempType tempType) throws ConverterException, ProducerException {

        final String name = tempType.getNameId().getName() + UNION;
        final String namespace = tempType.getNameId().getNamespace();

        final String elementId = idNumerator.getNumbered(name + ELEMENT);
        final TempIdentifier elementIdentifier = new TempIdentifier(TempIdentifier.Mode.ELEMENT, namespace, elementId);
        idManager.registerId(elementIdentifier, TempIdentifier.Mode.ELEMENT, elementId, null);

        final String typeId = idNumerator.getNumbered(name + TYPE);
        final TempIdentifier typeIdentifier = new TempIdentifier(TempIdentifier.Mode.TYPE_NAME, namespace, typeId);
        idManager.registerId(typeIdentifier, TempIdentifier.Mode.TYPE_NAME, typeId, null);

        final ViewType unionType = newSyntheticType(typeIdentifier);
        unionType.setContent(toUnionContent(tempType, elementId));
        viewModel.getTypes().put(typeId, unionType);

        final ViewElement unionElement = newSyntheticElement(elementIdentifier, typeIdentifier);
        unionElement.setCaption(getCaption(tempType.getAnnotation()));
        if (unionElement.getCaption() == null) {
            unionElement.setCaption(UNION_DEFAULT_CAPTION);
        }
        unionElement.setDescription(getDescription(tempType.getAnnotation()));

        viewModel.getElements().put(elementId, unionElement);

        return elementId;
    }

    /**
     * Преобразование в ViewContent.
     * Набор элементов и типов описывающих содержимое Union
     *
     * @param parentTempType  родительский тип
     * @param parentElementId id родительского элемента
     * @return содержимое
     * @throws ConverterException
     */
    private ViewContent toUnionContent(TempType parentTempType, String parentElementId) throws ConverterException, ProducerException {
        final ViewContent viewContent = new ViewContent();
        viewContent.setMode(ViewContent.Mode.CHOICE);
        viewContent.setElementIds(new ArrayList<>(parentTempType.getUnion().size()));

        int index = 0;
        for (TempIdentifier unionItemTypeIdentifier : parentTempType.getUnion()) {
            index++;
            final TempIdentifier variantTypeIdentifier = processXsdTypeId(unionItemTypeIdentifier);

            final String name = parentElementId + UNION_VARIANT;
            final String namespace = parentTempType.getNameId().getNamespace();

            final String elementId = idNumerator.getNumbered(name + ELEMENT);
            final TempIdentifier elementIdentifier = new TempIdentifier(TempIdentifier.Mode.ELEMENT, namespace, elementId);
            idManager.registerId(elementIdentifier, TempIdentifier.Mode.ELEMENT, elementId, null);

            final ViewElement variantViewElement = newSyntheticElement(elementIdentifier, variantTypeIdentifier);

            final TempType tempType = getTempType(variantTypeIdentifier, false);
            if (tempType != null) {
                variantViewElement.setCaption(getCaption(tempType.getAnnotation()));
                variantViewElement.setDescription(getDescription(tempType.getAnnotation()));
            }
            if (variantViewElement.getCaption() == null) {
                variantViewElement.setCaption(UNION_VARIANT_DEFAULT_CAPTION + " " + index);
            }

            viewModel.getElements().put(elementId, variantViewElement);

            viewContent.getElementIds().add(elementId);
        }

        return viewContent;
    }

    private String newWrappedElementId(TempGroup tempGroup, String name, ViewContent content, String defaultCaption) throws ConverterException {

        final String namespace = tempGroup.getId().getNamespace();

        final String elementId = idNumerator.getNumbered(name + ELEMENT);
        final TempIdentifier elementIdentifier = new TempIdentifier(TempIdentifier.Mode.ELEMENT, namespace, elementId);
        idManager.registerId(elementIdentifier, TempIdentifier.Mode.ELEMENT, elementId, null);

        final String typeId = idNumerator.getNumbered(name + TYPE);
        final TempIdentifier typeIdentifier = new TempIdentifier(TempIdentifier.Mode.TYPE_NAME, namespace, typeId);
        idManager.registerId(typeIdentifier, TempIdentifier.Mode.TYPE_NAME, typeId, null);


        final ViewType viewType = newSyntheticType(typeIdentifier);
        viewType.setContent(content);
        viewModel.getTypes().put(typeId, viewType);

        final ViewElement viewElement = newSyntheticElement(elementIdentifier, typeIdentifier);
        viewElement.setCaption(getCaption(tempGroup.getAnnotation()));
        if (viewElement.getCaption() == null) {
            if (tempGroup.getName() != null) {
                viewElement.setCaption(tempGroup.getName());
            } else if (defaultCaption == null) {
                viewElement.setCaption(defaultCaption);
            } else {
                viewElement.setCaption(elementId);
            }
        }
        viewElement.setDescription(getDescription(tempGroup.getAnnotation()));
        viewElement.setRepeatable(toViewElementRepeatable(tempGroup.getRestriction()));
        if (viewElement.getRepeatable() == null) {
            viewElement.setRequired(toViewElementRequired(tempGroup.getRestriction()));
        }

        viewModel.getElements().put(elementId, viewElement);

        return elementId;
    }

    /**
     * Преобразование в элемент, описывающий Group.
     *
     * @param tempGroup группа
     * @return id элемента
     * @throws ConverterException
     */
    private String toGroupElementId(TempGroup tempGroup) throws ConverterException, ProducerException {
        return newWrappedElementId(tempGroup, GROUP, toGroupContent(tempGroup), GROUP_DEFAULT_CAPTION);
    }

    /**
     * Преобразование в элемент, описывающий Choice.
     *
     * @param tempGroup группа
     * @return id элемента
     * @throws ConverterException
     */
    private String toChoiceElementId(TempGroup tempGroup) throws ConverterException, ProducerException {
        return newWrappedElementId(tempGroup, CHOICE, toChoiceGroupContent(tempGroup), CHOICE_DEFAULT_CAPTION);
    }

    /**
     * Преобразование в элемент, описывающий Repeatable.
     *
     * @param tempGroup группа
     * @return id элемента
     * @throws ConverterException
     */
    private String toRepeatableElementId(TempGroup tempGroup) throws ConverterException, ProducerException {
        return newWrappedElementId(tempGroup, REPEATABLE, toGroupContent(tempGroup), REPEATABLE_DEFAULT_CAPTION);
    }

    /**
     * Преобразование в ViewContent.
     * Набор элементов и типов описывающих содержимое Group
     *
     * @param tempGroup группа
     * @return содержимое
     * @throws ConverterException
     */
    private ViewContent toGroupContent(TempGroup tempGroup) throws ConverterException, ProducerException {
        List<String> elementIds = toSequenceElementIds(tempGroup.getIds());
        if (elementIds == null || elementIds.isEmpty()) {
            return null;
        }
        ViewContent groupContent = new ViewContent();
        if (TempGroup.Mode.CHOICE.equals(tempGroup.getMode())) {
            groupContent.setMode(ViewContent.Mode.CHOICE);
        } else {
            groupContent.setMode(ViewContent.Mode.SEQUENCE);
        }
        groupContent.setElementIds(elementIds);
        return groupContent;
    }

    private ViewContent toChoiceGroupContent(TempGroup tempGroup) throws ConverterException, ProducerException {
        List<String> elementIds = toChoiceElementIds(tempGroup.getIds());
        if (elementIds == null || elementIds.isEmpty()) {
            return null;
        }
        ViewContent choiceContent = new ViewContent();
        if (TempGroup.Mode.CHOICE.equals(tempGroup.getMode())) {
            choiceContent.setMode(ViewContent.Mode.CHOICE);
        } else {
            choiceContent.setMode(ViewContent.Mode.SEQUENCE);
        }
        choiceContent.setElementIds(elementIds);
        return choiceContent;
    }

    /**
     * Создание нового искуственного элемента.
     *
     * @param elementIdentifier идентификатор элемента
     * @param typeIdentifier    идентификатор типа
     * @return елемент
     * @throws ConverterException
     */
    private ViewElement newSyntheticElement(TempIdentifier elementIdentifier, TempIdentifier typeIdentifier) throws ConverterException {

        ViewElement viewElement = new ViewElement();
        viewElement.setIdentifier(elementIdentifier);
        viewElement.setPath(".");
        viewElement.setTypeId(idManager.getId(typeIdentifier));
        viewElement.setTypeIdentifier(typeIdentifier);

//        TempType tempType = getTempType(typeIdentifier, false);
//        if (tempType != null) {
//            viewElement.setCaption(getCaption(tempType.getAnnotation()));
//            viewElement.setDescription(getDescription(tempType.getAnnotation()));
//        }
//        if (viewElement.getCaption() == null) {
//            viewElement.setCaption(defaultCaption);
//        }

//        viewElement.setRestriction(new ViewElementRestriction());
//        viewElement.getRestriction().setMinOccurs("0");
//        viewElement.getRestriction().setMaxOccurs("1");

        return viewElement;
    }

    /**
     * Создание нового искуственного типа.
     *
     * @param typeIdentifier идентификатор типа
     * @return тип
     */
    private ViewType newSyntheticType(TempIdentifier typeIdentifier) {
        ViewType viewType = new ViewType();
        viewType.setIdentifier(typeIdentifier);
        return viewType;
    }

    private ViewType newBaseType(TempIdentifier typeIdentifier, BaseType base) {
        ViewType viewType = new ViewType();
        viewType.setIdentifier(typeIdentifier);
//        viewType.setRestriction(new ViewTypeRestriction());
        viewType.setBase(base);
        return viewType;
    }

    /**
     * Преобразование в список id элементов.
     *
     * @param ids список идентификаторов
     * @return список id
     * @throws ConverterException
     */
    private List<String> toSequenceElementIds(List<TempIdentifier> ids) throws ConverterException, ProducerException {
        List<String> elementIds = new ArrayList<>(ids.size());
        for (TempIdentifier tempIdentifier : ids) {
            List<String> groupElementIds = toElementIds(tempIdentifier);
            if (groupElementIds != null) {
                elementIds.addAll(groupElementIds);
            }
        }
        return elementIds;
    }

    private List<String> toChoiceElementIds(List<TempIdentifier> ids) throws ConverterException, ProducerException {
        List<String> elementIds = new ArrayList<>(ids.size());
        for (TempIdentifier identifier : ids) {
            switch (identifier.getMode()) {
                case ELEMENT:
                    final TempElement tempElement = getTempElement(identifier, true);
                    elementIds.add(idManager.getId(tempElement.getId()));
                    break;
                case GROUP_NAME:
                    final TempGroup tempGroup = getTempGroup(identifier, true);
                    elementIds.add(toGroupElementId(tempGroup));
                    break;
                default:
                    LOGGER.error("unsupported identifier mode: {}", identifier.getMode());
                    throw new ConverterException("unsupported identifier mode: " + identifier.getMode());
            }
        }
        return elementIds;
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

    /**
     * Получение заголовка из аннотации.
     *
     * @param list аннотация
     * @return заголовок
     */
    private String getCaption(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0).trim();
    }

    /**
     * Получение описания из аннотации.
     *
     * @param list аннотация
     * @return описание
     */
    private String getDescription(List<String> list) {
        if (list == null) {
            return null;
        }
        String result = String.join(", ", list).trim();
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    /**
     * Преобразование ссылок стандартых типов в типы
     *
     * @param typeId
     * @return
     * @throws ConverterException
     */
    private TempIdentifier processXsdTypeId(TempIdentifier typeId) throws ConverterException, ProducerException {
        Objects.requireNonNull(typeId);
        final String xsdTypeId = getXsdTypeId(typeId);
        if (XsdConst.BASE_TYPE_ID.equals(xsdTypeId) || XsdConst.typeIdSet.contains(xsdTypeId)) {
            return getBaseTypeId(typeId);
        }
        return typeId;
    }

    private BaseType getBaseType(TempIdentifier typeId) throws ConverterException {
        final BaseType mapperBaseType = viewMapperProducer.getBaseType(typeId.getNamespace(), typeId.getName());
        if (mapperBaseType != null) {
            return mapperBaseType;
        }
        return xsdTypeIdToBaseType(getXsdTypeId(typeId));
    }

    private BaseType xsdTypeIdToBaseType(String xsdTypeId) throws ConverterException {
        Objects.requireNonNull(xsdTypeId);

        if (XsdConst.BASE_TYPE_ID.equals(xsdTypeId)) {
            return BaseType.STRING;
        }
        if (XsdConst.isBooleanTypeId(xsdTypeId)) {
            return BaseType.BOOLEAN;
        }
        if (XsdConst.isStringTypeId(xsdTypeId)) {
            return BaseType.STRING;
        }
        if (XsdConst.isNumberTypeId(xsdTypeId)) {
            return BaseType.NUMBER;
        }
        if (XsdConst.typeIdSet.contains(xsdTypeId)) {
            return BaseType.STRING;
        }
//        if (XsdConst.URI.equals(typeId.getNamespace())) {
//            //TODO костыль для XSD xsd
//            LOGGER.debug("unsupported XSD base id ({})", typeId);
//            return ViewType.Base.STRING;
//        }
        throw new ConverterException("unsupported base id \"" + xsdTypeId + "\"");
//        return typeId;
    }

    private String getMapperId(TempIdentifier typeId) throws ProducerException {
        if (!mapperIdMap.containsKey(typeId)) {
            final ViewMapper viewMapper = viewMapperProducer.produce(typeId.getNamespace(), typeId.getName());
            if (viewMapper != null) {
                String mapperId = idNumerator.getNumbered(typeId.getName() + MAPPER);
                viewModel.getMappers().put(mapperId, viewMapper);
                mapperIdMap.put(typeId, mapperId);
            }
        }
        return mapperIdMap.get(typeId);
    }

    private ViewWidget getWidget(TempIdentifier typeId) throws ProducerException {
        if (viewWidgetProducer == null) {
            return null;
        }
        return viewWidgetProducer.produce(typeId.getNamespace(), typeId.getName());
    }

    private TempIdentifier getBaseTypeId(TempIdentifier typeId) throws ConverterException, ProducerException {

        if (baseTypeIdMap.containsKey(typeId)) {
            return baseTypeIdMap.get(typeId);
        }

        String xsdTypeId = getXsdTypeId(typeId);

        if (XsdConst.BASE_TYPE_ID.equals(xsdTypeId)) {
            xsdTypeId = XsdConst.STRING_TYPE_ID;
        }

        final String id = xsdTypeId + TYPE;

        final TempIdentifier tempIdentifier = new TempIdentifier(TempIdentifier.Mode.TYPE_NAME, viewModel.getTargetNamespace(), id);
        final ViewType viewType = newBaseType(tempIdentifier, getBaseType(typeId));

        viewType.setMapperId(getMapperId(typeId));
        viewType.setWidget(getWidget(typeId));
        viewType.setRestriction(toBaseTypeRestriction(xsdTypeId));

        idManager.registerId(tempIdentifier, TempIdentifier.Mode.TYPE_NAME, id, null);
        viewModel.getTypes().put(id, viewType);

        baseTypeIdMap.put(typeId, tempIdentifier);
        return tempIdentifier;
    }

    private ViewTypeRestriction toBaseTypeRestriction(String xsdTypeId) {

        if (XsdConst.BASE_TYPE_ID.equals(xsdTypeId)) {
            return null;
        }

        if (XsdConst.BOOLEAN_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
            new ViewTypeRestriction().setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(BOOLEAN_PATTERN);
            return viewTypeRestriction;
        }

        if (XsdConst.BYTE_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(-128L);
            viewTypeRestriction.setMaxInclusive(127L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }
        if (XsdConst.SHORT_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(-32768L);
            viewTypeRestriction.setMaxInclusive(32767L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }
        if (XsdConst.INT_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(-2147483648L);
            viewTypeRestriction.setMaxInclusive(2147483647L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }
        if (XsdConst.LONG_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(-9223372036854775808L);
            viewTypeRestriction.setMaxInclusive(9223372036854775807L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }

        if (XsdConst.UNSIGNED_BYTE_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(UNSIGNED_NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(0L);
            viewTypeRestriction.setMaxInclusive(255L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }
        if (XsdConst.UNSIGNED_SHORT_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(UNSIGNED_NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(0L);
            viewTypeRestriction.setMaxInclusive(65535L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }
        if (XsdConst.UNSIGNED_INT_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(UNSIGNED_NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(0L);
            viewTypeRestriction.setMaxInclusive(4294967295L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }
        if (XsdConst.UNSIGNED_LONG_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(UNSIGNED_NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(0L);
            viewTypeRestriction.setMaxInclusive(9223372036854775807L);//18446744073709551615L
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }

        if (XsdConst.INTEGER_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(-9223372036854775808L);
            viewTypeRestriction.setMaxInclusive(9223372036854775807L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }
        if (XsdConst.POSITIVE_INTEGER_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(1L);
            viewTypeRestriction.setMaxInclusive(9223372036854775807L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }
        if (XsdConst.NEGATIVE_INTEGER_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(-9223372036854775808L);
            viewTypeRestriction.setMaxInclusive(-1L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }
        if (XsdConst.NON_NEGATIVE_INTEGER_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(0L);
            viewTypeRestriction.setMaxInclusive(9223372036854775807L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }
        if (XsdConst.NON_POSITIVE_INTEGER_TYPE_ID.equals(xsdTypeId)) {
            ViewTypeRestriction viewTypeRestriction = new ViewTypeRestriction();
//            viewTypeRestriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.COLLAPSE);
//            viewTypeRestriction.setPattern(NUMBER_PATTERN);
            viewTypeRestriction.setMinInclusive(-9223372036854775808L);
            viewTypeRestriction.setMaxInclusive(0L);
            viewTypeRestriction.setFractionDigits(0);
            return viewTypeRestriction;
        }

//        if (XsdConst.TIME_TYPE_ID.equals(typeId.getName())
//                || XsdConst.DATE_TYPE_ID.equals(typeId.getName())
//                || XsdConst.DATE_TIME_TYPE_ID.equals(typeId.getName())) {
//            return getBaseTypeId(XsdConst.STRING_TYPE_ID, typeId.getName());
//        }

        //TODO разобраться с типами данных
        //для дат добавить маперы и поставить числовой тип.

        return null;
    }

    private static int parseNumberAndTruncatedToInt(String string) {
        Objects.requireNonNull(string);
        BigInteger value = new BigInteger(string);
        final BigInteger min = new BigInteger(Integer.toString(Integer.MIN_VALUE));
        if (value.compareTo(min) < 0) {
            LOGGER.warn("value {} truncated to {}", value.toString(), min.toString());
            value = min;
        }
        final BigInteger max = new BigInteger(Integer.toString(Integer.MAX_VALUE));
        if (value.compareTo(max) > 0) {
            LOGGER.warn("value {} truncated to {}", value.toString(), max.toString());
            value = max;
        }
        return value.intValue();
    }

    private static long parseNumberAndTruncatedToLong(String string) {
        Objects.requireNonNull(string);
        BigInteger value = new BigInteger(string);
        final BigInteger min = new BigInteger(Long.toString(Long.MIN_VALUE));
        if (value.compareTo(min) < 0) {
            LOGGER.warn("value {} truncated to {}", value.toString(), min.toString());
            value = min;
        }
        final BigInteger max = new BigInteger(Long.toString(Long.MAX_VALUE));
        if (value.compareTo(max) > 0) {
            LOGGER.warn("value {} truncated to {}", value.toString(), max.toString());
            value = max;
        }
        return value.longValue();
    }
}
