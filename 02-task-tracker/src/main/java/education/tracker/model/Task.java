package education.tracker.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task<T> {

    private T id;
    private String summary;
    private int priority;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime deadlineAt;

    public Task(
        T id,
        String summary,
        int priority,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime deadlineAt
    ) {
        this.id = id;
        this.summary = summary;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.deadlineAt = deadlineAt;
    }

    public Task(
        String summary,
        int priority,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime deadlineAt
    ) {
        this(null, summary, priority, status, createdAt, deadlineAt);
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeadlineAt() {
        return deadlineAt;
    }

    public void setDeadlineAt(LocalDateTime deadlineAt) {
        this.deadlineAt = deadlineAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task<?> task = (Task<?>) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    @SuppressWarnings("OperatorWrap")
    public String toString() {
        return "Task{" +
            "id=" + id +
            ", summary='" + summary + '\'' +
            ", priority=" + priority +
            ", status=" + status +
            ", createdAt=" + createdAt +
            ", deadlineAt=" + deadlineAt +
            '}';
    }
}
