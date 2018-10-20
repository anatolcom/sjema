package ru.anatol.sjema.printer;

import org.json.JSONObject;
import ru.anatol.sjema.producer.model.temp.TempIdentifier;
import ru.anatol.sjema.model.view.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

public class ViewModelPrinter implements Printer {

    private ViewModel viewModel;

    public ViewModelPrinter(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public String print() {
        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        print(printStream, 0);
        return outputStream.toString();
    }

    @Override
    public void print(PrintStream printStream, int indent) {

        ObjectPrinter modelPrinter = new ObjectPrinter();

        modelPrinter.put(ViewConst.VERSION, ViewConst.VERSION_VALUE);

        if (viewModel.getCaption() != null) {
            modelPrinter.put(ViewConst.CAPTION, viewModel.getCaption());
        }

        if (viewModel.getDescription() != null) {
            modelPrinter.put(ViewConst.DESCRIPTION, viewModel.getDescription());
        }

        if (viewModel.getTargetNamespace() != null) {
            modelPrinter.put(ViewConst.TARGET_NAMESPACE, viewModel.getTargetNamespace());
        }

        if (viewModel.getNamespaces() != null) {
            ObjectPrinter namespacesPrinter = new ObjectPrinter();
            modelPrinter.put(ViewConst.NAMESPACES, namespacesPrinter);
            for (Map.Entry<String, String> entry : viewModel.getNamespaces().entrySet()) {
                namespacesPrinter.put(entry.getKey(), entry.getValue());
            }
        }

        if (viewModel.getStructure() != null) {
            ObjectPrinter structurePrinter = new ObjectPrinter();
            modelPrinter.put(ViewConst.STRUCTURE, structurePrinter);
            for (Map.Entry<String, JSONObject> entry : viewModel.getStructure().entrySet()) {
                structurePrinter.put(entry.getKey(), entry.getValue().toString());
            }
        }

        if (viewModel.getElements() != null) {
            ObjectPrinter elementsPrinter = new ObjectPrinter();
            modelPrinter.put(ViewConst.ELEMENTS, elementsPrinter);
            for (Map.Entry<String, ViewElement> entry : viewModel.getElements().entrySet()) {
                elementsPrinter.put(entry.getKey(), toElementPrinter(entry.getValue()));
            }
        }

        if (viewModel.getTypes() != null) {
            ObjectPrinter typesPrinter = new ObjectPrinter();
            modelPrinter.put(ViewConst.TYPES, typesPrinter);
            for (Map.Entry<String, ViewType> entry : viewModel.getTypes().entrySet()) {
                typesPrinter.put(entry.getKey(), toTypePrinter(entry.getValue()));
            }
        }

        if (viewModel.getMappers() != null) {
            ObjectPrinter mappersPrinter = new ObjectPrinter();
            modelPrinter.put(ViewConst.MAPPERS, mappersPrinter);
            for (Map.Entry<String, ViewMapper> entry : viewModel.getMappers().entrySet()) {
                mappersPrinter.put(entry.getKey(), toMapperPrinter(entry.getValue()));
            }
        }

        if (viewModel.getValidation() != null) {
            ObjectPrinter validationPrinter = new ObjectPrinter();
            modelPrinter.put(ViewConst.VALIDATION, validationPrinter);
            if (viewModel.getValidation().getGroupMap() != null) {
                for (Map.Entry<String, ViewValidationGroup> entry : viewModel.getValidation().getGroupMap().entrySet()) {
                    ArrayPrinter errorsPrinter = new ArrayPrinter();
                    validationPrinter.put(entry.getKey(), errorsPrinter);
                    for (ViewValidationError viewValidationError : entry.getValue().getErrors()) {
                        errorsPrinter.put(toViewValidationErrorPrinter(viewValidationError));
                    }
                }
            }
        }

        modelPrinter.print(printStream, indent);
    }

    /**
     * Преобразование в принтер элемента.
     *
     * @param viewElement элемент
     * @return принтер
     */
    private ObjectPrinter toElementPrinter(ViewElement viewElement) {
        ObjectPrinter elementPrinter = new ObjectPrinter();

        elementPrinter.put(ViewConst.ELEMENT_PATH, viewElement.getPath());
        if (viewElement.isCreateEmpty()) {
            elementPrinter.put(ViewConst.ELEMENT_CREATE_EMPTY, viewElement.isCreateEmpty());
        }
        elementPrinter.put(ViewConst.ELEMENT_TYPE_ID, toIdentifierPrinter(viewElement.getTypeId(), viewElement.getTypeIdentifier()));

        elementPrinter.put(ViewConst.ELEMENT_CAPTION, viewElement.getCaption());
        if (viewElement.getDescription() != null) {
            elementPrinter.put(ViewConst.ELEMENT_DESCRIPTION, viewElement.getDescription());
        }

        if (viewElement.isRequired()) {
            elementPrinter.put(ViewConst.ELEMENT_REQUIRED, viewElement.isRequired());
        }

        if (viewElement.getRepeatable() != null) {
            elementPrinter.put(ViewConst.ELEMENT_REPEATABLE, toElementRepeatablePrinter(viewElement.getRepeatable()));
        }

        if (viewElement.isReadOnly()) {
            elementPrinter.put(ViewConst.ELEMENT_READ_ONLY, viewElement.isReadOnly());
        }

        if (viewElement.getDefaultValue() != null) {
            elementPrinter.put(ViewConst.ELEMENT_DEFAULT_VALUE, viewElement.getDefaultValue());
        }

        return elementPrinter;
    }

    /**
     * Преобразование в принтер ограничений элемента.
     *
     * @param restriction ограничения
     * @return принтер
     */
    private Printer toElementRepeatablePrinter(ViewElementRepeatable restriction) {
        ObjectPrinter restrictionPrinter = new ObjectPrinter();

        if (restriction.getPath() != null) {
            restrictionPrinter.put(ViewConst.ELEMENT_REPEATABLE_PATH, restriction.getPath());
        }

        if (restriction.getMin() != ViewConst.ELEMENT_REPEATABLE_MIN_DEFAULT) {
            restrictionPrinter.put(ViewConst.ELEMENT_REPEATABLE_MIN, restriction.getMin());
        }

        if (restriction.getMax() != ViewConst.ELEMENT_REPEATABLE_MAX_DEFAULT) {
            if (restriction.getMax() == ViewConst.UNBOUNDED_VALUE) {
                restrictionPrinter.put(ViewConst.ELEMENT_REPEATABLE_MAX, ViewConst.UNBOUNDED);
            } else {
                restrictionPrinter.put(ViewConst.ELEMENT_REPEATABLE_MAX, restriction.getMax());
            }
        }

        if (restriction.getCaption() != null) {
            restrictionPrinter.put(ViewConst.ELEMENT_REPEATABLE_CAPTION, restriction.getCaption());
        }

        if (restriction.getAddCaption() != null) {
            restrictionPrinter.put(ViewConst.ELEMENT_REPEATABLE_ADD_CAPTION, restriction.getAddCaption());
        }

        if (restriction.getRemoveCaption() != null) {
            restrictionPrinter.put(ViewConst.ELEMENT_REPEATABLE_REMOVE_CAPTION, restriction.getRemoveCaption());
        }

        return restrictionPrinter;
    }

    /**
     * Преобразовать в принтер типа.
     *
     * @param viewType тип
     * @return принтер
     */
    private ObjectPrinter toTypePrinter(ViewType viewType) {
        ObjectPrinter typePrinter = new ObjectPrinter();

        if (viewType.getDescription() != null) {
            typePrinter.put(ViewConst.TYPE_DESCRIPTION, viewType.getDescription());
        }

        if (viewType.getBase() != null) {
            typePrinter.put(ViewConst.TYPE_BASE, toIdentifierPrinter(viewType.getBase().getValue(), viewType.getBaseIdentifier()));
        }

        if (viewType.getRestriction() != null) {
            typePrinter.put(ViewConst.TYPE_RESTRICTION, toTypeRestrictionPrinter(viewType.getRestriction()));
        }

        if (viewType.getMapperId() != null) {
            typePrinter.put(ViewConst.TYPE_MAPPER_ID, viewType.getMapperId());
        }

        if (viewType.getContent() != null) {
            typePrinter.put(ViewConst.TYPE_CONTENT, toContentPrinter(viewType.getContent()));
        }

        if (viewType.getWidget() != null) {
            typePrinter.put(ViewConst.TYPE_WIDGET, toTypeWidgetPrinter(viewType.getWidget()));
        }

        return typePrinter;
    }

    private ObjectPrinter toMapperPrinter(ViewMapper viewMapper) {
        ObjectPrinter mapperPrinter = new ObjectPrinter();

        mapperPrinter.put(ViewConst.MAPPER_CLASS, viewMapper.getClassName());

        if (viewMapper.getParams() != null) {
            mapperPrinter.put(ViewConst.MAPPER_PARAMS, toMapperParamsPrinter(viewMapper.getParams()));
        }

        return mapperPrinter;
    }

    private ObjectPrinter toMapperParamsPrinter(ViewMapperParams viewMapperParams) {
        ObjectPrinter mapperParamsPrinter = new ObjectPrinter();

        for (Map.Entry<String, String> entry : viewMapperParams.getMap().entrySet()) {
            mapperParamsPrinter.put(entry.getKey(), entry.getValue());
        }

        return mapperParamsPrinter;
    }

    /**
     * Преобразование в принтер ограничений типа.
     *
     * @param restriction ограничения
     * @return принтер
     */
    private Printer toTypeRestrictionPrinter(ViewTypeRestriction restriction) {
        ObjectPrinter restrictionPrinter = new ObjectPrinter();

        if (restriction.getEnumeration() != null && !restriction.getEnumeration().isEmpty()) {
            ObjectPrinter enumerationPrinter = new ObjectPrinter();
            for (Map.Entry<String, String> entry : restriction.getEnumeration().entrySet()) {
                enumerationPrinter.put(entry.getKey(), entry.getValue());
            }
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_ENUMERATION, enumerationPrinter);
        }

        if (restriction.getPatterns() != null && !restriction.getPatterns().isEmpty()) {
            ArrayPrinter patternsPrinter = new ArrayPrinter();
            for (ViewTypeRestrictionPattern pattern : restriction.getPatterns()) {
                patternsPrinter.put(toPatternPattern(pattern));
            }
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_PATTERNS, patternsPrinter);
        }

