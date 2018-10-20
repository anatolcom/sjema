package ru.anatol.sjema.converter.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anatol.sjema.JsonUtil;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.model.BaseType;
import ru.anatol.sjema.model.view.ViewConst;
import ru.anatol.sjema.model.view.ViewContent;
import ru.anatol.sjema.model.view.ViewElement;
import ru.anatol.sjema.model.view.ViewElementRepeatable;
import ru.anatol.sjema.model.view.ViewMapper;
import ru.anatol.sjema.model.view.ViewMapperParams;
import ru.anatol.sjema.model.view.ViewModel;
import ru.anatol.sjema.model.view.ViewType;
import ru.anatol.sjema.model.view.ViewTypeRestriction;
import ru.anatol.sjema.model.view.ViewTypeRestrictionPattern;
import ru.anatol.sjema.model.view.ViewValidation;
import ru.anatol.sjema.model.view.ViewValidationError;
import ru.anatol.sjema.model.view.ViewValidationGroup;
import ru.anatol.sjema.model.view.ViewWidget;
import ru.anatol.sjema.validator.ValidatorException;
import ru.anatol.sjema.validator.ViewModelValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JsonViewToViewModelConverter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonViewToViewModelConverter.class);

    private ViewModel viewModel;

    public JsonViewToViewModelConverter() {
    }

    public ViewModel convert(JSONObject jsonView) throws ConverterException {
        try {
            final ViewModel viewModel = toViewModel(jsonView);
            ViewModelValidator.validate(viewModel);
            return viewModel;
        } catch (ConverterException ex) {
            throw ex;
        } catch (JSONException ex) {
            throw new ConverterException(ex);
        } catch (ValidatorException ex) {
            throw new ConverterException(ex);
        }
    }

    private ViewModel toViewModel(JSONObject jsonView) throws JSONException, ConverterException {

        viewModel = new ViewModel();

        if (!ViewConst.VERSION_VALUE.equals(JsonUtil.getString(jsonView, ViewConst.VERSION, true))) {
            throw new ConverterException("version mismatch");
        }

        viewModel.setCaption(JsonUtil.getString(jsonView, ViewConst.CAPTION, false));

        viewModel.setDescription(JsonUtil.getString(jsonView, ViewConst.DESCRIPTION, false));

        viewModel.setTargetNamespace(JsonUtil.getString(jsonView, ViewConst.TARGET_NAMESPACE, true));

        final JSONObject jsonNamespaces = JsonUtil.getJSONObject(jsonView, ViewConst.NAMESPACES, false);
        if (jsonNamespaces != null) {
            viewModel.setNamespaces(new HashMap<>());
            final Iterator keys = jsonNamespaces.keys();
            while (keys.hasNext()) {
                final String prefix = (String) keys.next();
                final String uri = jsonNamespaces.getString(prefix);
                viewModel.getNamespaces().put(prefix, uri);
            }
        }

        final JSONObject jsonStructure = JsonUtil.getJSONObject(jsonView, ViewConst.STRUCTURE, false);
        if (jsonStructure != null) {
            viewModel.setStructure(new HashMap<>());
            final Iterator keys = jsonStructure.keys();
            while (keys.hasNext()) {
                final String id = (String) keys.next();
                final JSONObject jsonStructureEntry = jsonStructure.getJSONObject(id);
                try {
                    viewModel.getStructure().put(id, jsonStructureEntry);
                } catch (Exception ex) {
                    throw new ConverterException("structure " + id + " contain error: " + ex.getMessage(), ex);
                }
            }
        }

        final JSONObject jsonElements = JsonUtil.getJSONObject(jsonView, ViewConst.ELEMENTS, false);
        if (jsonElements != null) {
            viewModel.setElements(new HashMap<>());
            final Iterator keys = jsonElements.keys();
            while (keys.hasNext()) {
                final String id = (String) keys.next();
                final JSONObject jsonElement = jsonElements.getJSONObject(id);
                try {
                    viewModel.getElements().put(id, toViewElement(jsonElement));
                } catch (Exception ex) {
                    throw new ConverterException("element " + id + " contain error: " + ex.getMessage(), ex);
                }
            }
        }

        final JSONObject jsonTypes = JsonUtil.getJSONObject(jsonView, ViewConst.TYPES, false);
        if (jsonTypes != null) {
            viewModel.setTypes(new HashMap<>());
            final Iterator keys = jsonTypes.keys();
            while (keys.hasNext()) {
                final String id = (String) keys.next();
                final JSONObject jsonType = jsonTypes.getJSONObject(id);
                try {
                    viewModel.getTypes().put(id, toViewType(jsonType));
                } catch (Exception ex) {
                    throw new ConverterException("type " + id + " contain error: " + ex.getMessage(), ex);
                }
            }
        }

        final JSONObject jsonMappers = JsonUtil.getJSONObject(jsonView, ViewConst.MAPPERS, false);
        if (jsonMappers != null) {
            viewModel.setMappers(new HashMap<>());
            final Iterator keys = jsonMappers.keys();
            while (keys.hasNext()) {
                final String id = (String) keys.next();
                final JSONObject jsonMapper = jsonMappers.getJSONObject(id);
                try {
                    viewModel.getMappers().put(id, toViewMapper(jsonMapper));
                } catch (Exception ex) {
                    throw new ConverterException("mapper " + id + " contain error: " + ex.getMessage(), ex);
                }
            }

        }

        final JSONObject jsonValidation = JsonUtil.getJSONObject(jsonView, ViewConst.VALIDATION, false);
        if (jsonValidation != null) {
            viewModel.setValidation(new ViewValidation());
            final Iterator keys = jsonValidation.keys();
            viewModel.getValidation().setGroupMap(new HashMap<>());
            while (keys.hasNext()) {
                final String elementId = (String) keys.next();
                final JSONArray jsonErrors = JsonUtil.getJSONArray(jsonValidation, elementId, false);
                if (jsonErrors != null) {
                    viewModel.getValidation().getGroupMap().put(elementId, toViewValidationGroup(jsonErrors));
                }
            }
        }

        return viewModel;
    }

    private ViewElement toViewElement(JSONObject jsonElement) throws JSONException {
        final ViewElement viewElement = new ViewElement();

        viewElement.setPath(JsonUtil.getString(jsonElement, ViewConst.ELEMENT_PATH, true));
        viewElement.setCreateEmpty(ViewConst.getCreateEmpty(JsonUtil.getBoolean(jsonElement, ViewConst.ELEMENT_CREATE_EMPTY, false)));
        viewElement.setTypeId(JsonUtil.getString(jsonElement, ViewConst.ELEMENT_TYPE_ID, false));
        viewElement.setCaption(JsonUtil.getString(jsonElement, ViewConst.ELEMENT_CAPTION, false));
        viewElement.setDescription(JsonUtil.getString(jsonElement, ViewConst.ELEMENT_DESCRIPTION, false));
        viewElement.setRequired(ViewConst.getRequired(JsonUtil.getBoolean(jsonElement, ViewConst.ELEMENT_REQUIRED, false)));
        final JSONObject jsonRepeatable = JsonUtil.getJSONObject(jsonElement, ViewConst.ELEMENT_REPEATABLE, false);
        if (jsonRepeatable != null) {
            viewElement.setRepeatable(toViewElementRepeatable(jsonRepeatable));
        }
        viewElement.setReadOnly(ViewConst.getReadOnly(JsonUtil.getBoolean(jsonElement, ViewConst.ELEMENT_READ_ONLY, false)));
        viewElement.setDefaultValue(JsonUtil.getString(jsonElement, ViewConst.ELEMENT_DEFAULT_VALUE, false));

        return viewElement;
    }

    private ViewElementRepeatable toViewElementRepeatable(JSONObject jsonRepeatable) throws JSONException {
        final ViewElementRepeatable repeatable = new ViewElementRepeatable();

        repeatable.setPath(JsonUtil.getString(jsonRepeatable, ViewConst.ELEMENT_REPEATABLE_PATH, false));

        final Object min = JsonUtil.get(jsonRepeatable, ViewConst.ELEMENT_REPEATABLE_MIN, false);
        repeatable.setMin(ViewConst.getMin(min));

        final Object max = JsonUtil.get(jsonRepeatable, ViewConst.ELEMENT_REPEATABLE_MAX, false);
        repeatable.setMax(ViewConst.getMax(max));

        repeatable.setCaption(JsonUtil.getString(jsonRepeatable, ViewConst.ELEMENT_REPEATABLE_CAPTION, false));
        repeatable.setAddCaption(JsonUtil.getString(jsonRepeatable, ViewConst.ELEMENT_REPEATABLE_ADD_CAPTION, false));
        repeatable.setRemoveCaption(JsonUtil.getString(jsonRepeatable, ViewConst.ELEMENT_REPEATABLE_REMOVE_CAPTION, false));

        return repeatable;
    }

    private ViewType toViewType(JSONObject jsonType) throws JSONException {
        final ViewType viewType = new ViewType();

        viewType.setDescription(JsonUtil.getString(jsonType, ViewConst.TYPE_DESCRIPTION, false));

        final String base = JsonUtil.getString(jsonType, ViewConst.TYPE_BASE, false);
        if (base != null) {
            viewType.setBase(BaseType.getByValue(base));
        }

        final JSONObject jsonRestriction = JsonUtil.getJSONObject(jsonType, ViewConst.TYPE_RESTRICTION, false);
        if (jsonRestriction != null) {
            viewType.setRestriction(toViewTypeRestriction(jsonRestriction));
        }

        final String jsonMapperId = JsonUtil.getString(jsonType, ViewConst.TYPE_MAPPER_ID, false);
        if (jsonMapperId != null) {
            viewType.setMapperId(jsonMapperId);
        }

        final JSONObject jsonContent = JsonUtil.getJSONObject(jsonType, ViewConst.TYPE_CONTENT, false);
        if (jsonContent != null) {
            viewType.setContent(toViewContent(jsonContent));
        }

        final JSONObject jsonWidget = JsonUtil.getJSONObject(jsonType, ViewConst.TYPE_WIDGET, false);
        if (jsonWidget != null) {
            viewType.setWidget(toViewTypeWidget(jsonWidget));
        }

        return viewType;
    }

    private ViewMapper toViewMapper(JSONObject jsonType) throws JSONException {
        final ViewMapper viewMapper = new ViewMapper();
        viewMapper.setClassName(JsonUtil.getString(jsonType, ViewConst.MAPPER_CLASS, true));

        final JSONObject jsonParams = JsonUtil.getJSONObject(jsonType, ViewConst.MAPPER_PARAMS, false);
        if (jsonParams != null) {
            viewMapper.setParams(toViewMapperParams(jsonParams));
        }

        return viewMapper;
    }

    private ViewMapperParams toViewMapperParams(JSONObject jsonParams) throws JSONException {
        final ViewMapperParams params = new ViewMapperParams();
        final Iterator keys = jsonParams.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            params.getMap().put(key, jsonParams.getString(key));
        }
        return params;
    }

    private ViewValidationGroup toViewValidationGroup(JSONArray jsonErrors) throws JSONException {
        final ViewValidationGroup validationGroup = new ViewValidationGroup();

        validationGroup.setErrors(new ArrayList<>(jsonErrors.length()));
        for (int q = 0; q < jsonErrors.length(); q++) {
            final JSONObject jsonError = jsonErrors.getJSONObject(q);
            validationGroup.getErrors().add(toViewValidationError(jsonError));
        }

        return validationGroup;
    }

    private ViewValidationError toViewValidationError(JSONObject jsonError) throws JSONException {
        final ViewValidationError error = new ViewValidationError();

        error.setRule(JsonUtil.getString(jsonError, ViewConst.VALIDATION_RULE, true));
        error.setMessage(JsonUtil.getString(jsonError, ViewConst.VALIDATION_MESSAGE, true));

        return error;
    }

    private ViewTypeRestriction toViewTypeRestriction(JSONObject jsonRestriction) throws JSONException {
        final ViewTypeRestriction restriction = new ViewTypeRestriction();

        final JSONObject jsonEnumeration = JsonUtil.getJSONObject(jsonRestriction, ViewConst.TYPE_RESTRICTION_ENUMERATION, false);
        if (jsonEnumeration != null) {
            restriction.setEnumeration(new HashMap<>());
            final Iterator keys = jsonEnumeration.keys();
            while (keys.hasNext()) {
                final String key = (String) keys.next();
                restriction.getEnumeration().put(key, jsonEnumeration.getString(key));
            }
        }

        final JSONArray jsonPatterns = JsonUtil.getJSONArray(jsonRestriction, ViewConst.TYPE_RESTRICTION_PATTERNS, false);
        if (jsonPatterns != null) {
            restriction.setPatterns(new ArrayList<>(jsonPatterns.length()));
            for (int q = 0; q < jsonPatterns.length(); q++) {
                restriction.getPatterns().add(toViewTypeRestrictionPattern(jsonPatterns.getJSONObject(q)));
            }
        }

        final String whiteSpace = JsonUtil.getString(jsonRestriction, ViewConst.TYPE_RESTRICTION_WHITE_SPACE, false);
        if (whiteSpace != null) {
            restriction.setWhiteSpace(ViewTypeRestriction.WhiteSpace.getByValue(whiteSpace));
        }

        restriction.setLength(JsonUtil.getLong(jsonRestriction, ViewConst.TYPE_RESTRICTION_LENGTH, false));
        restriction.setMinLength(JsonUtil.getLong(jsonRestriction, ViewConst.TYPE_RESTRICTION_MIN_LENGTH, false));
        restriction.setMaxLength(JsonUtil.getLong(jsonRestriction, ViewConst.TYPE_RESTRICTION_MAX_LENGTH, false));

        restriction.setMinInclusive(JsonUtil.getLong(jsonRestriction, ViewConst.TYPE_RESTRICTION_MIN_INCLUSIVE, false));
        restriction.setMaxInclusive(JsonUtil.getLong(jsonRestriction, ViewConst.TYPE_RESTRICTION_MAX_INCLUSIVE, false));
        restriction.setMinExclusive(JsonUtil.getLong(jsonRestriction, ViewConst.TYPE_RESTRICTION_MIN_EXCLUSIVE, false));
        restriction.setMaxExclusive(JsonUtil.getLong(jsonRestriction, ViewConst.TYPE_RESTRICTION_MAX_EXCLUSIVE, false));

        restriction.setTotalDigits(JsonUtil.getInt(jsonRestriction, ViewConst.TYPE_RESTRICTION_TOTAL_DIGITS, false));
        restriction.setFractionDigits(JsonUtil.getInt(jsonRestriction, ViewConst.TYPE_RESTRICTION_FRACTION_DIGITS, false));

        return restriction;
    }

    private ViewTypeRestrictionPattern toViewTypeRestrictionPattern(JSONObject jsonPattern) throws JSONException {
        ViewTypeRestrictionPattern viewTypeRestrictionPattern = new ViewTypeRestrictionPattern();

        viewTypeRestrictionPattern.setPattern(JsonUtil.getString(jsonPattern, ViewConst.TYPE_RESTRICTION_PATTERNS_PATTERN, true));
        viewTypeRestrictionPattern.setDescription(JsonUtil.getString(jsonPattern, ViewConst.TYPE_RESTRICTION_PATTERNS_DESCRIPTION, false));

        return viewTypeRestrictionPattern;
    }

    private ViewContent toViewContent(JSONObject jsonContent) throws JSONException {
        final ViewContent viewContent = new ViewContent();

        final String mode = JsonUtil.getString(jsonContent, ViewConst.TYPE_CONTENT_MODE, true);
        viewContent.setMode(ViewContent.Mode.valueOf(mode.toUpperCase()));

        final JSONArray elementIds = JsonUtil.getJSONArray(jsonContent, ViewConst.TYPE_CONTENT_ELEMENT_IDS, true);
        if (elementIds != null) {
            viewContent.setElementIds(new ArrayList<>(elementIds.length()));
            for (int q = 0; q < elementIds.length(); q++) {
                viewContent.getElementIds().add(elementIds.getString(q));
            }
        }

        final JSONArray xmlOrderIds = JsonUtil.getJSONArray(jsonContent, ViewConst.TYPE_CONTENT_XML_ORDER, false);
        if (xmlOrderIds != null) {
            viewContent.setXmlOrder(new ArrayList<>(xmlOrderIds.length()));
            for (int q = 0; q < xmlOrderIds.length(); q++) {
                viewContent.getXmlOrder().add(xmlOrderIds.getString(q));
            }
        }

        return viewContent;
    }

    private ViewWidget toViewTypeWidget(JSONObject jsonWidget) throws JSONException {
        final ViewWidget widget = new ViewWidget();

        widget.setName(JsonUtil.getString(jsonWidget, ViewConst.TYPE_WIDGET_NAME, true));
        widget.setParams(JsonUtil.getJSONObject(jsonWidget, ViewConst.TYPE_WIDGET_PARAMS, false));

        return widget;
    }

}

