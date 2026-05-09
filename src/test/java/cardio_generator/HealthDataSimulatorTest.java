package cardio_generator;

import com.cardio_generator.HealthDataSimulator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HealthDataSimulatorTest {

    @Test
    void testGetInstanceReturnsSameInstance() {
        HealthDataSimulator instance1 = HealthDataSimulator.getInstance();
        HealthDataSimulator instance2 = HealthDataSimulator.getInstance();
        assertSame(instance1, instance2);
    }
}
