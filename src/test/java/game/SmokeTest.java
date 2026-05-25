package game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke test that exists purely to verify the Maven + JUnit 5 pipeline
 * is wired up correctly. Once real test classes are added by the team,
 * this can be removed.
 */
class SmokeTest {

    @Test
    void mavenJunitPipelineIsWired() {
        assertTrue(true, "If you can see this passing, mvn test works.");
    }
}
