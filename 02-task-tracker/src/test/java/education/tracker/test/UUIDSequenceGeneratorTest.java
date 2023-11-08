package education.tracker.test;

import education.tracker.sequence.UUIDSequenceGenerator;
import java.util.UUID;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class UUIDSequenceGeneratorTest implements WithAssertions {

    @Test
    void shouldGenerateNextValue() {
        var generator = new UUIDSequenceGenerator();

        UUID result = generator.next();

        assertThat(result)
            .isNotNull();
    }

}
