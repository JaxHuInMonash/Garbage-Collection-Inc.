package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.ContractedWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FriendlyFireTrigger}.
 * Covers: worker-on-worker increments, non-worker attacker ignored,
 * and non-worker target ignored.
 */
class FriendlyFireTriggerTest {

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
        mockMap = mock(GameMap.class);
    }

    @Test
    @DisplayName("Worker attacks Worker — attacker's infraction level increments to one")
    void checkFriendlyFire_incrementsLevel_whenWorkerAttacksWorker() {
        ContractedWorker attacker = mock(ContractedWorker.class);
        ContractedWorker target = mock(ContractedWorker.class);
        when(mockMap.locationOf(attacker)).thenReturn(null);
        try { FriendlyFireTrigger.checkFriendlyFire(attacker, target, mockMap); } catch (Exception ignored) {}
        assertEquals(1, InfractionManager.getInstance().getLevel(attacker));
    }

    @Test
    @DisplayName("non-Worker attacks Worker — no infraction recorded")
    void checkFriendlyFire_doesNothing_whenNonWorkerAttacks() {
        Actor attacker = mock(Actor.class);
        ContractedWorker target = mock(ContractedWorker.class);
        FriendlyFireTrigger.checkFriendlyFire(attacker, target, mockMap);
        assertEquals(0, InfractionManager.getInstance().getLevel(attacker));
    }

    @Test
    @DisplayName("Worker attacks non-Worker — no infraction recorded")
    void checkFriendlyFire_doesNothing_whenTargetIsNotWorker() {
        ContractedWorker attacker = mock(ContractedWorker.class);
        Actor target = mock(Actor.class);
        FriendlyFireTrigger.checkFriendlyFire(attacker, target, mockMap);
        assertEquals(0, InfractionManager.getInstance().getLevel(attacker));
    }
}