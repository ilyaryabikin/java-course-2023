package education.tracker.test;

import education.tracker.model.Task;
import education.tracker.model.TaskStatus;
import java.time.LocalDateTime;

final class TestTaskUtil {

    public static <T> Task<T> createDefaultTask() {
        return new Task<T>(
            "Drink coffee",
            1,
            TaskStatus.TODO,
            LocalDateTime.now(),
            null
        );
    }

    public static <T> Task<T> createDefaultTask(T id) {
        return new Task<T>(
            id,
            "Drink coffee",
            1,
            TaskStatus.TODO,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2)
        );
    }

}
