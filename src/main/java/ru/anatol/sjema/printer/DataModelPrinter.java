package ru.anatol.sjema.printer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anatol.sjema.model.data.*;

import java.io.PrintStream;
import java.util.Objects;

public class DataModelPrinter implements Printer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataModelPrinter.class);

    private DataModel dataModel;

    public DataModelPrinter(DataModel dataModel) {
        Objects.requireNonNull(dataModel);
        this.dataModel = dataModel;
    }

    @Override
    public String print() {
        return null;
    }

    @Override
    public void print(PrintStream printStream, int indent) {

        ObjectPrinter dataModelPrinter = new ObjectPrinter();

        dataModelPrinter.put(DataConst.VERSION, DataConst.VERSION_VALUE);

        ObjectPrinter contentPrinter = new ObjectPrinter();
        dataModelPrinter.put(DataConst.CONTENT, contentPrinter);

        if (dataModel.getItem() != null) {
            contentPrinter.put(dataModel.getItem().getName(), toItemPrinter(dataModel.getItem()));
        }

        dataModelPrinter.print(printStream, indent);
    }

    private Printer toItemPrinter(DataItem dataItem) {
        if (dataItem instanceof DataSingleValue) {
            DataSingleValue singleValue = (DataSingleValue) dataItem;
            if (singleValue.getValue() == null) {
                return null;
            }
            return toValuePrinter(singleValue.getValue());
        }
        if (dataItem instanceof DataSingleNode) {
            DataSingleNode singleNode = (DataSingleNode) dataItem;
            if (singleNode.getNode() == null) {
                return new ObjectPrinter();
            }
            return toNodePrinter(singleNode.getNode());
        }
        if (dataItem instanceof DataRepeatableValue) {
            DataRepeatableValue multipleValue = (DataRepeatableValue) dataItem;
            ArrayPrinter arrayPrinter = new ArrayPrinter();
            for (DataValue dataValue : multipleValue.getValues()) {
                arrayPrinter.put(toValuePrinter(dataValue));
            }
            return arrayPrinter;
        }
        if (dataItem instanceof DataRepeatableNode) {
            DataRepeatableNode multipleNode = (DataRepeatableNode) dataItem;
            ArrayPrinter arrayPrinter = new ArrayPrinter();
            for (DataNode dataNode : multipleNode.getNodes()) {
                arrayPrinter.put(toNodePrinter(dataNode));
            }
            return arrayPrinter;
        }
        throw new UnsupportedOperationException("unknown data item mode: " + dataItem.getClass().getSimpleName());
    }

    private Printer toValuePrinter(DataValue dataValue) {
        switch (dataValue.getBaseType()) {
            case BOOLEAN:
                return new BooleanPrinter(Boolean.parseBoolean(dataValue.getValue()));
            case NUMBER:
                try {
                    return new NumberPrinter(Long.valueOf(dataValue.getValue()));
                } catch (NumberFormatException ex) {
                    return new NumberPrinter(Double.valueOf(dataValue.getValue()));
                }
            case STRING:
                return new StringPrinter(dataValue.getValue());
            default:
                throw new UnsupportedOperationException("unknown base type " + dataValue.getBaseType().name());
        }
    }

    private ObjectPrinter toNodePrinter(DataNode dataNode) {
        ObjectPrinter nodePrinter = new ObjectPrinter();
        for (DataItem item : dataNode) {
            if (item != null) {
                nodePrinter.put(item.getName(), toItemPrinter(item));
            }
        }
        return nodePrinter;
    }
}
