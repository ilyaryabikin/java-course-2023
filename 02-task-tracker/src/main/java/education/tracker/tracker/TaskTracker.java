package education.tracker.tracker;

import education.tracker.model.Task;
import education.tracker.model.TaskStatus;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface TaskTracker<T> {

    @NotNull
    Task<T> save(@NotNull Task<T> task);

    @Nullable
    Task<T> getById(@NotNull T id);

    @NotNull
    @Unmodifiable
    List<Task<T>> getAll();

    @Nullable
    Task<T> remove(@NotNull T id);

    @NotNull
    @Unmodifiable
    List<Task<T>> getOrderedByPriority();

    @Nullable
    Task<T> getMostPriority();

    @Nullable
    Task<T> getLeastPriority();

    @NotNull
    @Unmodifiable
    List<Task<T>> getOrderedByPriorityWithStatus(@NotNull TaskStatus status);

    @NotNull
    @Unmodifiable
    Map<TaskStatus, List<Task<T>>> getOrderedByPriorityGroupedByStatus();
}
