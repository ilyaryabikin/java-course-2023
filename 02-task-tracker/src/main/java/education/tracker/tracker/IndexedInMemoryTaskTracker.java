package education.tracker.tracker;

import education.tracker.model.Task;
import education.tracker.model.TaskStatus;
import education.tracker.sequence.IdSequenceGenerator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IndexedInMemoryTaskTracker<T extends Comparable<T>> implements TaskTracker<T> {

    private final Map<T, Task<T>> taskMap;
    private final SortedSet<Task<T>> priorityIndex;
    private final IdSequenceGenerator<T> idSequenceGenerator;
    private final TaskHistoryManager<T> taskHistoryManager;

    public IndexedInMemoryTaskTracker(final @NotNull IdSequenceGenerator<T> idSequenceGenerator) {
        this(idSequenceGenerator, null);
    }

    public IndexedInMemoryTaskTracker(
        final @NotNull IdSequenceGenerator<T> idSequenceGenerator,
        final @Nullable TaskHistoryManager<T> taskHistoryManager
    ) {
        this.taskMap = new HashMap<>();
        this.priorityIndex = new TreeSet<>(
            Comparator.<Task<T>>comparingInt(Task::getPriority)
                .reversed()
                .thenComparing(Task::getId)
        );
        this.idSequenceGenerator = idSequenceGenerator;
        this.taskHistoryManager = taskHistoryManager;
    }

    @Override
    public @NotNull Task<T> save(@NotNull Task<T> task) {
        Objects.requireNonNull(task);

        if (task.getId() == null) {
            task.setId(idSequenceGenerator.next());
        } else {
            idSequenceGenerator.setCurrentIfApplicable(task.getId());
        }

        taskMap.put(task.getId(), task);
        priorityIndex.add(task);

        addTaskToHistory(task);

        return task;
    }

    @Override
    public Task<T> getById(@NotNull T id) {
        Objects.requireNonNull(id);

        var result = taskMap.get(id);

        addTaskToHistory(result);

        return result;
    }

    @Override
    public @NotNull List<Task<T>> getAll() {
        return List.copyOf(taskMap.values());
    }

    @Override
    public Task<T> remove(@NotNull T id) {
        Objects.requireNonNull(id);

        var removedTask = taskMap.remove(id);
        if (removedTask != null) {
            priorityIndex.remove(removedTask);
        }

        removeTaskFromHistory(removedTask);

        return removedTask;
    }

    @Override
    public @NotNull List<Task<T>> getOrderedByPriority() {
        return List.copyOf(priorityIndex);
    }

    @Override
    public Task<T> getMostPriority() {
        try {
            var result = priorityIndex.first();

            addTaskToHistory(result);

            return result;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public Task<T> getLeastPriority() {
        try {
            var result = priorityIndex.last();

            addTaskToHistory(result);

            return result;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public @NotNull List<Task<T>> getOrderedByPriorityWithStatus(@NotNull TaskStatus status) {
        Objects.requireNonNull(status);

        return priorityIndex.stream()
            .filter(task -> status.equals(task.getStatus()))
            .toList();
    }

    @Override
    public @NotNull Map<TaskStatus, List<Task<T>>> getOrderedByPriorityGroupedByStatus() {
        return priorityIndex.stream()
            .collect(Collectors.groupingBy(Task::getStatus));
    }

    private void addTaskToHistory(@Nullable Task<T> task) {
        if (task != null) {
            getTaskHistoryManager().ifPresent(manager -> manager.addTaskToHistory(task));
        }
    }

    private void removeTaskFromHistory(@Nullable Task<T> task) {
        if (task != null) {
            getTaskHistoryManager().ifPresent(manager -> manager.removeTaskFromHistory(task));
        }
    }

    private Optional<TaskHistoryManager<T>> getTaskHistoryManager() {
        return Optional.ofNullable(taskHistoryManager);
    }
}
