package game.items;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PlasmaCutter}.
 *
 * <p>Tests focus on properties that can be verified without a live game
 * engine: weight, buy value, and item identity. Behaviour that requires
 * a map or actor (burn on purchase, cutting ground tiles) is covered by
 * integration testing via the test map setup in {@code EclipseNebula}.
 *
 * <p>Coverage: correct weight statistic, correct buy value, correct
 * display character, and that the item is not Sellable.
 * @author Mary
 */
class PlasmaCutterTest {

    /**
     * Verifies the Plasma Cutter has the correct weight of 7
     * as specified in REQ1.
     */
    @Test
    void testPlasmaCutterHasCorrectWeight() {
        PlasmaCutter cutter = new PlasmaCutter();
        assertEquals(7, cutter.getStatistic(game.ItemStatistics.WEIGHT));
    }

    /**
     * Verifies the Plasma Cutter costs 50 worker credits to buy
     * as specified in REQ1.
     */
    @Test
    void testPlasmaCutterHasCorrectBuyValue() {
        PlasmaCutter cutter = new PlasmaCutter();
        assertEquals(50, cutter.getBuyValue());
    }

    /**
     * Verifies the Plasma Cutter displays as '>' on the map.
     */
    @Test
    void testPlasmaCutterHasCorrectDisplayChar() {
        PlasmaCutter cutter = new PlasmaCutter();
        assertEquals('>', cutter.getDisplayChar());
    }

    /**
     * Verifies the Plasma Cutter has no sell value statistic,
     * confirming it cannot be sold back to the Supercomputer as per REQ1.
     */
    @Test
    void testPlasmaCutterHasNoSellValue() {
        PlasmaCutter cutter = new PlasmaCutter();
        assertFalse(cutter.hasStatistic(game.ItemStatistics.SELL_VALUE));
    }

    /**
     * Verifies toString returns the correct item name.
     */
    @Test
    void testPlasmaCutterToString() {
        PlasmaCutter cutter = new PlasmaCutter();
        assertEquals("Plasma Cutter", cutter.toString());
    }
}