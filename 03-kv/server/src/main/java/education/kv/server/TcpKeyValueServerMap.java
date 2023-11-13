package education.kv.server;

import education.kv.shared.protocol.KVProtocolRequest;
import education.kv.shared.protocol.KVProtocolResponse;
import education.kv.shared.protocol.KVProtocolUtils;
import education.kv.shared.protocol.SerializationUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TcpKeyValueServerMap extends AbstractDelegatingMap<String, byte[]> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final int port;

    public TcpKeyValueServerMap(int port, Map<String, byte[]> delegatingMap) {
        super(delegatingMap);
        this.port = port;
    }

    public void listen() throws IOException {
        try (ServerSocket socket = new ServerSocket(port)) {
            LOGGER.info("Server listening on port {}", port);

            while (true) {
                LOGGER.info("Waiting for a client connection");

                try (
                    Socket clientSocket = socket.accept();
                    InputStream inputStream = clientSocket.getInputStream();
                    OutputStream outputStream = clientSocket.getOutputStream()
                ) {
                    LOGGER.info("New client connected: {}", clientSocket.getInetAddress());

                    handleClientConnection(inputStream, outputStream);
                } catch (Exception e) {
                    LOGGER.error("Error during processing client connection", e);
                }
            }
        }
    }

    private void handleClientConnection(InputStream inputStream, OutputStream outputStream) throws IOException {
        KVProtocolRequest request = KVProtocolUtils.readRequestFromInputStream(inputStream);

        LOGGER.info("Read request = {}", request);

        var command = request.command();

        String key = "";
        byte[] value = KVProtocolUtils.EMPTY_ARRAY;
        byte[] error = KVProtocolUtils.EMPTY_ARRAY;
        try {
            switch (command) {
                case GET -> {
                    key = request.key();
                    value = get(request.key());
                }
                case PUT -> {
                    key = request.key();
                    value = put(request.key(), request.data());
                }
                case REMOVE -> {
                    key = request.key();
                    value = remove(request.key());
                }
                case null -> throw new NullPointerException("Command is null");
            }
        } catch (Exception e) {
            LOGGER.warn("Caught exception while handling the client request", e);
            error = SerializationUtils.serializeObject(e);
        }

        KVProtocolResponse response = new KVProtocolResponse(key, value, error);

        KVProtocolUtils.writeResponseIntoOutputStream(response, outputStream);

        LOGGER.info("Sent response = {}", response);

        outputStream.flush();
    }

}
