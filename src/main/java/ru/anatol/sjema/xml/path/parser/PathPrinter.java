package ru.anatol.sjema.xml.path.parser;

import ru.anatol.sjema.xml.path.constant.Constant;
import ru.anatol.sjema.xml.path.constant.ConstantInteger;
import ru.anatol.sjema.xml.path.constant.ConstantNodeName;
import ru.anatol.sjema.xml.path.function.Function;
import ru.anatol.sjema.xml.path.function.FunctionNodeName;
import ru.anatol.sjema.xml.path.function.FunctionNodeSet;
import ru.anatol.sjema.xml.path.operation.Operation;
import ru.anatol.sjema.xml.path.operation.Path;
import ru.anatol.sjema.xml.path.operator.OperatorEquals;
import ru.anatol.sjema.xml.path.PathConst;
import ru.anatol.sjema.xml.path.XPath;
import ru.anatol.sjema.xml.path.constant.ConstantString;
import ru.anatol.sjema.xml.path.step.Step;
import ru.anatol.sjema.xml.path.step.StepContext;
import ru.anatol.sjema.xml.path.step.StepRoot;

import java.io.PrintStream;
import java.util.Objects;

public class PathPrinter {

    public static void printPath(PrintStream printStream, XPath path) {
        printOperation(printStream, path.getOperation());
    }

    public static void printOperation(PrintStream printStream, Operation operation) {
        Objects.requireNonNull(operation);

        if (operation instanceof Constant) {
            printConstant(printStream, (Constant) operation);
            return;
        }

        if (operation instanceof Path) {
            Path operationPath = (Path) operation;
            boolean first = true;
            for (Step step : operationPath.getSteps()) {
                if (first) {
                    first = false;
                } else {
                    printStream.print(PathConst.STEP);
                }
                PathPrinter.printStep(printStream, step);
            }
            return;
        }

        if (operation instanceof OperatorEquals) {
            OperatorEquals operatorEquals = (OperatorEquals) operation;
            printOperation(printStream, operatorEquals.getOperandA());
            printStream.print(PathConst.OPERATOR_EQUALS);
            printOperation(printStream, operatorEquals.getOperandB());
            return;
        }

        throw new UnsupportedOperationException(operation.getClass().getSimpleName());
    }

    private static void printConstant(PrintStream printStream, Constant constant) {
        Objects.requireNonNull(constant);

        if (constant instanceof ConstantInteger) {
            ConstantInteger constantInteger = (ConstantInteger) constant;
            printStream.print(constantInteger.getValue());
            return;
        }

        if (constant instanceof ConstantString) {
            ConstantString constantString = (ConstantString) constant;
            printStream.print(PathConst.TEXT_BEGIN);
            printStream.print(constantString.getValue());
            printStream.print(PathConst.TEXT_END);
            return;
        }

        throw new UnsupportedOperationException(constant.getClass().getSimpleName());
    }

    private static void printStep(PrintStream printStream, Step step) {
        if (step instanceof StepRoot) {
            return;
        }
        if (step instanceof StepContext) {
            StepContext stepContext = (StepContext) step;
            printPathItem(printStream, stepContext, !stepContext.getConditions().isEmpty());
            for (Operation operation : stepContext.getConditions()) {
                printStream.print(PathConst.CONDITION_BEGIN);
                printOperation(printStream, operation);
                printStream.print(PathConst.CONDITION_END);
            }
        }
    }

    private static void printPathItem(PrintStream printStream, StepContext stepContext, boolean condition) {

        Function function = stepContext.getFunction();
        switch (stepContext.getAxis()) {
            case PARENT:
                if (!condition && function instanceof FunctionNodeSet) {
                    printStream.print(PathConst.PARENT_SHORT);
                    return;
                }
                break;
            case SELF:
                if (!condition && function instanceof FunctionNodeSet) {
                    printStream.print(PathConst.SELF_SHORT);
                    return;
                }
                break;
            case ATTRIBUTE:
                if (!condition && function instanceof FunctionNodeName) {
                    printStream.print(PathConst.ATTRIBUTE_SHORT);
                    printNodeName(printStream, ((FunctionNodeName) function).getNodeName());
                    return;
                }
                break;
            case CHILD:
                if (function instanceof FunctionNodeName) {
                    printNodeName(printStream, ((FunctionNodeName) function).getNodeName());
                    return;
                }
                break;
            default:
                throw new UnsupportedOperationException("unsupported " + stepContext.getAxis());
        }

        printStream.print(stepContext.getAxis().name().toLowerCase());
        printStream.print(PathConst.AXIS);
        printFunction(printStream, stepContext.getFunction());
        return;

    }

    private static void printNodeName(PrintStream printStream, ConstantNodeName nodeName) {
        if (nodeName.getPrefix() != null) {
            printStream.print(nodeName.getPrefix());
            printStream.print(PathConst.PREFIX);
        }
        printStream.print(nodeName.getName());
    }

    private static void printFunction(PrintStream printStream, Function function) {

        if (function instanceof FunctionNodeSet) {
            printStream.print(PathConst.FN_NODE);
            printStream.print(PathConst.FUNCTION_BEGIN);
            printStream.print(PathConst.FUNCTION_END);
            return;
        }

        if (function instanceof FunctionNodeName) {
            printStream.print(PathConst.FN_NAME);
            printStream.print(PathConst.FUNCTION_BEGIN);
            FunctionNodeName functionNodeName = (FunctionNodeName) function;
            ConstantNodeName nodeName = functionNodeName.getNodeName();
            printStream.print(PathConst.TEXT_BEGIN);
            if (nodeName.getPrefix() != null) {
                printStream.print(nodeName.getPrefix());
                printStream.print(PathConst.PREFIX);
            }
            printStream.print(nodeName.getName());
            printStream.print(PathConst.TEXT_END);
            printStream.print(PathConst.FUNCTION_END);
            return;
        }
        throw new UnsupportedOperationException("function " + function.name());
    }
}
