package education.kv.server.test;

import static org.junit.jupiter.api.Assertions.assertAll;

import education.kv.server.DiskBackedMap;
import education.kv.shared.protocol.SerializationUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiskBackedMapTest implements WithAssertions {

    private DiskBackedMap<HashMap<String, Integer>, String, Integer> diskBackedMap;
    private Path filePath;

    @BeforeEach
    void setUp() throws IOException {
        filePath = Files.createTempFile(null, null);
        diskBackedMap = new DiskBackedMap<>(new HashMap<>(), filePath);
    }

    @Test
    void shouldThrowExceptionWhenFileDataIsIllegal() throws IOException {
        Files.writeString(filePath, "Hello, world!");

        assertThatThrownBy(() -> diskBackedMap.loadFromDisk())
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldPutToDelegateAndFlushToDisk() {
        String key = "testKey";
        Integer value = 42;

        diskBackedMap.put(key, value);

        assertAll(
            () -> assertThat(diskBackedMap.getDelegatingMap()).containsEntry(key, value),
            () -> assertFileContains(Map.of(key, value))
        );
    }

    @Test
    void shouldRemoveFromDelegateAndFlushToDisk() {
        String key = "testKey";
        Integer value = 42;
        diskBackedMap.put(key, value);

        diskBackedMap.remove(key);

        assertAll(
            () -> assertThat(diskBackedMap.getDelegatingMap()).doesNotContainKey(key),
            () -> assertFileIsEmpty()
        );
    }

    @Test
    void shouldPutAllToDelegateAndFlushToDisk() {
        Map<String, Integer> map = Map.of("key1", 1, "key2", 2);

        diskBackedMap.putAll(map);

        assertAll(
            () -> assertThat(diskBackedMap.getDelegatingMap()).containsAllEntriesOf(map),
            () -> assertFileContains(map)
        );
    }

    private void assertFileContains(Map<String, Integer> expectedContent) {
        try {
            byte[] bytes = Files.readAllBytes(filePath);
            Map<String, Integer> deserializedMap = SerializationUtils.deserializeObject(bytes);

            for (Map.Entry<String, Integer> entry : expectedContent.entrySet()) {
                assertThat(deserializedMap).contains(entry);
            }
        } catch (Exception e) {
            fail("Failed to read file content", e);
        }
    }

    private void assertFileIsEmpty() {
        try {
            byte[] bytes = Files.readAllBytes(filePath);
            assertThat(bytes).isEmpty();
        } catch (Exception e) {
            fail("Failed to read file content", e);
        }
    }

}
