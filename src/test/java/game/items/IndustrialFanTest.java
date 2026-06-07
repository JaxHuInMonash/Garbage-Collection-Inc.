package game.items;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link IndustrialFan}.
 *
 * <p>Coverage: correct weight, correct sell value, correct deposit value,
 * implements both Sellable and Depositable.
 * @author Mary
 */
class IndustrialFanTest {

    /**
     * Verifies Industrial Fan has correct weight of 5 as per REQ1.
     */
    @Test
    void testIndustrialFanHasCorrectWeight() {
        IndustrialFan fan = new IndustrialFan();
        assertEquals(5, fan.getStatistic(game.ItemStatistics.WEIGHT));
    }

    /**
     * Verifies Industrial Fan sells for 150 worker credits as per REQ1.
     */
    @Test
    void testIndustrialFanHasCorrectSellValue() {
        IndustrialFan fan = new IndustrialFan();
        assertEquals(150, fan.getSellValue());
    }

    /**
     * Verifies Industrial Fan deposits for 10 company credits as per REQ1.
     */
    @Test
    void testIndustrialFanHasCorrectDepositValue() {
        IndustrialFan fan = new IndustrialFan();
        assertEquals(10, fan.getDepositValue());
    }

    /**
     * Verifies Industrial Fan implements Depositable.
     */
    @Test
    void testIndustrialFanHasDepositValue() {
        IndustrialFan fan = new IndustrialFan();
        assertEquals(10, fan.getDepositValue());
    }

    /**
     * Verifies display character is '@' as per REQ1.
     */
    @Test
    void testIndustrialFanDisplayChar() {
        IndustrialFan fan = new IndustrialFan();
        assertEquals('@', fan.getDisplayChar());
    }
}
