package ru.anatol.sjema.converter.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.anatol.sjema.converter.ConverterException;
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
import ru.anatol.sjema.model.view.ViewValidationError;
import ru.anatol.sjema.model.view.ViewValidationGroup;
import ru.anatol.sjema.model.view.ViewWidget;

import java.util.Map;

public class ViewModelToJsonViewConverter {

    public ViewModelToJsonViewConverter() {
    }

    public JSONObject convert(ViewModel viewModel) throws ConverterException {
        try {
            return toJsonView(viewModel);
        } catch (ConverterException ex) {
            throw ex;
        } catch (JSONException ex) {
            throw new ConverterException(ex);
        }
    }

    /**
     * Преобразование в JSON представления.
     *
     * @param viewModel представление
     * @return JSON
     * @throws JSONException
     */
    private JSONObject toJsonView(ViewModel viewModel) throws JSONException, ConverterException {

        JSONObject jsonModel = new JSONObject();

        jsonModel.put(ViewConst.VERSION, ViewConst.VERSION_VALUE);

        if (viewModel.getCaption() != null) {
            jsonModel.put(ViewConst.CAPTION, viewModel.getCaption());
        }

        if (viewModel.getDescription() != null) {
            jsonModel.put(ViewConst.DESCRIPTION, viewModel.getDescription());
        }

        if (viewModel.getTargetNamespace() != null) {
            jsonModel.put(ViewConst.TARGET_NAMESPACE, viewModel.getTargetNamespace());
        }

        if (viewModel.getNamespaces() != null) {
            JSONObject jsonNamespaces = new JSONObject();
            jsonModel.put(ViewConst.NAMESPACES, jsonNamespaces);
            for (Map.Entry<String, String> entry : viewModel.getNamespaces().entrySet()) {
                jsonNamespaces.put(entry.getKey(), entry.getValue());
            }
        }

        if (viewModel.getStructure() != null) {
            JSONObject jsonStructure = new JSONObject();
            jsonModel.put(ViewConst.STRUCTURE, jsonStructure);
            for (Map.Entry<String, JSONObject> entry : viewModel.getStructure().entrySet()) {
                jsonStructure.put(entry.getKey(), entry.getValue());
            }
        }

        if (viewModel.getElements() != null) {
            JSONObject jsonElements = new JSONObject();
            jsonModel.put(ViewConst.ELEMENTS, jsonElements);
            for (Map.Entry<String, ViewElement> entry : viewModel.getElements().entrySet()) {
                jsonElements.put(entry.getKey(), toJsonElement(entry.getValue()));
            }
        }

        if (viewModel.getTypes() != null) {
            JSONObject jsonTypes = new JSONObject();
            jsonModel.put(ViewConst.TYPES, jsonTypes);
            for (Map.Entry<String, ViewType> entry : viewModel.getTypes().entrySet()) {
                jsonTypes.put(entry.getKey(), toJsonType(entry.getValue()));
            }
        }

        if (viewModel.getMappers() != null) {
            JSONObject jsonMappers = new JSONObject();
            jsonModel.put(ViewConst.MAPPERS, jsonMappers);
            for (Map.Entry<String, ViewMapper> entry : viewModel.getMappers().entrySet()) {
                jsonMappers.put(entry.getKey(), toMapperPrinter(entry.getValue()));
            }
        }

        if (viewModel.getValidation() != null) {
            JSONObject jsonValidation = new JSONObject();
            jsonModel.put(ViewConst.VALIDATION, jsonValidation);
            if (viewModel.getValidation().getGroupMap() != null) {
                for (Map.Entry<String, ViewValidationGroup> entry : viewModel.getValidation().getGroupMap().entrySet()) {
                    JSONArray errorsPrinter = new JSONArray();
                    jsonValidation.put(entry.getKey(), errorsPrinter);
                    for (ViewValidationError viewValidationError : entry.getValue().getErrors()) {
                        errorsPrinter.put(toJsonViewValidationError(viewValidationError));
                    }
                }
            }
        }

        return jsonModel;
    }

    /**
     * Преобразовать в JSON элемента.
     *
     * @param viewElement элемент
     * @return JSON
     * @throws JSONException
     */
    private JSONObject toJsonElement(ViewElement viewElement) throws JSONException {
        JSONObject jsonElement = new JSONObject();
        jsonElement.put(ViewConst.ELEMENT_PATH, viewElement.getPath());
        if (viewElement.isCreateEmpty()) {
            jsonElement.put(ViewConst.ELEMENT_CREATE_EMPTY, viewElement.isCreateEmpty());
        }
        jsonElement.put(ViewConst.ELEMENT_TYPE_ID, viewElement.getTypeId());

        jsonElement.put(ViewConst.ELEMENT_CAPTION, viewElement.getCaption());
        if (viewElement.getDescription() != null) {
            jsonElement.put(ViewConst.ELEMENT_DESCRIPTION, viewElement.getDescription());
        }

        if (viewElement.isRequired()) {
            jsonElement.put(ViewConst.ELEMENT_REQUIRED, viewElement.isRequired());
        }

        if (viewElement.getRepeatable() != null) {
            jsonElement.put(ViewConst.ELEMENT_REPEATABLE, toElementRepeatablePrinter(viewElement.getRepeatable()));
        }

        if (viewElement.isReadOnly()) {
            jsonElement.put(ViewConst.ELEMENT_READ_ONLY, viewElement.isReadOnly());
        }

        if (viewElement.getDefaultValue() != null) {
            jsonElement.put(ViewConst.ELEMENT_DEFAULT_VALUE, viewElement.getDefaultValue());
        }

        return jsonElement;
    }

