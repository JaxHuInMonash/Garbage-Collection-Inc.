package game.items;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AluminiumScrap}.
 *
 * <p>Tests verify item properties and capability interfaces without
 * requiring a live game engine. Deposit side effects (20% cut hazard)
 * are non-deterministic and covered by integration testing.
 *
 * <p>Coverage: correct weight, correct deposit value, not sellable,
 * is depositable, display character.
 * @author Mary
 */
class AluminiumScrapTest {

    /**
     * Verifies Aluminium Scrap has correct weight of 2 as per REQ1.
     */
    @Test
    void testAluminiumScrapHasCorrectWeight() {
        AluminiumScrap scrap = new AluminiumScrap();
        assertEquals(2, scrap.getStatistic(game.ItemStatistics.WEIGHT));
    }

    /**
     * Verifies Aluminium Scrap deposits for 50 company credits as per REQ1.
     */
    @Test
    void testAluminiumScrapHasDepositValue() {
        AluminiumScrap scrap = new AluminiumScrap();
        assertEquals(50, scrap.getDepositValue());
    }
    /**
     * Verifies Aluminium Scrap has a deposit value of 50 company credits,
     * confirming it is depositable as per REQ1.
     */
    @Test
    void testAluminiumScrapHasCorrectDepositValue() {
        AluminiumScrap scrap = new AluminiumScrap();
        assertEquals(50, scrap.getDepositValue());
    }

    /**
     * Verifies Aluminium Scrap has no sell value statistic,
     * confirming it cannot be sold back to the Supercomputer as per REQ1.
     */
    @Test
    void testAluminiumScrapHasNoSellValue() {
        AluminiumScrap scrap = new AluminiumScrap();
        assertFalse(scrap.hasStatistic(game.ItemStatistics.SELL_VALUE));
    }

    /**
     * Verifies display character is '%' as per REQ1.
     */
    @Test
    void testAluminiumScrapDisplayChar() {
        AluminiumScrap scrap = new AluminiumScrap();
        assertEquals('%', scrap.getDisplayChar());
    }
}
