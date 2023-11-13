package education.kv.server;


import education.kv.shared.protocol.SerializationUtils;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiskBackedMap<T extends Map<K, V> & Serializable, K, V> extends AbstractDelegatingMap<K, V> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Path filePath;

    public DiskBackedMap(T delegate, Path filePath) {
        super(delegate);
        this.filePath = filePath;
        loadFromDisk();
    }

    @Override
    public @Nullable V put(K key, V value) {
        var result = super.put(key, value);
        flushToDisk();
        return result;
    }

    @Override
    public V remove(Object key) {
        var result = super.remove(key);
        flushToDisk();
        return result;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        super.putAll(m);
        flushToDisk();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getDelegatingMap() {
        return (T) super.getDelegatingMap();
    }

    public void loadFromDisk() {
        try (
            FileChannel channel = FileChannel.open(
                filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.READ
            )
        ) {
            ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
            channel.read(buffer);

            if (buffer.array().length == 0) {
                LOGGER.info("\"{}\" contains no data", filePath);
                super.clear();
                return;
            }

            T deserialized = SerializationUtils.deserializeObject(buffer.array());

            LOGGER.info("Loaded {} bytes from \"{}\"", buffer.array().length, filePath);

            super.clear();
            super.putAll(deserialized);
        } catch (Exception e) {
            LOGGER.error("Caught an exception during loading DiskBackedMap from the disk", e);
            throw new IllegalStateException(e);
        }
    }

    public void flushToDisk() {
        try (
            FileChannel channel = FileChannel.open(
                filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
            )
        ) {
            if (getDelegatingMap().isEmpty()) {
                LOGGER.info("Truncated \"{}\" as delegating map is empty", filePath);
                return;
            }

            byte[] serialized = SerializationUtils.serializeObject(getDelegatingMap());
            ByteBuffer buffer = ByteBuffer.wrap(serialized);
            channel.write(buffer);

            LOGGER.info("Flushed {} bytes to \"{}\"", buffer.array().length, filePath);
        } catch (Exception e) {
            LOGGER.error("Caught an exception during flushing DiskBackedMap to the disk", e);
            throw new IllegalStateException(e);
        }
    }

}
