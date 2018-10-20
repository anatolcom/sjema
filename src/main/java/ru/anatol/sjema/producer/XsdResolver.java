package ru.anatol.sjema.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2001.xmlschema.Schema;
import ru.anatol.sjema.converter.ConverterException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class XsdResolver implements XsdSchemaResolver {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XsdResolver.class);

    /**
     * Карта путей.
     * key - id
     * value - path
     */
    private Map<String, String> pathMap = new HashMap();

    @Override
    public final String getId(String schemaLocation, String parentSchemaId) throws ConverterException {
        try {
            final String path = getPath(schemaLocation, parentSchemaId);
            //LOGGER.debug("path by schemaLocation: {}, parentSchemaId: {}, path: {}", schemaLocation, parentSchemaId, path);
            final String id = generateId(path);
            if (!pathMap.containsKey(id)) {
                pathMap.put(id, path);
                //LOGGER.debug("registry id: \"{}\" - path: \"{}\"", id, path);
            }
            return id;
        } catch (Exception ex) {
            throw new ConverterException(ex);
        }
    }

    @Override
    public final Schema resolve(String schemaLocation, String parentSchemaId) throws ConverterException {
        try {
            final String path = getPath(schemaLocation, parentSchemaId);
            //LOGGER.debug("path by schemaLocation: {}, parentSchemaId: {}, path: {}", schemaLocation, parentSchemaId, path);
            final Schema schema = getSchema(path);
            LOGGER.debug("resolve schemaLocation: \"{}\" as \"{}\"", schemaLocation, path);
            return schema;
        } catch (Exception ex) {
            throw new ConverterException("schemaLocation: \"" + schemaLocation + "\" can not resolved, because: " + ex.getMessage(), ex);
        }
    }

    protected final String getPath(String schemaLocation, String parentSchemaId) {
        //путь относительно расположения головного файла
        if (schemaLocation.startsWith("./")) {
            return Paths.get(schemaLocation.substring(2)).normalize().toString();
        }
        //путь относительно расположения файла, из которого ссылка
        final Path parent = getParentPath(parentSchemaId);
        if (parent != null) {
            return parent.resolve(schemaLocation).normalize().toString();
        }
        return Paths.get(schemaLocation).normalize().toString();
    }

    private Path getParentPath(String parentSchemaId) {
        if (parentSchemaId == null || !pathMap.containsKey(parentSchemaId)) {
            return null;
        }
        return Paths.get(pathMap.get(parentSchemaId)).getParent();
    }

    protected abstract String generateId(String path) throws Exception;

    protected abstract Schema getSchema(String path) throws Exception;

    public static String getHash(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        Objects.requireNonNull(inputStream);
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");//SHA-256
        final DigestInputStream dis = new DigestInputStream(inputStream, messageDigest);
        while (dis.read() != -1) ;
        return new BigInteger(messageDigest.digest()).toString(16).toUpperCase();
    }

}
