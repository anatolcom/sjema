package ru.anatol.sjema.mapper;

public interface Producer {

    String name();

    Mapper produce(MapperParams params) throws MapperException;

}
