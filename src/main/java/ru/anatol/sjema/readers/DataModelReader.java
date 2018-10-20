package ru.anatol.sjema.readers;

import org.json.JSONObject;
import org.json.JSONTokener;
import ru.anatol.sjema.converter.data.JsonDataToDataModelConverter;
import ru.anatol.sjema.model.data.DataModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;

public class DataModelReader {
    public DataModel read(InputStream inputStream, Charset charset) throws ReaderException {
        Objects.requireNonNull(inputStream, "inputStream is null");
        try {
            JSONObject jsonObject = new JSONObject(new JSONTokener(new InputStreamReader(inputStream, charset)));
            return new JsonDataToDataModelConverter().convert(jsonObject);
        } catch (Exception ex) {
            throw new ReaderException(ex);
        }
    }

    public DataModel read(String value) throws ReaderException {
        Objects.requireNonNull(value, "value is null");
        try {
            JSONObject jsonObject = new JSONObject(new JSONTokener(value));
            return new JsonDataToDataModelConverter().convert(jsonObject);
        } catch (Exception ex) {
            throw new ReaderException(ex);
        }
    }

    public DataModel read(String value, Charset charset) throws ReaderException {
        try (InputStream inputStream = new ByteArrayInputStream(value.getBytes(charset))) {
            return read(inputStream, charset);
        } catch (IOException ex) {
            throw new ReaderException(ex);
        }
    }
}
