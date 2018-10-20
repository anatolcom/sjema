package ru.anatol.sjema.readers;

import org.w3c.dom.Document;
import ru.anatol.sjema.xml.DomUtil;

import java.io.InputStream;
import java.util.Objects;

public class XmlDataReader {
    public Document read(InputStream inputStream) throws ReaderException {
        Objects.requireNonNull(inputStream, "inputStream is null");
        try {
            return DomUtil.getDocument(inputStream);
        } catch (Exception ex) {
            throw new ReaderException(ex);
        }
    }
}
