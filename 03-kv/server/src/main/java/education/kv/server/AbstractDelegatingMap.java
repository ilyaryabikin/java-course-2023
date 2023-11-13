package education.kv.server;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDelegatingMap<K, V> implements Map<K, V> {

    private final Map<K, V> delegate;

    protected AbstractDelegatingMap(Map<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    public Map<K, V> getDelegatingMap() {
        return delegate;
    }
}
