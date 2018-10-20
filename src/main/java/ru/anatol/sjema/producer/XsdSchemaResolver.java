package ru.anatol.sjema.producer;

import org.w3._2001.xmlschema.Schema;
import ru.anatol.sjema.converter.ConverterException;

public interface XsdSchemaResolver {

    String getId(String schemaLocation, String parentId) throws ConverterException;

    Schema resolve(String schemaLocation, String parentId) throws ConverterException;

}
