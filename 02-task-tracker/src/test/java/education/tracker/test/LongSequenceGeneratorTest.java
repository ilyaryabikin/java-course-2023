package education.tracker.test;

import education.tracker.sequence.LongSequenceGenerator;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class LongSequenceGeneratorTest implements WithAssertions {

    @Test
    void shouldGenerateNextValue() {
        long initValue = 1L;
        var generator = new LongSequenceGenerator(initValue);

        Long result = generator.next();

        assertThat(result)
            .isEqualTo(initValue + 1);
    }

    @Test
    void shouldGenerateNextValueAfterSettingCurrent() {
        var generator = new LongSequenceGenerator();

        long currentValue = 731065716350L;

        generator.setCurrentIfApplicable(currentValue);

        Long result = generator.next();

        assertThat(result)
            .isEqualTo(currentValue + 1);
    }

    @Test
    void shouldNotChangeCurrentSequenceValueIfPassedCurrentValueIsLess() {
        long initValue = 15L;
        var generator = new LongSequenceGenerator(initValue);

        generator.setCurrentIfApplicable(4L);

        Long result = generator.next();

        assertThat(result)
            .isEqualTo(initValue + 1);
    }

}
