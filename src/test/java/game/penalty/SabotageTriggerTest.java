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
 * Unit tests for {@link SabotageTrigger}.
 * Covers: worker increments level, non-worker ignored,
 * and repeated sabotage caps at max level.
 */
class SabotageTriggerTest {

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
    @DisplayName("Worker uses AlienCube — infraction level increments to one")
    void checkAndApply_incrementsLevel_forContractedWorker() {
        ContractedWorker mockWorker = mock(ContractedWorker.class);
        when(mockMap.locationOf(mockWorker)).thenReturn(null);
        SabotageTrigger trigger = new SabotageTrigger();
        try { trigger.checkAndApply(mockWorker, mockMap); } catch (Exception ignored) {}
        assertEquals(1, InfractionManager.getInstance().getLevel(mockWorker));
    }

    @Test
    @DisplayName("non-Worker uses AlienCube — no infraction recorded")
    void checkAndApply_doesNothing_forNonWorkerActor() {
        Actor mockActor = mock(Actor.class);
        SabotageTrigger trigger = new SabotageTrigger();
        trigger.checkAndApply(mockActor, mockMap);
        assertEquals(0, InfractionManager.getInstance().getLevel(mockActor));
    }

    @Test
    @DisplayName("Worker sabotages ten times — level caps at three")
    void checkAndApply_capsAtThree_afterRepeatedSabotage() {
        ContractedWorker mockWorker = mock(ContractedWorker.class);
        when(mockMap.locationOf(mockWorker)).thenReturn(null);
        SabotageTrigger trigger = new SabotageTrigger();
        for (int i = 0; i < 10; i++) {
            try { trigger.checkAndApply(mockWorker, mockMap); } catch (Exception ignored) {}
        }
        assertEquals(3, InfractionManager.getInstance().getLevel(mockWorker));
    }
}