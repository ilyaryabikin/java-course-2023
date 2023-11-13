package education.kv.shared.protocol;

import static education.kv.shared.protocol.SerializationUtils.deserializeEnum;
import static education.kv.shared.protocol.SerializationUtils.deserializeInt;
import static education.kv.shared.protocol.SerializationUtils.serializeEnum;
import static education.kv.shared.protocol.SerializationUtils.serializeInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

public final class KVProtocolUtils {

    public static final byte[] EMPTY_ARRAY = new byte[]{};
    public static final byte[] EXPECTED_PROTOCOL_MESSAGE_MARKER = "0xAB".getBytes();
    public static final int KEY_SIZE_FRAME_WIDTH = 4;
    public static final int DATA_SIZE_FRAME_WIDTH = 4;
    public static final int COMMAND_FRAME_WIDTH = 16;
    public static final int ERROR_SIZE_FRAME_WIDTH = 4;

    private KVProtocolUtils() {
    }

    public static KVProtocolRequest readRequestFromInputStream(InputStream inputStream) throws IOException {
        validateMarker(inputStream);

        byte[] header = inputStream.readNBytes(KEY_SIZE_FRAME_WIDTH + DATA_SIZE_FRAME_WIDTH + COMMAND_FRAME_WIDTH);

        int keySize = deserializeInt(header, 0);
        int dataSize = deserializeInt(header, KEY_SIZE_FRAME_WIDTH);
        KVProtocolCommand command = deserializeEnum(
            header,
            KEY_SIZE_FRAME_WIDTH + DATA_SIZE_FRAME_WIDTH,
            COMMAND_FRAME_WIDTH,
            KVProtocolCommand.class
        );

        byte[] key = keySize > 0 ? inputStream.readNBytes(keySize) : EMPTY_ARRAY;
        byte[] data = dataSize > 0 ? inputStream.readNBytes(dataSize) : EMPTY_ARRAY;

        return new KVProtocolRequest(command, new String(key), data);
    }

    public static void writeRequestIntoOutputStream(
        KVProtocolRequest request,
        OutputStream outputStream
    ) throws IOException {
        byte[] keyBytes = request.key().getBytes();
        byte[] dataBytes = request.data();
        byte[] commandBytes = serializeEnum(request.command(), COMMAND_FRAME_WIDTH);

        int keySize = keyBytes.length;
        int dataSize = dataBytes.length;

        byte[] keySizeBytes = serializeInt(keySize);
        byte[] dataSizeBytes = serializeInt(dataSize);

        byte[] dataBuffer = combineArrays(
            EXPECTED_PROTOCOL_MESSAGE_MARKER,
            keySizeBytes,
            dataSizeBytes,
            commandBytes,
            keyBytes,
            dataBytes
        );

        outputStream.write(dataBuffer);
    }

    public static KVProtocolResponse readResponseFromInputStream(InputStream inputStream) throws IOException {
        validateMarker(inputStream);

        byte[] header = inputStream.readNBytes(KEY_SIZE_FRAME_WIDTH + DATA_SIZE_FRAME_WIDTH + ERROR_SIZE_FRAME_WIDTH);

        int keySize = deserializeInt(header, 0);
        int dataSize = deserializeInt(header, KEY_SIZE_FRAME_WIDTH);
        int errorSize = deserializeInt(header, KEY_SIZE_FRAME_WIDTH + DATA_SIZE_FRAME_WIDTH);

        byte[] key = keySize > 0 ? inputStream.readNBytes(keySize) : EMPTY_ARRAY;
        byte[] data = dataSize > 0 ? inputStream.readNBytes(dataSize) : EMPTY_ARRAY;
        byte[] error = errorSize > 0 ? inputStream.readNBytes(errorSize) : EMPTY_ARRAY;

        return new KVProtocolResponse(new String(key), data, error);
    }

    public static void writeResponseIntoOutputStream(
        KVProtocolResponse response,
        OutputStream outputStream
    ) throws IOException {
        byte[] keyBytes = response.key().getBytes();
        byte[] dataBytes = response.data();
        byte[] errorBytes = response.error();

        int keySize = keyBytes.length;
        int dataSize = dataBytes.length;
        int errorSize = errorBytes.length;

        byte[] keySizeBytes = serializeInt(keySize);
        byte[] dataSizeBytes = serializeInt(dataSize);
        byte[] errorSizeBytes = serializeInt(errorSize);

        byte[] dataBuffer = combineArrays(
            EXPECTED_PROTOCOL_MESSAGE_MARKER,
            keySizeBytes,
            dataSizeBytes,
            errorSizeBytes,
            keyBytes,
            dataBytes,
            errorBytes
        );

        outputStream.write(dataBuffer);
    }

    private static void validateMarker(InputStream inputStream) throws IOException {
        byte[] messageMarker = inputStream.readNBytes(EXPECTED_PROTOCOL_MESSAGE_MARKER.length);
        if (!Arrays.equals(messageMarker, EXPECTED_PROTOCOL_MESSAGE_MARKER)) {
            throw new IllegalArgumentException("Provided InputStream does not satisfy KV Protocol Format");
        }
    }

    private static byte[] combineArrays(byte[]... arrays) {
        long arraysTotalLength = 0;

        for (byte[] array : arrays) {
            Objects.requireNonNull(array);
            arraysTotalLength += array.length;
        }

        if (arraysTotalLength > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Combined array length exceeds maximum allowed length");
        }

        byte[] result = new byte[(int) arraysTotalLength];
        int currentOffset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentOffset, array.length);
            currentOffset += array.length;
        }

        return result;
    }

}
