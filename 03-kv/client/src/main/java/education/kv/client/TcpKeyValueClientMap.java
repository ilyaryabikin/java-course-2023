package education.kv.client;

import static education.kv.shared.protocol.KVProtocolCommand.GET;
import static education.kv.shared.protocol.KVProtocolCommand.PUT;
import static education.kv.shared.protocol.KVProtocolCommand.REMOVE;

import education.kv.shared.protocol.KVProtocolRequest;
import education.kv.shared.protocol.KVProtocolResponse;
import education.kv.shared.protocol.KVProtocolUtils;
import education.kv.shared.protocol.SerializationUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TcpKeyValueClientMap<V extends Serializable> implements Map<String, V> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final String host;
    private final int port;

    public TcpKeyValueClientMap(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        try {
            KVProtocolResponse response = doSendRequest(new KVProtocolRequest(GET, (String) key));

            if (response.data().length == 0) {
                return null;
            }

            return SerializationUtils.deserializeObject(response.data());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Nullable
    @Override
    public V put(String key, V value) {
        try {
            KVProtocolResponse response = doSendRequest(
                new KVProtocolRequest(
                    PUT,
                    key,
                    SerializationUtils.serializeObject(value)
                )
            );

            if (response.data().length == 0) {
                return null;
            }

            return SerializationUtils.deserializeObject(response.data());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public V remove(Object key) {
        try {
            KVProtocolResponse response = doSendRequest(new KVProtocolRequest(REMOVE, (String) key));

            if (response.data().length == 0) {
                return null;
            }

            return SerializationUtils.deserializeObject(response.data());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Set<Entry<String, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    private KVProtocolResponse doSendRequest(KVProtocolRequest request) throws Exception {
        try (
            Socket socket = getSocket();
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
        ) {
            KVProtocolUtils.writeRequestIntoOutputStream(request, outputStream);

            LOGGER.info("Sent request = {}", request);

            outputStream.flush();

            KVProtocolResponse response = KVProtocolUtils.readResponseFromInputStream(inputStream);

            LOGGER.info("Received response = {}", response);
            if (response.containsError()) {
                LOGGER.warn("Response contains exception bytes");
                throw SerializationUtils.<Exception>deserializeObject(response.error());
            }

            return response;
        }
    }

    private Socket getSocket() throws IOException {
        return new Socket(host, port);
    }
}