        if (restriction.getWhiteSpace() != null) {
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_WHITE_SPACE, restriction.getWhiteSpace().getValue());
        }

        if (restriction.getLength() != null) {
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_LENGTH, restriction.getLength());
        }

        if (restriction.getMinLength() != null) {
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_MIN_LENGTH, restriction.getMinLength());
        }

        if (restriction.getMaxLength() != null) {
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_MAX_LENGTH, restriction.getMaxLength());
        }

        if (restriction.getMinInclusive() != null) {
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_MIN_INCLUSIVE, restriction.getMinInclusive());
        }

        if (restriction.getMaxInclusive() != null) {
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_MAX_INCLUSIVE, restriction.getMaxInclusive());
        }

        if (restriction.getMinExclusive() != null) {
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_MIN_EXCLUSIVE, restriction.getMinExclusive());
        }

        if (restriction.getMaxExclusive() != null) {
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_MAX_EXCLUSIVE, restriction.getMaxExclusive());
        }

        if (restriction.getTotalDigits() != null) {
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_TOTAL_DIGITS, restriction.getTotalDigits());
        }

        if (restriction.getFractionDigits() != null) {
            restrictionPrinter.put(ViewConst.TYPE_RESTRICTION_FRACTION_DIGITS, restriction.getFractionDigits());
        }

        return restrictionPrinter;
    }

    /**
     * Преобразовать в принтер паттерна.
     *
     * @param pattern содержимое
     * @return принтер
     */
    private Printer toPatternPattern(ViewTypeRestrictionPattern pattern) {
        ObjectPrinter jsonPattern = new ObjectPrinter();

        jsonPattern.put(ViewConst.TYPE_RESTRICTION_PATTERNS_PATTERN, pattern.getPattern());

        if (pattern.getDescription() != null) {
            jsonPattern.put(ViewConst.TYPE_RESTRICTION_PATTERNS_DESCRIPTION, pattern.getDescription());
        }
        return jsonPattern;
    }


    /**
     * Преобразовать в принтер содержимого.
     *
     * @param content содержимое
     * @return принтер
     */
    private Printer toContentPrinter(ViewContent content) {
        switch (content.getMode()) {
            case SEQUENCE:
                return toContentSequencePrinter(content);
            case CHOICE:
                return toContentChoicePrinter(content);
            default:
                throw new UnsupportedOperationException("unknown content mode: " + content.getMode().name());
        }
    }

    private Printer toContentSequencePrinter(ViewContent content) {
        ObjectPrinter sequencePrinter = new ObjectPrinter();
        sequencePrinter.put(ViewConst.TYPE_CONTENT_MODE, ViewConst.TYPE_CONTENT_MODE_SEQUENCE);

        ArrayPrinter elementIdsPrinter = new ArrayPrinter();
        sequencePrinter.put(ViewConst.TYPE_CONTENT_ELEMENT_IDS, elementIdsPrinter);
        for (String elementId : content.getElementIds()) {
            elementIdsPrinter.put(elementId);
        }

        if (content.getXmlOrder() != null) {
            ArrayPrinter xmlOrderPrinter = new ArrayPrinter();
            sequencePrinter.put(ViewConst.TYPE_CONTENT_XML_ORDER, xmlOrderPrinter);
            for (String elementId : content.getXmlOrder()) {
                xmlOrderPrinter.put(elementId);
            }
        }

        return sequencePrinter;
    }

    private Printer toContentChoicePrinter(ViewContent content) {
        ObjectPrinter choicePrinter = new ObjectPrinter();
        choicePrinter.put(ViewConst.TYPE_CONTENT_MODE, ViewConst.TYPE_CONTENT_MODE_CHOICE);

        ArrayPrinter elementIdsPrinter = new ArrayPrinter();
        choicePrinter.put(ViewConst.TYPE_CONTENT_ELEMENT_IDS, elementIdsPrinter);
        for (String elementId : content.getElementIds()) {
            elementIdsPrinter.put(elementId);
        }

        if (content.getXmlOrder() != null) {
            ArrayPrinter xmlOrderPrinter = new ArrayPrinter();
            choicePrinter.put(ViewConst.TYPE_CONTENT_XML_ORDER, xmlOrderPrinter);
            for (String elementId : content.getXmlOrder()) {
                xmlOrderPrinter.put(elementId);
            }
        }

        return choicePrinter;
    }

    /**
     * Преобразовать в принтер виджета.
     *
     * @param widget виджет
     * @return принтер
     */
    private Printer toTypeWidgetPrinter(ViewWidget widget) {
        ObjectPrinter widgetPrinter = new ObjectPrinter();

        widgetPrinter.put(ViewConst.TYPE_WIDGET_NAME, widget.getName());
        if (widget.getParams() != null) {
            widgetPrinter.put(ViewConst.TYPE_WIDGET_PARAMS, widget.getParams().toString());
        }

        return widgetPrinter;
    }

    private Printer toViewValidationErrorPrinter(ViewValidationError error) {
        ObjectPrinter errorPrinter = new ObjectPrinter();

        errorPrinter.put(ViewConst.VALIDATION_RULE, error.getRule());
        errorPrinter.put(ViewConst.VALIDATION_MESSAGE, error.getMessage());

        return errorPrinter;
    }

    private Printer toIdentifierPrinter(String id, TempIdentifier tempIdentifier) {
        String identifier = null;
        if (tempIdentifier != null) {
            identifier = tempIdentifier.toString();
        }
        return new StringPrinter(id + " (" + identifier + ")");
//        return new StringPrinter(id);
    }

}
