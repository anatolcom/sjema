package ru.anatol.sjema.simple.structure.converter;

import org.json.JSONObject;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.converter.view.structure.StructureToJsonConverter;
import ru.anatol.sjema.simple.structure.SimpleStructure;


public class StructureToJsonConverterImpl implements StructureToJsonConverter<SimpleStructure> {

    public StructureToJsonConverterImpl() {
    }

    @Override
    public JSONObject convert(SimpleStructure structure) throws ConverterException {
        return new JSONObject();
    }

}
