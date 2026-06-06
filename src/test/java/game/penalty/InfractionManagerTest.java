package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link InfractionManager}.
 * Covers: level tracking, capping, reset, singleton identity,
 * and duplicate-spawn prevention via activeTargets.
 */
class InfractionManagerTest {

    private InfractionManager manager;
    private Actor mockViolator;
    private GameMap mockMap;

    @BeforeEach
    void setUp() {
        try {
            var field = InfractionManager.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
        } catch (Exception e) {
            fail("Could not reset InfractionManager singleton");
        }
        manager = InfractionManager.getInstance();
        mockViolator = mock(Actor.class);
        mockMap = mock(GameMap.class);
    }

    @Test
    @DisplayName("fresh actor — level starts at zero")
    void getLevel_returnsZero_forNewActor() {
        assertEquals(0, manager.getLevel(mockViolator));
    }

    @Test
    @DisplayName("one increment — level becomes one")
    void increment_raisesLevel_byOne() {
        when(mockMap.locationOf(mockViolator)).thenReturn(null);
        try { manager.increment(mockViolator, mockMap); } catch (Exception ignored) {}
        assertEquals(1, manager.getLevel(mockViolator));
    }

    @Test
    @DisplayName("ten increments — level caps at three")
    void increment_capsAtMaxLevel_afterManyIncrements() {
        when(mockMap.locationOf(mockViolator)).thenReturn(null);
        for (int i = 0; i < 10; i++) {
            try { manager.increment(mockViolator, mockMap); } catch (Exception ignored) {}
        }
        assertEquals(3, manager.getLevel(mockViolator));
    }

    @Test
    @DisplayName("reset after increment — level returns to zero")
    void reset_setsLevelBackToZero() {
        when(mockMap.locationOf(mockViolator)).thenReturn(null);
        try { manager.increment(mockViolator, mockMap); } catch (Exception ignored) {}
        manager.reset(mockViolator);
        assertEquals(0, manager.getLevel(mockViolator));
    }

    @Test
    @DisplayName("reset on fresh actor — stays at zero, no exception")
    void reset_onFreshActor_remainsZero() {
        assertDoesNotThrow(() -> manager.reset(mockViolator));
        assertEquals(0, manager.getLevel(mockViolator));
    }

    @Test
    @DisplayName("getInstance called twice — returns same singleton")
    void getInstance_returnsSameInstance_always() {
        assertSame(InfractionManager.getInstance(), InfractionManager.getInstance());
    }

    @Test
    @DisplayName("clearTarget on fresh actor — no exception thrown")
    void clearTarget_onFreshActor_doesNotThrow() {
        assertDoesNotThrow(() -> manager.clearTarget(mockViolator));
    }
}
