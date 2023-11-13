package education.kv.shared.protocol;

import java.util.Arrays;
import java.util.Objects;

public record KVProtocolRequest(KVProtocolCommand command, String key, byte[] data) {

    public KVProtocolRequest(KVProtocolCommand command, String key, byte[] data) {
        this.command = Objects.requireNonNull(command);
        this.key = key != null ? key : "";
        this.data = data != null ? data : KVProtocolUtils.EMPTY_ARRAY;
    }

    public KVProtocolRequest(KVProtocolCommand command, String key) {
        this(command, key, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KVProtocolRequest request = (KVProtocolRequest) o;
        return command == request.command && Objects.equals(key, request.key) && Arrays.equals(
            data,
            request.data
        );
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(command, key);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "KVProtocolRequest{"
            + "command=" + command
            + ", key='" + key + '\''
            + ", data=" + Arrays.toString(data)
            + '}';
    }
}
