package ru.anatol.sjema.printer;

import ru.anatol.sjema.producer.model.temp.TempElement;
import ru.anatol.sjema.producer.model.temp.TempFacets;
import ru.anatol.sjema.producer.model.temp.TempFacetsPattern;
import ru.anatol.sjema.producer.model.temp.TempGroup;
import ru.anatol.sjema.producer.model.temp.TempIdentifier;
import ru.anatol.sjema.producer.model.temp.TempModel;
import ru.anatol.sjema.producer.model.temp.TempType;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

public class TempModelPrinter implements Printer {

    private TempModel tempModel;

    //---------------------------------------------------------------------------

    public TempModelPrinter(TempModel tempModel) {
        this.tempModel = tempModel;
    }

    //---------------------------------------------------------------------------

    public String print() {
        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        print(printStream, 0);
        return outputStream.toString();
    }

    //---------------------------------------------------------------------------

    public void print(PrintStream printStream, int indent) {
        ObjectPrinter tempModelModel = new ObjectPrinter();
        tempModelModel.put("version", tempModel.getVersion());

        tempModelModel.put("attributeFormDefault", tempModel.getAttributeFormDefault().name());
        tempModelModel.put("elementFormDefault", tempModel.getElementFormDefault().name());

        if (tempModel.getElementSet() != null) {
            ArrayPrinter elementsPrinter = new ArrayPrinter();
            tempModelModel.put("elements", elementsPrinter);
            for (TempElement tempElement : tempModel.getElementSet()) {
                elementsPrinter.put(toElementPrinter(tempElement));
            }
        }

        if (tempModel.getTypeSet() != null) {
            ArrayPrinter typesPrinter = new ArrayPrinter();
            tempModelModel.put("types", typesPrinter);
            for (TempType tempType : tempModel.getTypeSet()) {
                typesPrinter.put(toTypePrinter(tempType));
            }
        }

        if (tempModel.getGroupSet() != null) {
            ArrayPrinter groupsPrinter = new ArrayPrinter();
            tempModelModel.put("groups", groupsPrinter);
            for (TempGroup tempGroup : tempModel.getGroupSet()) {
                groupsPrinter.put(toGroupPrinter(tempGroup));
            }
        }

        String comment = tempModel.getComment();
        if (comment != null && !comment.isEmpty()) {
            tempModelModel.addComment(comment);
        }

        tempModelModel.print(printStream, indent);
    }

    //---------------------------------------------------------------------------

    private Printer toElementPrinter(TempElement tempElement) {

        ObjectPrinter elementPrinter = new ObjectPrinter();

        elementPrinter.put("id", toIdentifierPrinter(tempElement.getId()));

        elementPrinter.put("mode", tempElement.getMode().name());

        elementPrinter.put("root", tempElement.isRoot());

        if (tempElement.getNameId() != null) {
            elementPrinter.put("nameId", toIdentifierPrinter(tempElement.getNameId()));
        }
        if (tempElement.getAnnotation() != null && !tempElement.getAnnotation().isEmpty()) {
            elementPrinter.put("annotation", new ArrayPrinter(tempElement.getAnnotation()));
        }

        if (tempElement.getRefId() != null) {
            elementPrinter.put("refId", toIdentifierPrinter(tempElement.getRefId()));
        }

        if (tempElement.getTypeId() != null) {
            elementPrinter.put("typeId", toIdentifierPrinter(tempElement.getTypeId()));
        }

        if (tempElement.isConstant()) {
            elementPrinter.put("constant", "true");
            elementPrinter.put("value", tempElement.getValue());
        } else {
            if (tempElement.getValue() != null) {
                elementPrinter.put("defaultValue", tempElement.getValue());
            }
        }

        if (tempElement.isNillable()) {
            elementPrinter.put("nillable", "true");
        }
        if (tempElement.isAbstract()) {
            elementPrinter.put("abstract", "true");
        }
        if (tempElement.getFinal() != null && !tempElement.getFinal().isEmpty()) {
            elementPrinter.put("final", new ArrayPrinter(tempElement.getFinal()));
        }
        if (tempElement.getForm() != null) {
            elementPrinter.put("form", tempElement.getForm().name());
        }

        if (tempElement.getRestriction() != null) {
            ObjectPrinter restrictionPrinter = new ObjectPrinter();
            elementPrinter.put("restriction", restrictionPrinter);
            restrictionPrinter.put("minOccurs", tempElement.getRestriction().getMinOccurs());
            restrictionPrinter.put("maxOccurs", tempElement.getRestriction().getMaxOccurs());
        }

        String comment = tempElement.getComment();
        if (comment != null && !comment.isEmpty()) {
            elementPrinter.addComment(comment);
        }

        return elementPrinter;
    }

    //---------------------------------------------------------------------------

    private Printer toTypePrinter(TempType tempType) {

        ObjectPrinter typePrinter = new ObjectPrinter();

        typePrinter.put("id", toIdentifierPrinter(tempType.getId()));
        typePrinter.put("mode", tempType.getMode().name());

        if (tempType.getNameId() != null) {
            typePrinter.put("nameId", toIdentifierPrinter(tempType.getNameId()));
        }
        if (tempType.getAnnotation() != null && !tempType.getAnnotation().isEmpty()) {
            typePrinter.put("annotation", new ArrayPrinter(tempType.getAnnotation()));
        }

        if (tempType.getRestriction() != null) {
            ObjectPrinter restrictionPrinter = new ObjectPrinter();
            typePrinter.put("restriction", restrictionPrinter);
            if (tempType.getRestriction().getBaseId() != null) {
                restrictionPrinter.put("baseId", toIdentifierPrinter(tempType.getRestriction().getBaseId()));
            }
            restrictionPrinter.put("annotation", new ArrayPrinter(tempType.getRestriction().getAnnotation()));

            if (tempType.getRestriction().getFacets() != null) {
                restrictionPrinter.put("facets", toFacetsPrinter(tempType.getRestriction().getFacets()));
            }

            String comment = tempType.getRestriction().getComment();
            if (comment != null && !comment.isEmpty()) restrictionPrinter.addComment(comment);
        }

        if (tempType.getUnion() != null) {
            ArrayPrinter unionPrinter = new ArrayPrinter();
            typePrinter.put("union", unionPrinter);
            for (TempIdentifier tempIdentifier : tempType.getUnion()) {
                unionPrinter.put(toIdentifierPrinter(tempIdentifier));
            }
        }

        if (tempType.getContentId() != null) {
            typePrinter.put("contentId", toIdentifierPrinter(tempType.getContentId()));
        }

        String comment = tempType.getComment();
        if (comment != null && !comment.isEmpty()) typePrinter.addComment(comment);

        return typePrinter;
    }

