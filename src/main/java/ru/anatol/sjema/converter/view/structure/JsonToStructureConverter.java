package ru.anatol.sjema.converter.view.structure;

import org.json.JSONObject;
import ru.anatol.sjema.converter.ConverterException;

public interface JsonToStructureConverter<Structure> {

    public Structure convert(JSONObject jsonStructure) throws ConverterException;

}

