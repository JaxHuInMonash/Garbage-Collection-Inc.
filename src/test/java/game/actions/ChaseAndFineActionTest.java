package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.penalty.InfractionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ChaseAndFineAction.DespawnAction}.
 * Covers: bot removal, target clearing, and menu description.
 */
class ChaseAndFineActionTest {

    private Actor mockBot;
    private Actor mockTarget;
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
        mockBot = mock(Actor.class);
        mockTarget = mock(Actor.class);
        mockMap = mock(GameMap.class);
    }

    @Test
    @DisplayName("despawn — bot is removed from the map")
    void despawnAction_removesBot_fromMap() {
        ChaseAndFineAction.DespawnAction despawn = new ChaseAndFineAction.DespawnAction(mockBot, mockTarget);
        despawn.execute(mockBot, mockMap);
        verify(mockMap).removeActor(mockBot);
    }

    @Test
    @DisplayName("despawn — target is cleared from active targets")
    void despawnAction_clearsTarget_inInfractionManager() {
        ChaseAndFineAction.DespawnAction despawn = new ChaseAndFineAction.DespawnAction(mockBot, mockTarget);
        despawn.execute(mockBot, mockMap);
        assertEquals(0, InfractionManager.getInstance().getLevel(mockTarget));
    }

    @Test
    @DisplayName("despawn — menu description is not null or empty")
    void despawnAction_menuDescription_isNotEmpty() {
        ChaseAndFineAction.DespawnAction despawn = new ChaseAndFineAction.DespawnAction(mockBot, mockTarget);
        String desc = despawn.menuDescription(mockBot);
        assertNotNull(desc);
        assertFalse(desc.isEmpty());
    }
}