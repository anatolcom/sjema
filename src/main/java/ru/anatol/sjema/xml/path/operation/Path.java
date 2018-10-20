package ru.anatol.sjema.xml.path.operation;

import ru.anatol.sjema.xml.path.step.Step;

import java.util.ArrayList;
import java.util.List;

public class Path implements Operation {

    private final List<Step> steps = new ArrayList<>();

    public Path() {
    }

    public List<Step> getSteps() {
        return steps;
    }

}
