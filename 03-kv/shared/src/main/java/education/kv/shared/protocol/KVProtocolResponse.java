package education.kv.shared.protocol;

import java.util.Arrays;
import java.util.Objects;

public record KVProtocolResponse(String key, byte[] data, byte[] error) {

    public KVProtocolResponse(String key, byte[] data, byte[] error) {
        this.key = key != null ? key : "";
        this.data = data != null ? data : KVProtocolUtils.EMPTY_ARRAY;
        this.error = error != null ? error : KVProtocolUtils.EMPTY_ARRAY;
    }

    public boolean containsError() {
        return !Arrays.equals(error, KVProtocolUtils.EMPTY_ARRAY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KVProtocolResponse that = (KVProtocolResponse) o;
        return Objects.equals(key, that.key) && Arrays.equals(
            data,
            that.data
        ) && Arrays.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(key);
        result = 31 * result + Arrays.hashCode(data);
        result = 31 * result + Arrays.hashCode(error);
        return result;
    }

    @Override
    public String toString() {
        return "KVProtocolResponse{"
            + "key='" + key + '\''
            + ", data=" + Arrays.toString(data)
            + ", error=" + Arrays.toString(error)
            + '}';
    }
}
