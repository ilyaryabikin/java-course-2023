package education.tracker.test;

import static education.tracker.test.TestTaskUtil.createDefaultTask;
import static org.junit.jupiter.api.Assertions.assertAll;

import education.tracker.model.Task;
import education.tracker.model.TaskStatus;
import education.tracker.sequence.UUIDSequenceGenerator;
import education.tracker.tracker.IndexedInMemoryTaskTracker;
import education.tracker.tracker.TaskTracker;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class InMemoryUUIDTaskTrackerTest implements WithAssertions {

    private final TaskTracker<UUID> tracker = new IndexedInMemoryTaskTracker<>(
        new UUIDSequenceGenerator()
    );

    @Test
    void shouldAddTaskWithGivenId() {
        Task<UUID> task = createDefaultTask(UUID.randomUUID());

        Task<UUID> addedTask = tracker.save(task);

        assertThat(addedTask)
            .isEqualTo(task);
    }

    @Test
    void shouldAddTaskAndGenerateId() {
        Task<UUID> task = createDefaultTask();

        Task<UUID> addedTask = tracker.save(task);


        assertAll(
            () -> assertThat(addedTask)
                .extracting(Task::getId)
                .isNotNull(),

            () -> assertThat(addedTask)
                .extracting(
                    Task::getSummary,
                    Task::getPriority,
                    Task::getStatus,
                    Task::getCreatedAt,
                    Task::getDeadlineAt
                )
                .containsExactly(
                    task.getSummary(),
                    task.getPriority(),
                    task.getStatus(),
                    task.getCreatedAt(),
                    task.getDeadlineAt()
                )
        );
    }

    @Test
    void shouldOverrideExistingTask() {
        Task<UUID> task = createDefaultTask();

        Task<UUID> addedTask = tracker.save(task);

        addedTask.setSummary("Drink tea");
        addedTask.setDeadlineAt(addedTask.getCreatedAt().plusMinutes(5L));
        addedTask.setPriority(Integer.MAX_VALUE);

        Task<UUID> overridenTask = tracker.save(addedTask);
        Task<UUID> retrievedTask = tracker.getById(addedTask.getId());
        List<Task<UUID>> allTasks = tracker.getAll();

        assertAll(
            () -> assertThat(overridenTask)
                .isEqualTo(addedTask),

            () -> assertThat(retrievedTask)
                .isEqualTo(addedTask),

            () -> assertThat(allTasks)
                .containsExactly(addedTask)
        );
    }

    @Test
    void shouldGetNullTaskByIdWhenTaskDoesNotExist() {
        Task<UUID> retrievedTask = tracker.getById(UUID.randomUUID());

        assertThat(retrievedTask)
            .isNull();
    }

    @Test
    void shouldGetPreviouslyAddedTaskById() {
        Task<UUID> addedTask = tracker.save(createDefaultTask());

        Task<UUID> retrievedTask = tracker.getById(addedTask.getId());

        assertThat(retrievedTask)
            .isEqualTo(addedTask);
    }

    @Test
    void shouldGetEmptyTasksWhenTasksDoNotExist() {
        List<Task<UUID>> tasks = tracker.getAll();

        assertThat(tasks)
            .isEmpty();
    }

    @Test
    void shouldGetPreviouslyAddedTasks() {
        List<Task<UUID>> tasksToAdd = List.of(
            createDefaultTask(UUID.randomUUID()),
            createDefaultTask(UUID.randomUUID()),
            createDefaultTask(UUID.randomUUID())
        );

        tasksToAdd.forEach(tracker::save);

        List<Task<UUID>> retrievedTasks = tracker.getAll();

        assertThat(retrievedTasks)
            .containsExactlyInAnyOrderElementsOf(tasksToAdd);
    }

    @Test
    void shouldNotRemoveTaskIfIdDoesNotExist() {
        Task<UUID> removedTask = tracker.remove(UUID.randomUUID());

        assertThat(removedTask)
            .isNull();
    }

    @Test
    void shouldRemovePreviouslyAddedTask() {
        Task<UUID> addedTask = tracker.save(createDefaultTask());

        Task<UUID> removedTask = tracker.remove(addedTask.getId());

        assertThat(removedTask)
            .isEqualTo(addedTask);
    }

    @Test
    void shouldGetEmptyTasksOrderedByPriorityWhenTasksDoNotExist() {
        List<Task<UUID>> tasks = tracker.getOrderedByPriority();

        assertThat(tasks)
            .isEmpty();
    }

    @Test
    void shouldGetTasksOrderedByPriority() {
        Task<UUID> task1 = createDefaultTask(UUID.randomUUID());
        task1.setPriority(1);

        tracker.save(task1);

        Task<UUID> task2 = createDefaultTask(UUID.randomUUID());
        task2.setPriority(-1);

        tracker.save(task2);

        Task<UUID> task3 = createDefaultTask(UUID.randomUUID());
        task3.setPriority(10);

        tracker.save(task3);

        List<Task<UUID>> tasksByPriority = tracker.getOrderedByPriority();

        assertThat(tasksByPriority)
            .containsExactly(task3, task1, task2);
    }

    @Test
    void shouldGetNullMostPriorityWhenTasksDoNotExist() {
        Task<UUID> mostPriortyTask = tracker.getMostPriority();

        assertThat(mostPriortyTask)
            .isNull();
    }

    @Test
    void shouldGetMostPriority() {
        Task<UUID> task1 = createDefaultTask(UUID.randomUUID());
        task1.setPriority(1);

        tracker.save(task1);

        Task<UUID> task2 = createDefaultTask(UUID.randomUUID());
        task2.setPriority(-1);

        tracker.save(task2);

        Task<UUID> task3 = createDefaultTask(UUID.randomUUID());
        task3.setPriority(10);

        tracker.save(task3);

        Task<UUID> mostPriorityTask = tracker.getMostPriority();

        assertThat(mostPriorityTask)
            .isEqualTo(task3);
    }

    @Test
    void shouldGetNullLeastPriorityWhenTasksDoNotExist() {
        Task<UUID> leastPriortyTask = tracker.getLeastPriority();

        assertThat(leastPriortyTask)
            .isNull();
    }

    @Test
    void shouldGetLeastPriority() {
        Task<UUID> task1 = createDefaultTask(UUID.randomUUID());
        task1.setPriority(1);

        tracker.save(task1);

        Task<UUID> task2 = createDefaultTask(UUID.randomUUID());
        task2.setPriority(-1);

        tracker.save(task2);

        Task<UUID> task3 = createDefaultTask(UUID.randomUUID());
        task3.setPriority(10);

        tracker.save(task3);

        Task<UUID> leastPriorityTask = tracker.getLeastPriority();

        assertThat(leastPriorityTask)
            .isEqualTo(task2);
    }

    @ParameterizedTest
    @EnumSource(TaskStatus.class)
    void shouldGetEmptyTasksOrderedByPriorityWithStatusWhenTasksDoNotExist(TaskStatus status) {
        List<Task<UUID>> tasks = tracker.getOrderedByPriorityWithStatus(status);

        assertThat(tasks)
            .isEmpty();
    }

    @Test
    void shouldGetTasksOrderedByPriorityWithStatus() {
        Task<UUID> task1 = createDefaultTask(UUID.randomUUID());
        task1.setPriority(1);
        task1.setStatus(TaskStatus.TODO);

        tracker.save(task1);

        Task<UUID> task2 = createDefaultTask(UUID.randomUUID());
        task2.setPriority(-1);
        task2.setStatus(TaskStatus.DONE);

        tracker.save(task2);

        Task<UUID> task3 = createDefaultTask(UUID.randomUUID());
        task3.setPriority(10);
        task3.setStatus(TaskStatus.TODO);

        tracker.save(task3);

        List<Task<UUID>> priorityTodoTasks = tracker.getOrderedByPriorityWithStatus(TaskStatus.TODO);

        assertThat(priorityTodoTasks)
            .containsExactly(task3, task1);
    }

    @Test
    void shouldGetEmptyTasksOrderedByPriorityGroupedByStatusWhenTasksDoNotExist() {
        Map<TaskStatus, List<Task<UUID>>> priorityTodoTasks = tracker.getOrderedByPriorityGroupedByStatus();

        assertThat(priorityTodoTasks)
            .isEmpty();
    }

    @Test
    void shouldGetTasksOrderedByPriorityGroupedStatus() {
        Task<UUID> task1 = createDefaultTask(UUID.randomUUID());
        task1.setPriority(1);
        task1.setStatus(TaskStatus.TODO);

        tracker.save(task1);

        Task<UUID> task2 = createDefaultTask(UUID.randomUUID());
        task2.setPriority(-1);
        task2.setStatus(TaskStatus.DONE);

        tracker.save(task2);

        Task<UUID> task3 = createDefaultTask(UUID.randomUUID());
        task3.setPriority(10);
        task3.setStatus(TaskStatus.TODO);

        tracker.save(task3);

        Map<TaskStatus, List<Task<UUID>>> priorityTodoTasks = tracker.getOrderedByPriorityGroupedByStatus();

        assertThat(priorityTodoTasks)
            .containsExactlyInAnyOrderEntriesOf(
                Map.of(
                    TaskStatus.DONE, List.of(task2),
                    TaskStatus.TODO, List.of(task3, task1)
                )
            );
    }
}
