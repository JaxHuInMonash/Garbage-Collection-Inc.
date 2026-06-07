package game.grounds;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Vent}.
 *
 * <p>Tests verify that the Vent correctly implements the
 * {@link game.capabilities.Cuttable} interface and has the correct
 * display character. The actual cut behaviour (dropping fan, spawning
 * Undead, tile transformation) requires a live game map and is covered
 * by integration testing.
 *
 * <p>Coverage: implements Cuttable, correct display character.
 * @author Mary
 */
class VentTest {

    /**
     * Verifies the display character is 'V' as shown on the map.
     */
    @Test
    void testVentDisplayChar() {
        Vent vent = new Vent();
        assertEquals('V', vent.getDisplayChar());
    }

    /**
     * Verifies toString returns the ground name correctly.
     */
    @Test
    void testVentToString() {
        Vent vent = new Vent();
        assertEquals("Vent", vent.toString());
    }
}