package education.tracker.sequence;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class UUIDSequenceGenerator implements IdSequenceGenerator<UUID> {

    @Override
    public @NotNull UUID next() {
        return UUID.randomUUID();
    }

}
