package ru.anatol.sjema.converter.view.structure;

import org.json.JSONObject;
import ru.anatol.sjema.converter.ConverterException;

public interface StructureToJsonConverter<Structure> {

    public JSONObject convert(Structure structure) throws ConverterException;

}
