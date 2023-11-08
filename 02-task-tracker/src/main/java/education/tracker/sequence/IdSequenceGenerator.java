package education.tracker.sequence;

import org.jetbrains.annotations.NotNull;

public interface IdSequenceGenerator<T> {

    @NotNull
    T next();

    default void setCurrentIfApplicable(@NotNull T current) {
        // do nothing
    }
}
