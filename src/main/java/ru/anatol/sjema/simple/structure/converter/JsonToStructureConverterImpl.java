package ru.anatol.sjema.simple.structure.converter;

import org.json.JSONObject;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.converter.view.structure.JsonToStructureConverter;
import ru.anatol.sjema.simple.structure.SimpleStructure;

import java.util.Objects;

public class JsonToStructureConverterImpl implements JsonToStructureConverter<SimpleStructure> {

    public JsonToStructureConverterImpl() {
    }

    @Override
    public SimpleStructure convert(JSONObject jsonStructure) throws ConverterException {
        Objects.requireNonNull(jsonStructure, "jsonStructure is null");
        return new SimpleStructure();
    }

}

