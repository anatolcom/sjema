package ru.anatol.sjema.test.simple;

import org.w3._2001.xmlschema.Schema;
import ru.anatol.sjema.producer.XsdResolver;
import ru.anatol.sjema.test.simple.TestUtil;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ResourceXsdResolver extends XsdResolver {

    /**
     * Путь к базовой директории.
     */
    private final String baseDirPath;

    public ResourceXsdResolver(String path) {
        this.baseDirPath = path;
    }

    protected String generateId(String path) throws Exception {
        final String filename = new File(new File(baseDirPath), path).getPath();
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new IOException("resource with name: \"" + filename + "\" not found");
            }
            return getHash(inputStream);
        }
    }

    protected Schema getSchema(String path) throws Exception {
        final String filename = new File(new File(baseDirPath), path).getPath();
        return TestUtil.getSchemaFromXsd(filename);
    }

}
