package education.tracker.tracker;

import education.tracker.model.Task;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class InMemoryTaskHistoryManager<T> implements TaskHistoryManager<T> {

    private final LinkedHashSet<Task<T>> history = new LinkedHashSet<>();

    @Override
    public void addTaskToHistory(@NotNull Task<T> task) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(task.getId());

        history.addFirst(task);
    }

    @Override
    public boolean removeTaskFromHistory(@NotNull Task<T> task) {
        Objects.requireNonNull(task);

        return history.remove(task);
    }

    @Override
    public @NotNull List<Task<T>> getViewHistory() {
        return List.copyOf(history);
    }

    @Override
    public void clearViewHistory() {
        history.clear();
    }
}
