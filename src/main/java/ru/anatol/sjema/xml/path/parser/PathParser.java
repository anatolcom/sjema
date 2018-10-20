package ru.anatol.sjema.xml.path.parser;

import ru.anatol.sjema.xml.path.constant.Constant;
import ru.anatol.sjema.xml.path.constant.ConstantInteger;
import ru.anatol.sjema.xml.path.constant.ConstantNodeName;
import ru.anatol.sjema.xml.path.constant.ConstantString;
import ru.anatol.sjema.xml.path.function.Function;
import ru.anatol.sjema.xml.path.function.FunctionNodeName;
import ru.anatol.sjema.xml.path.function.FunctionNodeSet;
import ru.anatol.sjema.xml.path.operation.Operation;
import ru.anatol.sjema.xml.path.operation.Path;
import ru.anatol.sjema.xml.path.operator.Operator;
import ru.anatol.sjema.xml.path.operator.OperatorEquals;
import ru.anatol.sjema.xml.path.step.Axis;
import ru.anatol.sjema.xml.Namespaces;
import ru.anatol.sjema.xml.path.PathConst;
import ru.anatol.sjema.xml.path.XPath;
import ru.anatol.sjema.xml.path.step.StepContext;
import ru.anatol.sjema.xml.path.step.StepRoot;

public class PathParser {

    private final Cursor cursor;
    private final Namespaces namespaces;

    public PathParser(String path, Namespaces namespaces) {
        this.cursor = new Cursor(path);
        this.namespaces = namespaces;
    }

    /**
     * Пропуск неиспользуемых символов.
     *
     * @return найденные неиспользуемые символы
     * @throws ParserException
     */
    private String skipUnused() throws ParserException {
        StringBuilder str = new StringBuilder();
        while (cursor.hasRead()) {
            char c = cursor.readChar();
            if (c > 32) {
                break;
            }
            str.append(c);
            cursor.next();
        }
        if (str.length() == 0) {
            return null;
        }
        return str.toString();
    }

    /**
     * Вычитывание заранее известного символа.
     *
     * @param marker известный символ
     * @return true если найден, иначе false
     * @throws ParserException
     */
    private boolean isMarker(char marker) throws ParserException {
        if (!cursor.equals(marker)) {
            return false;
        }
        cursor.next();
        return true;
    }

    /**
     * Вычитывание заранее известного набора символов.
     *
     * @param marker известный набор символов
     * @return true если найден, иначе false
     * @throws ParserException
     */
    private boolean isMarker(String marker) throws ParserException {
        int pos = cursor.getPos();
        char[] chars = marker.toCharArray();
        for (char c : chars) {
            if (!cursor.hasRead() || !cursor.equals(c)) {
                cursor.setPos(pos);
                return false;
            }
            cursor.next();
        }
        return true;
    }

