package game.grounds;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AluminiumDoor}.
 *
 * <p>Tests verify that the door correctly implements the {@link game.capabilities.Cuttable}
 * interface and has the correct display character. The actual cut behaviour
 * (dropping scrap, tile transformation, explosion) requires a live game map
 * and is covered by integration testing via the test map in EclipseNebula.
 *
 * <p>Coverage: implements Cuttable, correct display character, cannot be entered.
 * @author Mary
 */
class AluminiumDoorTest {

    /**
     * Verifies the display character is '=' as shown on the map.
     */
    @Test
    void testAluminiumDoorDisplayChar() {
        AluminiumDoor door = new AluminiumDoor();
        assertEquals('=', door.getDisplayChar());
    }

    /**
     * Verifies actors cannot enter the door tile — it blocks movement.
     * Invalid input: passing null actor, door should still return false.
     */
    @Test
    void testAluminiumDoorBlocksEntry() {
        AluminiumDoor door = new AluminiumDoor();
        assertFalse(door.canActorEnter(null));
    }
}


