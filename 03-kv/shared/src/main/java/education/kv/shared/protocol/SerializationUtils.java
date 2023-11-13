package education.kv.shared.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("MagicNumber")
public final class SerializationUtils {

    private SerializationUtils() {
    }

    public static byte[] serializeObject(Serializable object) throws IOException {
        try (
            var baos = new ByteArrayOutputStream();
            var objectOutputStream = new ObjectOutputStream(baos)
        ) {
            objectOutputStream.writeObject(object);
            return baos.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deserializeObject(byte[] bytes)
        throws IOException, ClassNotFoundException, ClassCastException {
        try (var objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T) objectInputStream.readObject();
        }
    }

    public static <E extends Enum<E>> E deserializeEnum(byte[] array, int offset, int length, Class<E> eClass) {
        String enumName = new String(array, offset, length).trim();
        return E.valueOf(eClass, enumName);
    }

    public static <E extends Enum<E>> byte[] serializeEnum(E value, int byteArrayLength) {
        byte[] enumBytes = value.name().getBytes();
        if (enumBytes.length > byteArrayLength) {
            throw new IllegalArgumentException("Requested byteArrayLength is less than actual enum bytes");
        }

        if (enumBytes.length == byteArrayLength) {
            return enumBytes;
        }

        byte[] result = new byte[byteArrayLength];
        System.arraycopy(enumBytes, 0, result, 0, enumBytes.length);
        return result;
    }

    public static int deserializeInt(byte[] array, int offset) {
        if (array.length < 4 + offset || offset < 0) {
            throw new IllegalArgumentException("Invalid array length or offset");
        }

        int result = 0;
        for (int i = offset; i < 4 + offset; i++) {
            result |= (array[i] & 0xFF) << (8 * (i - offset)); // little-endian byte order
        }
        return result;
    }

    public static byte[] serializeInt(int val) {
        byte[] byteArray = new byte[4];
        for (int i = 0; i < 4; i++) {
            byteArray[i] = (byte) ((val >> (i * 8)) & 0xFF); // little-endian byte order
        }
        return byteArray;
    }
}
