package education.kv.client;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        TcpKeyValueClientMap<String> tcpKeyValueClientMap = new TcpKeyValueClientMap<>("localhost", 8080);

        String key = tcpKeyValueClientMap.get("key");
        LOGGER.info("get(\"key\") -> {}", key);

        String value = tcpKeyValueClientMap.put("key", "value");
        LOGGER.info("put(\"key\", \"value\") -> {}", value);

        value = tcpKeyValueClientMap.put("large", Stream.generate(() -> "c").limit(8192).collect(Collectors.joining()));
        LOGGER.info("put(\"larger\", \"cccc...\") -> {}", value);

        key = tcpKeyValueClientMap.get("key");
        LOGGER.info("get(\"key\") -> {}", key);

        boolean containsKey = tcpKeyValueClientMap.containsKey("key");
        LOGGER.info("containsKey(\"key\") -> {}", containsKey);

        tcpKeyValueClientMap.put("value", "value");
        LOGGER.info("put(\"value\", \"value\") -> {}", key);

        key = tcpKeyValueClientMap.get("value");
        LOGGER.info("get(\"value\") -> {}", key);

        String removed = tcpKeyValueClientMap.remove("removed");
        LOGGER.info("remove(\"removed\") -> {}", removed);

        removed = tcpKeyValueClientMap.remove("value");
        LOGGER.info("remove(\"value\") -> {}", removed);
    }
}
