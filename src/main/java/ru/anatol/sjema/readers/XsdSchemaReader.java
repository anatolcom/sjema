package ru.anatol.sjema.readers;

import org.w3._2001.xmlschema.Schema;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.Objects;

public class XsdSchemaReader {
    public Schema read(InputStream inputStream) throws ReaderException {
        Objects.requireNonNull(inputStream, "inputStream is null");
        try {
            XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

            JAXBContext context = JAXBContext.newInstance(Schema.class);
            Unmarshaller um = context.createUnmarshaller();
            return (Schema) um.unmarshal(xmlReader);

        } catch (JAXBException | XMLStreamException ex) {
            throw new ReaderException(ex);
        }
    }
}
