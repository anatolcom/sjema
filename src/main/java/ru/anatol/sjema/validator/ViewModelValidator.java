package ru.anatol.sjema.validator;

import org.json.JSONObject;
import ru.anatol.sjema.model.view.ViewElement;
import ru.anatol.sjema.model.view.ViewModel;
import ru.anatol.sjema.model.view.ViewType;
import ru.anatol.sjema.model.view.ViewValidationGroup;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ViewModelValidator {

    public static void validate(ViewModel viewModel) throws ValidatorException {

        // проверка наличия используемых корневых элементов в структуре
        if (viewModel.getStructure() != null && !viewModel.getStructure().isEmpty()) {
            for (Map.Entry<String, JSONObject> entry : viewModel.getStructure().entrySet()) {
                if (!viewModel.getElements().containsKey(entry.getKey())) {
                    throw new ValidatorException("structure \"" + entry.getKey() + "\" refers to an undefined element \"" + entry.getKey() + "\"");
                }
            }
        }

        // проверка наличия используемых типов в элементах
        if (viewModel.getElements() != null && !viewModel.getElements().isEmpty()) {
            for (Map.Entry<String, ViewElement> entry : viewModel.getElements().entrySet()) {
                validateElement(viewModel, entry.getKey(), entry.getValue());
            }
        }

        // проверка наличия используемых элементов в типах
        if (viewModel.getTypes() != null) {
            for (Map.Entry<String, ViewType> entry : viewModel.getTypes().entrySet()) {
                validateType(viewModel, entry.getKey(), entry.getValue());
            }
        }

        if (viewModel.getValidation() != null && viewModel.getValidation().getGroupMap() != null) {
            for (Map.Entry<String, ViewValidationGroup> entry : viewModel.getValidation().getGroupMap().entrySet()) {
                validateValidationGroup(viewModel, entry.getKey(), entry.getValue());
            }
        }
    }

    private static void validateElement(ViewModel viewModel, String elementId, ViewElement element) throws ValidatorException {
        if (element.getTypeId() != null) {
            if (viewModel.getTypes() == null || !viewModel.getTypes().containsKey(element.getTypeId())) {
                throw new ValidatorException("element \"" + elementId + "\" refers to an undefined type \"" + element.getTypeId() + "\"");
            }
        }
    }

    private static void validateType(ViewModel viewModel, String typeId, ViewType type) throws ValidatorException {
        // проверка контента
        if (type.getContent() != null) {
            Set<String> elementIds = new HashSet<>();
            for (String elementId : type.getContent().getElementIds()) {
                if (elementIds.contains(elementId)) {
                    throw new ValidatorException("elementIds in content of type \"" + typeId + "\" contain not unique elementId \"" + elementId + "\"");
                }
                elementIds.add(elementId);
                if (viewModel.getElements() == null || !viewModel.getElements().containsKey(elementId)) {
                    throw new ValidatorException("elementIds in content of type \"" + typeId + "\" refers to an undefined element \"" + elementId + "\"");
                }
            }

            // проверка порядка елементов в XML
            if (type.getContent().getXmlOrder() != null) {
                Set<String> xmlOrder = new HashSet<>();
                for (String elementId : type.getContent().getXmlOrder()) {
                    if (xmlOrder.contains(elementId)) {
                        throw new ValidatorException("xmlOrder in content of type \"" + typeId + "\" contain not unique elementId \"" + elementId + "\"");
                    }
                    xmlOrder.add(elementId);
                    if (!elementIds.contains(elementId)) {
                        throw new ValidatorException("xmlOrder in content of type \"" + typeId + "\" refers to element \"" + elementId + "\" not presented in elementIds");
                    }
                }
                for (String elementId : elementIds) {
                    if (!xmlOrder.contains(elementId)) {
                        throw new ValidatorException("xmlOrder in content of type \"" + typeId + "\" not refers to element \"" + elementId + "\" presented in elementIds");
                    }
                }
            }
        }
    }

    private static void validateValidationGroup(ViewModel viewModel, String elementId, ViewValidationGroup validationGroup) throws ValidatorException {
        if (!viewModel.getStructure().containsKey(elementId)) {
            throw new ValidatorException("validation group name \"" + elementId + "\" refers to element \"" + elementId + "\"  not presented in structure");
        }
    }

}