    /**
     * Вычитывание названия.
     *
     * @param required обязательность.
     * @return название
     * @throws ParserException
     */
    private String readName(boolean required) throws ParserException {
        cursor.requireNonOutOfRange();
        StringBuilder str = new StringBuilder();
        while (cursor.hasRead()) {
            char c = cursor.readChar();
            //название должно начинаться с буквы
            if (str.length() == 0 && !Character.isAlphabetic(c)) {
                break;
            }
            //либо буква, либо цифра
            if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                break;
            }
            str.append(c);
            cursor.next();
        }
        if (str.length() == 0) {
            if (required) {
                throw new ParserException("unexpected symbol '" + cursor.readChar() + "' at position " + cursor.getPos());
            }
            return null;
        }
        return str.toString();
    }

    /**
     * Вычитывание беззнакового целого числа.
     *
     * @return число
     * @throws ParserException
     */
    private Integer readUnsignedInteger() throws ParserException {
        StringBuilder str = new StringBuilder();
        while (cursor.hasRead()) {
            char c = cursor.readChar();
            if (!Character.isDigit(c)) {
                break;
            }
            str.append(c);
            cursor.next();
        }
        if (str.length() == 0) {
            return null;
        }
        return Integer.parseInt(str.toString());
    }

    /**
     * Вычитывание числовой константы.
     *
     * @return константа
     * @throws ParserException
     */
    private ConstantInteger readConstantInteger() throws ParserException {
        Integer integer = readUnsignedInteger();
        if (integer == null) {
            return null;
        }
        return new ConstantInteger(integer);
    }

    /**
     * Вычитывание строковой константы.
     *
     * @return константа
     * @throws ParserException
     */
    private ConstantString readConstantString() throws ParserException {
        if (!isMarker(PathConst.TEXT_BEGIN)) {
            return null;
        }
        StringBuilder valueBuilder = new StringBuilder();
        while (true) {
            if (!cursor.hasRead()) {
                throw new ParserException("unexpected end of text value");
            }
            if (isMarker(PathConst.TEXT_END)) {
                break;
            }
            if (isMarker(PathConst.TEXT_SHIELD)) {
                valueBuilder.append(cursor.readChar());
                cursor.next();
                continue;
            }
            valueBuilder.append(cursor.readChar());
            cursor.next();
        }
        return new ConstantString(valueBuilder.toString());
    }

    public XPath read() throws ParserException {
        return new XPath(readOperation());
    }

    private Path readPath() throws ParserException {

        Path operationPath = new Path();
        if (isMarker(PathConst.STEP)) {
            operationPath.getSteps().add(new StepRoot());
            skipUnused();
        }
        while (cursor.hasRead()) {
            StepContext step = readStepContext();
            if (step == null) {
                break;
            }
            operationPath.getSteps().add(step);
//            skipUnused();
            if (!cursor.hasRead() || !isMarker(PathConst.STEP)) {
                break;
            }
        }
        return operationPath;
    }

    private StepContext readStepContext() throws ParserException {
        StepContext step = readContextItem();
        while (cursor.hasRead()) {
            Operation operation = readCondition();
            if (operation == null) {
                break;
            }
            step.getConditions().add(operation);
        }
        return step;
    }

    private Operator readOperator(Operation operand) throws ParserException {
        if (isMarker(PathConst.OPERATOR_EQUALS)) {
            return new OperatorEquals(operand, readOperation());
        }
        return null;
    }

    private Operation readOperation() throws ParserException {
        Operation operation = readConstant();
        if (operation == null) {
            operation = readPath();
        }
        while (cursor.hasRead()) {
            int pos = cursor.getPos();
            skipUnused();
            Operator operator = readOperator(operation);
            if (operator == null) {
                cursor.setPos(pos);
                break;
            }
            operation = operator;
        }
        return operation;
    }

    private Constant readConstant() throws ParserException {
        Constant constant = readConstantInteger();
        if (constant != null) {
            return constant;
        }
        constant = readConstantString();
        if (constant != null) {
            return constant;
        }
        return null;
    }

    public Operation readCondition() throws ParserException {
        if (!isMarker(PathConst.CONDITION_BEGIN)) {
            return null;
        }
        Operation operation = readOperation();
        if (operation != null && !isMarker(PathConst.CONDITION_END)) {
            throw new ParserException("unexpected symbol " + cursor.readChar() + " at position " + cursor.getPos());
        }
        return operation;
    }

    private Axis readAxis() throws ParserException {
        int pos = cursor.getPos();
        String name = readName(false);
        skipUnused();
        if (!isMarker(PathConst.AXIS)) {
            cursor.setPos(pos);
            return null;
        }
        return Axis.valueOf(name.toUpperCase());
    }

    private String readPrefix() throws ParserException {
        int pos = cursor.getPos();
        String name = readName(false);
        if (!cursor.hasRead() || !isMarker(PathConst.PREFIX)) {
            cursor.setPos(pos);
            return null;
        }
        return name;
    }

    private ConstantNodeName readNodeName() throws ParserException {
        return new ConstantNodeName(readPrefix(), readName(true));
    }

    private Function readFunction() throws ParserException {
        int pos = cursor.getPos();
        String name = readName(false);
        skipUnused();
        if (!isMarker(PathConst.FUNCTION_BEGIN)) {
            cursor.setPos(pos);
            return null;
        }
        if (!isMarker(PathConst.FUNCTION_END)) {
            throw new ParserException("unexpected symbol " + cursor.readChar() + " at position " + cursor.getPos());
        }
        if (PathConst.FN_NODE.equals(name)) {
            return new FunctionNodeSet();
        }
        throw new ParserException("unknown function " + name + " at position " + pos);
    }


    private StepContext readContextItem() throws ParserException {

        final Axis axis = readAxis();
        if (axis != null) {
            skipUnused();
        }

        //parent::  ||  self::
        if (Axis.PARENT.equals(axis) || Axis.SELF.equals(axis)) {
            StepContext stepContext = new StepContext(axis);
            stepContext.setFunction(readFunction());
            return stepContext;
        }

        //child::  ||  attribute::
        if (Axis.CHILD.equals(axis) || Axis.ATTRIBUTE.equals(axis)) {
            StepContext stepContext = new StepContext(axis);
            stepContext.setFunction(readFunction());
            if (stepContext.getFunction() == null) {
                stepContext.setFunction(new FunctionNodeName(readNodeName()));
            }
            return stepContext;
        }

        //..
        if (isMarker(PathConst.PARENT_SHORT)) {
            StepContext stepContext = new StepContext(Axis.PARENT);
            stepContext.setFunction(new FunctionNodeSet());
            return stepContext;
        }

        //.
        if (isMarker(PathConst.SELF_SHORT)) {
            StepContext stepContext = new StepContext(Axis.SELF);
            stepContext.setFunction(new FunctionNodeSet());
            return stepContext;
        }

        //@
        if (isMarker(PathConst.ATTRIBUTE_SHORT)) {
            StepContext stepContext = new StepContext(Axis.ATTRIBUTE);
            stepContext.setFunction(new FunctionNodeName(readNodeName()));
            return stepContext;
        }

        //
        StepContext stepContext = new StepContext(Axis.CHILD);
        stepContext.setFunction(new FunctionNodeName(readNodeName()));
        return stepContext;

    }


}