    //---------------------------------------------------------------------------

    private Printer toFacetsPrinter(TempFacets tempFacets) {

        ObjectPrinter facetsPrinter = new ObjectPrinter();

        if (tempFacets.getEnumeration() != null && !tempFacets.getEnumeration().isEmpty()) {
            ObjectPrinter enumerationPrinter = new ObjectPrinter();
            facetsPrinter.put("enumeration", enumerationPrinter);
            for (Map.Entry<String, String> entry : tempFacets.getEnumeration().entrySet()) {
                enumerationPrinter.put(entry.getKey(), entry.getValue());
            }
        }

        if (tempFacets.getFacets() != null && !tempFacets.getFacets().isEmpty()) {
            for (Map.Entry<String, String> entry : tempFacets.getFacets().entrySet()) {
                facetsPrinter.put(entry.getKey(), entry.getValue());
            }
        }

        if (tempFacets.getPatterns() != null && !tempFacets.getPatterns().isEmpty()) {
            ArrayPrinter patternsPrinter = new ArrayPrinter();
            for (TempFacetsPattern pattern : tempFacets.getPatterns()) {
                patternsPrinter.put(toPatternPattern(pattern));
            }
            facetsPrinter.put("patterns", patternsPrinter);
        }

        if (tempFacets.getWhiteSpace() != null) {
            facetsPrinter.put("whiteSpace", tempFacets.getWhiteSpace().name().toLowerCase());
        }

        String comment = tempFacets.getComment();
        if (comment != null && !comment.isEmpty()) facetsPrinter.addComment(comment);

        return facetsPrinter;
    }

    //---------------------------------------------------------------------------

    private Printer toPatternPattern(TempFacetsPattern pattern) {

        ObjectPrinter jsonPattern = new ObjectPrinter();

        jsonPattern.put("pattern", pattern.getPattern());

        if (pattern.getDescription() != null) {
            jsonPattern.put("description", pattern.getDescription());
        }
        return jsonPattern;
    }

    //---------------------------------------------------------------------------

    private Printer toGroupPrinter(TempGroup tempGroup) {

        ObjectPrinter groupPrinter = new ObjectPrinter();

        groupPrinter.put("mode", tempGroup.getMode().name());

        groupPrinter.put("id", toIdentifierPrinter(tempGroup.getId()));
        if (tempGroup.getNameId() != null) {
            groupPrinter.put("nameId", toIdentifierPrinter(tempGroup.getNameId()));
        }

        if (tempGroup.getName() != null) {
            groupPrinter.put("name", tempGroup.getName());
        }
        if (tempGroup.getAnnotation() != null && !tempGroup.getAnnotation().isEmpty()) {
            groupPrinter.put("annotation", new ArrayPrinter(tempGroup.getAnnotation()));
        }

        if (tempGroup.getRefId() != null) {
            groupPrinter.put("refId", toIdentifierPrinter(tempGroup.getRefId()));
        }

        if (tempGroup.getExtensionId() != null) {
            groupPrinter.put("extensionId", toIdentifierPrinter(tempGroup.getExtensionId()));
        }

        if (tempGroup.getRestriction() != null) {
            ObjectPrinter restrictionPrinter = new ObjectPrinter();
            groupPrinter.put("restriction", restrictionPrinter);
            if (tempGroup.getRestriction().getTypeId() != null) {
                restrictionPrinter.put("typeId", toIdentifierPrinter(tempGroup.getRestriction().getTypeId()));
            }
        }

        ArrayPrinter idsPrinter = new ArrayPrinter();
        groupPrinter.put("ids", idsPrinter);
        for (TempIdentifier tempIdentifier : tempGroup.getIds()) {
            idsPrinter.put(toIdentifierPrinter(tempIdentifier));
        }

        String comment = tempGroup.getComment();
        if (comment != null && !comment.isEmpty()) {
            groupPrinter.addComment(comment);
        }

        return groupPrinter;
    }
    //---------------------------------------------------------------------------

    private Printer toIdentifierPrinter(TempIdentifier tempIdentifier) {

//        PObject pIdentifier = new PObject();
//        pIdentifier.add("mode", new PString(identifier.getMode().name()));
//        pIdentifier.add("name", new PString(identifier.getName()));
//        pIdentifier.add("namespace", new PString(identifier.getNamespace()));
//        return pIdentifier;

        return new StringPrinter(tempIdentifier.getMode().name() + " " + tempIdentifier.getNamespace() + " " + tempIdentifier.getName());
    }

    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------
    //---------------------------------------------------------------------------

    public static String indent(int number) {
        StringBuilder indentBuilder = new StringBuilder();
        for (int q = 0; q < number; q++) {
            indentBuilder.append("    ");
        }
        return indentBuilder.toString();
    }

    //---------------------------------------------------------------------------
}
