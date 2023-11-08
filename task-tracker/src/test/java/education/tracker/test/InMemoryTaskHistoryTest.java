package education.tracker.test;

import static education.tracker.test.TestTaskUtil.createDefaultTask;
import static org.junit.jupiter.api.Assertions.assertAll;

import education.tracker.model.Task;
import education.tracker.sequence.UUIDSequenceGenerator;
import education.tracker.tracker.InMemoryTaskHistoryManager;
import education.tracker.tracker.IndexedInMemoryTaskTracker;
import education.tracker.tracker.TaskHistoryManager;
import education.tracker.tracker.TaskTracker;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class InMemoryTaskHistoryTest implements WithAssertions {

    private final TaskHistoryManager<UUID> historyManager = new InMemoryTaskHistoryManager<>();
    private final TaskTracker<UUID> tracker = new IndexedInMemoryTaskTracker<>(
        new UUIDSequenceGenerator(),
        historyManager
    );

    @Test
    void shouldAddTaskToHistoryAfterSaving() {
        Task<UUID> task = createDefaultTask();

        var savedTask = tracker.save(task);

        List<Task<UUID>> tasks = historyManager.getViewHistory();

        assertThat(tasks)
            .containsExactly(savedTask);
    }

    @Test
    void shouldAddTaskToHistoryAfterSavingInTheReverseOrder() {
        List<Task<UUID>> savedTasks = Stream.of(
                TestTaskUtil.<UUID>createDefaultTask(),
                TestTaskUtil.<UUID>createDefaultTask(),
                TestTaskUtil.<UUID>createDefaultTask()
            )
            .map(tracker::save)
            .toList()
            .reversed();

        List<Task<UUID>> tasks = historyManager.getViewHistory();

        assertThat(tasks)
            .containsExactlyElementsOf(savedTasks);
    }

    @Test
    void shouldRemoveAllTasks() {
        Task<UUID> task = createDefaultTask();

        var savedTask = tracker.save(task);
        List<Task<UUID>> tasksAfterSaving = historyManager.getViewHistory();

        historyManager.clearViewHistory();
        List<Task<UUID>> tasksAfterClearing = historyManager.getViewHistory();

        assertAll(
            () -> assertThat(tasksAfterSaving)
                .containsExactly(savedTask),

            () -> assertThat(tasksAfterClearing)
                .isEmpty()
        );
    }

    @Test
    void shouldRemoveTaskFromHistoryAfterDeleting() {
        Task<UUID> task = createDefaultTask();

        var savedTask = tracker.save(task);

        tracker.remove(savedTask.getId());

        List<Task<UUID>> tasks = historyManager.getViewHistory();

        assertThat(tasks)
            .isEmpty();
    }

    @Test
    void shouldAddTaskToHistoryAfterGettingById() {
        Task<UUID> task = createDefaultTask();

        var savedTask = tracker.save(task);

        historyManager.clearViewHistory();

        var taskById = tracker.getById(savedTask.getId());

        List<Task<UUID>> tasks = historyManager.getViewHistory();

        assertThat(tasks)
            .containsExactly(taskById);
    }

    @Test
    void shouldAddTaskToHistoryAfterGettingMostPriority() {
        Task<UUID> task1 = createDefaultTask(UUID.randomUUID());
        task1.setPriority(1);
        tracker.save(task1);

        Task<UUID> task2 = createDefaultTask(UUID.randomUUID());
        task2.setPriority(-1);
        tracker.save(task2);

        Task<UUID> task3 = createDefaultTask(UUID.randomUUID());
        task3.setPriority(10);
        tracker.save(task3);

        historyManager.clearViewHistory();

        Task<UUID> mostPriorityTask = tracker.getMostPriority();
        List<Task<UUID>> tasks = historyManager.getViewHistory();

        assertThat(tasks)
            .containsExactly(mostPriorityTask);
    }

    @Test
    void shouldAddTaskToHistoryAfterGettingLeastPriority() {
        Task<UUID> task1 = createDefaultTask(UUID.randomUUID());
        task1.setPriority(1);
        tracker.save(task1);

        Task<UUID> task2 = createDefaultTask(UUID.randomUUID());
        task2.setPriority(-1);
        tracker.save(task2);

        Task<UUID> task3 = createDefaultTask(UUID.randomUUID());
        task3.setPriority(10);
        tracker.save(task3);

        historyManager.clearViewHistory();

        Task<UUID> leastPriorityTask = tracker.getLeastPriority();
        List<Task<UUID>> tasks = historyManager.getViewHistory();

        assertThat(tasks)
            .containsExactly(leastPriorityTask);
    }

}
