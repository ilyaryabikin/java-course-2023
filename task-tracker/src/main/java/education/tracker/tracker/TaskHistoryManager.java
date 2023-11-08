package education.tracker.tracker;

import education.tracker.model.Task;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface TaskHistoryManager<T> {

    void addTaskToHistory(@NotNull Task<T> task);

    boolean removeTaskFromHistory(@NotNull Task<T> task);

    @NotNull
    List<Task<T>> getViewHistory();

    void clearViewHistory();

}