    /**
     * Преобразование в JSON ограничений элемента.
     *
     * @param restriction ограничения
     * @return JSON
     * @throws JSONException
     */
    private JSONObject toElementRepeatablePrinter(ViewElementRepeatable restriction) throws JSONException {
        JSONObject jsonRestriction = new JSONObject();

        if (restriction.getPath() != null) {
            jsonRestriction.put(ViewConst.ELEMENT_REPEATABLE_PATH, restriction.getPath());
        }

        if (restriction.getMin() != ViewConst.ELEMENT_REPEATABLE_MIN_DEFAULT) {
            jsonRestriction.put(ViewConst.ELEMENT_REPEATABLE_MIN, restriction.getMin());
        }

        if (restriction.getMax() != ViewConst.ELEMENT_REPEATABLE_MAX_DEFAULT) {
            if (restriction.getMax() == ViewConst.UNBOUNDED_VALUE) {
                jsonRestriction.put(ViewConst.ELEMENT_REPEATABLE_MAX, ViewConst.UNBOUNDED);
            } else {
                jsonRestriction.put(ViewConst.ELEMENT_REPEATABLE_MAX, restriction.getMax());
            }
        }

        if (restriction.getCaption() != null) {
            jsonRestriction.put(ViewConst.ELEMENT_REPEATABLE_CAPTION, restriction.getCaption());
        }

        if (restriction.getAddCaption() != null) {
            jsonRestriction.put(ViewConst.ELEMENT_REPEATABLE_ADD_CAPTION, restriction.getAddCaption());
        }

        if (restriction.getRemoveCaption() != null) {
            jsonRestriction.put(ViewConst.ELEMENT_REPEATABLE_REMOVE_CAPTION, restriction.getRemoveCaption());
        }

        return jsonRestriction;
    }

    /**
     * Преобразование в принтер типа.
     *
     * @param viewType тип
     * @return принтер
     * @throws JSONException
     */
    private JSONObject toJsonType(ViewType viewType) throws JSONException {
        JSONObject jsonType = new JSONObject();

        if (viewType.getDescription() != null) {
            jsonType.put(ViewConst.TYPE_DESCRIPTION, viewType.getDescription());
        }

        if (viewType.getBase() != null) {
            jsonType.put(ViewConst.TYPE_BASE, viewType.getBase().getValue());
        }

        if (viewType.getRestriction() != null) {
            jsonType.put(ViewConst.TYPE_RESTRICTION, toJsonTypeRestriction(viewType.getRestriction()));
        }

        if (viewType.getMapperId() != null) {
            jsonType.put(ViewConst.TYPE_MAPPER_ID, viewType.getMapperId());
        }

        if (viewType.getContent() != null) {
            jsonType.put(ViewConst.TYPE_CONTENT, toJsonContent(viewType.getContent()));
        }

        if (viewType.getWidget() != null) {
            jsonType.put(ViewConst.TYPE_WIDGET, toJsonTypeWidget(viewType.getWidget()));
        }

        return jsonType;
    }


    private JSONObject toMapperPrinter(ViewMapper viewMapper) throws JSONException {
        JSONObject jsonMapper = new JSONObject();

        jsonMapper.put(ViewConst.MAPPER_CLASS, viewMapper.getClassName());

        if (viewMapper.getParams() != null) {
            jsonMapper.put(ViewConst.MAPPER_PARAMS, toJsonMapperParams(viewMapper.getParams()));
        }

        return jsonMapper;
    }

    private JSONObject toJsonMapperParams(ViewMapperParams viewMapperParams) throws JSONException {
        JSONObject jsonMapperParams = new JSONObject();

        for (Map.Entry<String, String> entry : viewMapperParams.getMap().entrySet()) {
            jsonMapperParams.put(entry.getKey(), entry.getValue());
        }

        return jsonMapperParams;
    }

    /**
     * Преобразование в JSON ограничений типа.
     *
     * @param restriction ограничения
     * @return JSON
     * @throws JSONException
     */
    private JSONObject toJsonTypeRestriction(ViewTypeRestriction restriction) throws JSONException {
        JSONObject jsonRestriction = new JSONObject();

        if (restriction.getEnumeration() != null && !restriction.getEnumeration().isEmpty()) {
            JSONObject jsonEnumeration = new JSONObject();
            for (Map.Entry<String, String> entry : restriction.getEnumeration().entrySet()) {
                jsonEnumeration.put(entry.getKey(), entry.getValue());
            }
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_ENUMERATION, jsonEnumeration);
        }

