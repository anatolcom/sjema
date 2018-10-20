package ru.anatol.sjema.xml.path.operator;

import ru.anatol.sjema.xml.path.operation.Operation;

public class OperatorEquals implements Operator {

    private final Operation operandA;
    private final Operation operandB;

    public OperatorEquals(Operation operandA, Operation operandB) {
        this.operandA = operandA;
        this.operandB = operandB;
    }

    public Operation getOperandA() {
        return operandA;
    }

    public Operation getOperandB() {
        return operandB;
    }
}
