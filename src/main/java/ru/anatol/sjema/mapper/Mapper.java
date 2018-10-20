package ru.anatol.sjema.mapper;

import org.w3c.dom.Node;

public interface Mapper {

    /**
     * Преобразование данных XML в данные внутреннего представления.
     *
     * @param node узел дерева XML.
     * @return данные внутреннего представления
     * @throws MapperException
     */
    String getValue(Node node) throws MapperException;

    /**
     * Преобразование данных внутреннего представления в данные XML.
     *
     * @param node  узел дерева XML.
     * @param value данные внутреннего представления
     * @throws MapperException
     */
    void setValue(Node node, String value) throws MapperException;
}
