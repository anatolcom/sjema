package ru.anatol.sjema.readers;

import org.json.JSONObject;
import org.json.JSONTokener;
import ru.anatol.sjema.converter.view.JsonViewToViewModelConverter;
import ru.anatol.sjema.JsonUtil;
import ru.anatol.sjema.model.view.ViewModel;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;

public class ViewModelReader {
    @Deprecated
    public ViewModel read(InputStream inputStream, Charset charset) throws ReaderException {
        Objects.requireNonNull(inputStream, "inputStream is null");
        try {
            final JSONObject jsonObject = new JSONObject(new JSONTokener(new InputStreamReader(inputStream, charset)));
            return new JsonViewToViewModelConverter().convert(jsonObject);
        } catch (Exception ex) {
            throw new ReaderException(ex);
        }
    }

    public ViewModel read(byte[] content, Charset charset) throws ReaderException {
        Objects.requireNonNull(content, "content is null");
        try {
            final JSONObject jsonObject = JsonUtil.parseJsonAsObject(content, charset);
            return new JsonViewToViewModelConverter().convert(jsonObject);
        } catch (Exception ex) {
            throw new ReaderException(ex);
        }
    }
}
