package ru.anatol.sjema;

public class BomUtil {

    private static final byte[] BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    public static boolean haveBOM(byte[] buf) {
        if (buf == null || buf.length < 3) {
            return false;
        }
        return buf[0] == BOM[0] && buf[1] == BOM[1] && buf[2] == BOM[2];
    }

    public static byte[] removeBOM(byte[] buf) {
        if (buf == null) {
            return null;
        }
        if (!haveBOM(buf)) {
            return buf;
        }
        byte b[] = new byte[buf.length - 3];
        for (int q = 0; q < b.length; q++) {
            b[q] = buf[q + 3];
        }
        return b;
    }

}
