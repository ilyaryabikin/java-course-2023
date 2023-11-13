package education.kv.server;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class Server {

    public static void main(String[] args) throws IOException {
        TcpKeyValueServerMap tcpKeyValueServerMap =
            new TcpKeyValueServerMap(
                8080,
                new DiskBackedMap<>(new HashMap<>(), Path.of("data.bin"))
            );

        tcpKeyValueServerMap.listen();
    }

}
