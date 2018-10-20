package ru.anatol.sjema.xml.path.function;

import ru.anatol.sjema.xml.path.PathConst;
import ru.anatol.sjema.xml.path.constant.ConstantNodeName;

public class FunctionNodeName implements Function {

    private final ConstantNodeName nodeName;

    public FunctionNodeName(ConstantNodeName nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String name() {
        return PathConst.FN_NAME;
    }

    public ConstantNodeName getNodeName() {
        return nodeName;
    }

}
