package ru.anatol.sjema.producer.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.anatol.sjema.converter.ConverterException;
import ru.anatol.sjema.producer.model.temp.TempIdentifier;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.UUID;

public class IdManager {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IdManager.class);

    ModeLayer modeLayer = new ModeLayer();

    public IdManager() {
    }

    public void print() {
        if (modeLayer == null) {
            return;
        }

        OutputStream outputStream = new ByteArrayOutputStream();
        try (PrintStream printStream = new PrintStream(outputStream)) {

            printStream.println();
            printStream.println("ID MAP");

            for (TempIdentifier.Mode mode : modeLayer.getModeSet()) {

                printStream.println("  " + mode.name());
                NamespaceLayer namespaceLayer = modeLayer.getNamespaceLayer(mode);
                if (namespaceLayer == null) {
                    continue;
                }
                for (String namespace : namespaceLayer.getNamespaceSet()) {
                    printStream.println("    " + namespace);
                    NameLayer nameLayer = namespaceLayer.getNameLayer(namespace);
                    if (nameLayer == null) {
                        continue;
                    }
                    for (String name : nameLayer.getNameSet()) {
                        IdLayer idLayer = nameLayer.getIdLayer(name);
                        if (idLayer != null) {
                            printStream.println("      " + name + " - " + idLayer.getId());
                        } else {
                            printStream.println("      " + name + " - undefined");
                        }
                    }
                }
            }
        }
        LOGGER.debug(outputStream.toString());
    }

    /**
     * Регистрация идентификатора
     *
     * @param identifier временный идентификатор
     * @param mode       режим
     * @param id         новый идентификатор или null
     * @param object     связанный объект
     */
    public void registerId(TempIdentifier identifier, TempIdentifier.Mode mode, String id, Object object) throws ConverterException {
        Objects.requireNonNull(identifier);
        if (identifier.getMode() != mode) {
            LOGGER.error("mismatch mode: {} and identifier: {}", mode.name(), identifier);
            throw new ConverterException("mismatch mode: " + mode.name() + " and identifier: " + identifier);
        }
        NamespaceLayer namespaceLayer = modeLayer.getOrRegNamespaceLayer(identifier.getMode());
        NameLayer nameLayer = namespaceLayer.getOrRegNameLayer(identifier.getNamespace());
        IdLayer idLayer = nameLayer.getIdLayer(identifier.getName());
        if (idLayer != null) {
            LOGGER.error("not unique identifier: ", identifier);
            throw new ConverterException("not unique type id of identifier: " + identifier);
        }
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        nameLayer.regIdLayer(identifier.getName(), new IdLayer(id, object));
    }

    /**
     * Получение записи с идентификатором.
     *
     * @param identifier временный идентификатор
     * @return
     */
    private IdLayer getIdLayer(TempIdentifier identifier) {
        Objects.requireNonNull(identifier);
        NamespaceLayer namespaceLayer = modeLayer.getNamespaceLayer(identifier.getMode());
        Objects.requireNonNull(namespaceLayer, "namespace map not found by identifier: " + identifier);
        NameLayer nameLayer = namespaceLayer.getNameLayer(identifier.getNamespace());
        Objects.requireNonNull(nameLayer, "name map not found by identifier: " + identifier);
        IdLayer idLayer = nameLayer.getIdLayer(identifier.getName());
        Objects.requireNonNull(idLayer, "id not found by identifier: " + identifier);
        return idLayer;
    }

    /**
     * Получение идентификатора.
     *
     * @param identifier временный идентификатор
     * @return
     */
    public String getId(TempIdentifier identifier) {
        return getIdLayer(identifier).getId();
    }

    /**
     * Получение объекта.
     *
     * @param identifier временный идентификатор
     * @return
     */
    public Object getObject(TempIdentifier identifier) {
        return getIdLayer(identifier).getObject();
    }

}
