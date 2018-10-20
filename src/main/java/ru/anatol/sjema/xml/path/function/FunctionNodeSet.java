package ru.anatol.sjema.xml.path.function;

import ru.anatol.sjema.xml.path.PathConst;

public class FunctionNodeSet implements Function {

    public FunctionNodeSet() {
    }

    @Override
    public String name() {
        return PathConst.FN_NODE;
    }

}
