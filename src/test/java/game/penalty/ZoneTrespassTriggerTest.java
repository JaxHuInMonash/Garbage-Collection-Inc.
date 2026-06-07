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
 * Unit tests for {@link ZoneTrespassTrigger}.
 * Covers: worker increments level, non-worker ignored,
 * and repeated trespass caps at max level.
 */
class ZoneTrespassTriggerTest {

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
    @DisplayName("ContractedWorker steps on tile — infraction level increments to one")
    void checkAndApply_incrementsLevel_forContractedWorker() {
        ContractedWorker mockWorker = mock(ContractedWorker.class);
        when(mockMap.locationOf(mockWorker)).thenReturn(null);
        ZoneTrespassTrigger trigger = new ZoneTrespassTrigger("Server Room");
        try { trigger.checkAndApply(mockWorker, mockMap); } catch (Exception ignored) {}
        assertEquals(1, InfractionManager.getInstance().getLevel(mockWorker));
    }

    @Test
    @DisplayName("non-Worker steps on tile — infraction level stays zero")
    void checkAndApply_doesNothing_forNonWorkerActor() {
        Actor mockActor = mock(Actor.class);
        ZoneTrespassTrigger trigger = new ZoneTrespassTrigger("Server Room");
        trigger.checkAndApply(mockActor, mockMap);
        assertEquals(0, InfractionManager.getInstance().getLevel(mockActor));
    }

    @Test
    @DisplayName("Worker trespasses ten times — level caps at three")
    void checkAndApply_capsAtThree_forRepeatedTrespass() {
        ContractedWorker mockWorker = mock(ContractedWorker.class);
        when(mockMap.locationOf(mockWorker)).thenReturn(null);
        ZoneTrespassTrigger trigger = new ZoneTrespassTrigger("Toxic Lab");
        for (int i = 0; i < 10; i++) {
            try { trigger.checkAndApply(mockWorker, mockMap); } catch (Exception ignored) {}
        }
        assertEquals(3, InfractionManager.getInstance().getLevel(mockWorker));
    }
}
