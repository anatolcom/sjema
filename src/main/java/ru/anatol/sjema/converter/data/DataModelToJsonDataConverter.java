package ru.anatol.sjema.converter.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.model.data.*;

public class DataModelToJsonDataConverter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataModelToJsonDataConverter.class);

    public DataModelToJsonDataConverter() {
    }

    public JSONObject convert(DataModel dataModel) throws ConverterException {
        try {
            return toJsonData(dataModel);
        } catch (ConverterException ex) {
            throw ex;
        } catch (JSONException ex) {
            throw new ConverterException(ex);
        }
    }

    private JSONObject toJsonData(DataModel dataModel) throws JSONException, ConverterException {
        JSONObject jsonData = new JSONObject();

        jsonData.put(DataConst.VERSION, DataConst.VERSION_VALUE);

        JSONObject jsonContent = new JSONObject();
        jsonData.put(DataConst.CONTENT, jsonContent);

        if (dataModel.getItem() != null) {
            jsonContent.put(dataModel.getItem().getName(), toJsonItem(dataModel.getItem()));
        }

        return jsonData;
    }

    private Object toJsonItem(DataItem dataItem) throws JSONException, ConverterException {
        if (dataItem instanceof DataSingleValue) {
            DataSingleValue singleValue = (DataSingleValue) dataItem;
            if (singleValue.getValue() == null) {
                return null;
            }
            return toJsonValue(singleValue.getValue());
        }
        if (dataItem instanceof DataSingleNode) {
            DataSingleNode singleNode = (DataSingleNode) dataItem;
            if (singleNode.getNode() == null) {
                return new JSONObject();
            }
            return toJsonNode(singleNode.getNode());
        }
        if (dataItem instanceof DataRepeatableValue) {
            DataRepeatableValue multipleValue = (DataRepeatableValue) dataItem;
            JSONArray jsonArray = new JSONArray();
            for (DataValue dataValue : multipleValue.getValues()) {
                jsonArray.put(toJsonValue(dataValue));
            }
            return jsonArray;
        }
        if (dataItem instanceof DataRepeatableNode) {
            DataRepeatableNode multipleNode = (DataRepeatableNode) dataItem;
            JSONArray jsonArray = new JSONArray();
            for (DataNode dataNode : multipleNode.getNodes()) {
                jsonArray.put(toJsonNode(dataNode));
            }
            return jsonArray;
        }
        throw new UnsupportedOperationException("unknown data item mode: " + dataItem.getClass().getSimpleName());
    }

    private Object toJsonValue(DataValue dataValue) throws ConverterException {
        switch (dataValue.getBaseType()) {
            case BOOLEAN:
                return Boolean.parseBoolean(dataValue.getValue());
            case NUMBER:
                try {
                    return Long.valueOf(dataValue.getValue());
                } catch (NumberFormatException ex) {
                    return Double.valueOf(dataValue.getValue());
                }
            case STRING:
                return dataValue.getValue();
            default:
                throw new ConverterException("unknown base type " + dataValue.getBaseType().name());
        }
    }


    private JSONObject toJsonNode(DataNode dataNode) throws JSONException, ConverterException {
        JSONObject jsonNode = new JSONObject();
        for (DataItem item : dataNode) {
            if (item != null) {
                jsonNode.put(item.getName(), toJsonItem(item));
            }
        }
        return jsonNode;
    }
}
