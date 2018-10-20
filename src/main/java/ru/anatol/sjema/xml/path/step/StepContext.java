package ru.anatol.sjema.xml.path.step;

import ru.anatol.sjema.xml.path.function.Function;
import ru.anatol.sjema.xml.path.operation.Operation;

import java.util.ArrayList;
import java.util.List;

public class StepContext implements Step {

    private final Axis axis;
    private Function function;
    private final List<Operation> conditions = new ArrayList<>();

    public StepContext(Axis axis) {
        this.axis = axis;
    }

    public Axis getAxis() {
        return axis;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public List<Operation> getConditions() {
        return conditions;
    }

}