        if (restriction.getPatterns() != null && !restriction.getPatterns().isEmpty()) {
            JSONArray jsonPatterns = new JSONArray();
            for (ViewTypeRestrictionPattern pattern : restriction.getPatterns()) {
                jsonPatterns.put(toJsonPattern(pattern));
            }
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_PATTERNS, jsonPatterns);
        }

        if (restriction.getWhiteSpace() != null) {
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_WHITE_SPACE, restriction.getWhiteSpace().getValue());
        }

        if (restriction.getLength() != null) {
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_LENGTH, restriction.getLength());
        }

        if (restriction.getMinLength() != null) {
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_MIN_LENGTH, restriction.getMinLength());
        }

        if (restriction.getMaxLength() != null) {
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_MAX_LENGTH, restriction.getMaxLength());
        }

        if (restriction.getMinInclusive() != null) {
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_MIN_INCLUSIVE, restriction.getMinInclusive());
        }

        if (restriction.getMaxInclusive() != null) {
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_MAX_INCLUSIVE, restriction.getMaxInclusive());
        }

        if (restriction.getMinExclusive() != null) {
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_MIN_EXCLUSIVE, restriction.getMinExclusive());
        }

        if (restriction.getMaxExclusive() != null) {
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_MAX_EXCLUSIVE, restriction.getMaxExclusive());
        }

        if (restriction.getTotalDigits() != null) {
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_TOTAL_DIGITS, restriction.getTotalDigits());
        }

        if (restriction.getFractionDigits() != null) {
            jsonRestriction.put(ViewConst.TYPE_RESTRICTION_FRACTION_DIGITS, restriction.getFractionDigits());
        }

        return jsonRestriction;
    }

    private JSONObject toJsonPattern(ViewTypeRestrictionPattern pattern) throws JSONException {
        JSONObject jsonPattern = new JSONObject();

        jsonPattern.put(ViewConst.TYPE_RESTRICTION_PATTERNS_PATTERN, pattern.getPattern());

        if (pattern.getDescription() != null) {
            jsonPattern.put(ViewConst.TYPE_RESTRICTION_PATTERNS_DESCRIPTION, pattern.getDescription());
        }
        return jsonPattern;
    }

    private JSONObject toJsonContent(ViewContent content) throws JSONException {
        switch (content.getMode()) {
            case SEQUENCE:
                return toJsonContentSequence(content);
            case CHOICE:
                return toJsonContentChoice(content);
            default:
                throw new UnsupportedOperationException("unknown content mode: " + content.getMode().name());
        }
    }

    private JSONObject toJsonContentSequence(ViewContent content) throws JSONException {
        JSONObject jsonSequence = new JSONObject();
        jsonSequence.put(ViewConst.TYPE_CONTENT_MODE, ViewConst.TYPE_CONTENT_MODE_SEQUENCE);

        JSONArray jsonElementIds = new JSONArray();
        jsonSequence.put(ViewConst.TYPE_CONTENT_ELEMENT_IDS, jsonElementIds);
        for (String elementId : content.getElementIds()) {
            jsonElementIds.put(elementId);
        }

        if (content.getXmlOrder() != null) {
            JSONArray jsonXmlOrder = new JSONArray();
            jsonSequence.put(ViewConst.TYPE_CONTENT_XML_ORDER, jsonXmlOrder);
            for (String elementId : content.getXmlOrder()) {
                jsonXmlOrder.put(elementId);
            }
        }

        return jsonSequence;
    }

    private JSONObject toJsonContentChoice(ViewContent content) throws JSONException {
        JSONObject jsonChoice = new JSONObject();
        jsonChoice.put(ViewConst.TYPE_CONTENT_MODE, ViewConst.TYPE_CONTENT_MODE_CHOICE);

        JSONArray jsonElementIds = new JSONArray();
        jsonChoice.put(ViewConst.TYPE_CONTENT_ELEMENT_IDS, jsonElementIds);
        for (String elementId : content.getElementIds()) {
            jsonElementIds.put(elementId);
        }

        if (content.getXmlOrder() != null) {
            JSONArray jsonXmlOrder = new JSONArray();
            jsonChoice.put(ViewConst.TYPE_CONTENT_XML_ORDER, jsonXmlOrder);
            for (String elementId : content.getXmlOrder()) {
                jsonXmlOrder.put(elementId);
            }
        }

        return jsonChoice;
    }

    /**
     * Преобразовать в JSON виджета.
     *
     * @param widget виджет
     * @return JSON
     */
    private JSONObject toJsonTypeWidget(ViewWidget widget) throws JSONException {
        JSONObject jsonWidget = new JSONObject();

        jsonWidget.put(ViewConst.TYPE_WIDGET_NAME, widget.getName());
        if (widget.getParams() != null) {
            jsonWidget.put(ViewConst.TYPE_WIDGET_PARAMS, widget.getParams());
        }

        return jsonWidget;
    }

    private JSONObject toJsonViewValidationError(ViewValidationError error) throws JSONException {
        JSONObject jsonError = new JSONObject();

        jsonError.put(ViewConst.VALIDATION_RULE, error.getRule());
        jsonError.put(ViewConst.VALIDATION_MESSAGE, error.getMessage());

        return jsonError;
    }

}
