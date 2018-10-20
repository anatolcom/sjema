package ru.anatol.sjema.validator.structure;

import ru.anatol.sjema.validator.ValidatorException;

public interface StructureValidator<Structure> {

    void validate(Structure structure) throws ValidatorException;

}
