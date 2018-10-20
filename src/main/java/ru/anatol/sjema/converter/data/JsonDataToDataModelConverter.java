package ru.anatol.sjema.converter.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anatol.sjema.JsonUtil;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.model.BaseType;
import ru.anatol.sjema.model.data.*;

import java.util.Iterator;

public class JsonDataToDataModelConverter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDataToDataModelConverter.class);

    private DataModel dataModel;

    public JsonDataToDataModelConverter() {
    }

    public DataModel convert(JSONObject jsonData) throws ConverterException {
        try {
            return toDataModel(jsonData);
        } catch (ConverterException ex) {
            throw ex;
        } catch (JSONException ex) {
            throw new ConverterException(ex);
        }
    }

    private DataModel toDataModel(JSONObject jsonData) throws JSONException, ConverterException {

        dataModel = new DataModel();

        if (!DataConst.VERSION_VALUE.equals(JsonUtil.getString(jsonData, DataConst.VERSION, true))) {
            throw new ConverterException("version mismatch");
        }

        JSONObject jsonContent = JsonUtil.getJSONObject(jsonData, DataConst.CONTENT, true);

        Iterator keys = jsonContent.keys();
        if (keys.hasNext()) {
            String key = (String) keys.next();
            DataItem dataItem = toDataItem(key, jsonContent.get(key));
            if (dataItem != null) {
                dataModel.setItem(dataItem);
            }
        }

        return dataModel;
    }

    private DataItem toDataItem(String name, Object item) throws JSONException, ConverterException {
        if (item instanceof JSONArray) {
            return toMultiple(name, (JSONArray) item);
        }
        if (item instanceof JSONObject) {
            return toSingleNode(name, (JSONObject) item);
        }
        return toSingleValue(name, item);
    }

    private DataItem toSingleValue(String name, Object value) throws JSONException, ConverterException {
        DataSingleValue singleValue = new DataSingleValue(name);
        singleValue.setValue(toDataValue(value));
        return singleValue;
    }

    private DataItem toSingleNode(String name, JSONObject jsonObject) throws JSONException, ConverterException {
        DataSingleNode singleNode = new DataSingleNode(name);
        singleNode.setNode(getDataNode(jsonObject));
        return singleNode;
    }

    private DataItem toMultiple(String name, JSONArray jsonArray) throws JSONException, ConverterException {
        if (jsonArray.length() == 0) {
            return null;
        }
        if (jsonArray.get(0) instanceof JSONObject) {
            return toMultipleNode(name, jsonArray);
        }
        return toMultipleValue(name, jsonArray);
    }

    private DataItem toMultipleNode(String name, JSONArray jsonArray) throws JSONException, ConverterException {
        DataRepeatableNode multipleNode = new DataRepeatableNode(name, jsonArray.length());
        for (int q = 0; q < jsonArray.length(); q++) {
            multipleNode.getNodes().add(getDataNode(jsonArray.getJSONObject(q)));
        }
        return multipleNode;
    }

    private DataItem toMultipleValue(String name, JSONArray jsonArray) throws JSONException, ConverterException {
        DataRepeatableValue multipleValue = new DataRepeatableValue(name, jsonArray.length());
        for (int q = 0; q < jsonArray.length(); q++) {
            multipleValue.getValues().add(toDataValue(jsonArray.get(q)));
        }
        return multipleValue;
    }

    private boolean isNull(Object value) {
        if (value == null) {
            return true;
        }
        if (JSONObject.NULL.equals(value)) {
            return true;
        }
        return false;
    }

    private DataValue toDataValue(Object value) throws ConverterException {
        if (isNull(value)) {
            return null;
        }
        if (value instanceof Boolean) {
            return new DataValue(BaseType.BOOLEAN, value.toString());
        }
        if (value instanceof Number) {
            return new DataValue(BaseType.NUMBER, value.toString());
        }
        if (value instanceof String) {
            return new DataValue(BaseType.STRING, value.toString());
        }
        throw new ConverterException("unsupported type " + value.getClass().getSimpleName());
    }

    private DataNode getDataNode(JSONObject jsonObject) throws JSONException, ConverterException {
        DataNode dataNode = new DataNode(jsonObject.length());
        Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            DataItem dataItem = toDataItem(key, jsonObject.get(key));
            if (dataItem != null) {
                dataNode.add(dataItem);
            }
        }
        return dataNode;
    }

}

