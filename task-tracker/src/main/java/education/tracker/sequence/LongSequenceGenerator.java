package education.tracker.sequence;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class LongSequenceGenerator implements IdSequenceGenerator<Long> {

    private Long currentId;

    public LongSequenceGenerator(Long currentId) {
        this.currentId = currentId;
    }

    public LongSequenceGenerator() {
        this(0L);
    }

    @Override
    public @NotNull Long next() {
        return ++currentId;
    }

    @Override
    public void setCurrentIfApplicable(@NotNull Long current) {
        Objects.requireNonNull(current);

        if (current <= currentId) {
            return;
        }

        currentId = current;
    }

}
