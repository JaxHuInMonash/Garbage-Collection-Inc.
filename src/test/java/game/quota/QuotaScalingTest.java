package game.quota;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QuotaScaling}.
 *
 * <p>{@link QuotaScaling} is a pure utility class with no state, so no
 * setup or teardown is required. Each test passes inputs directly and
 * checks the returned value.
 *
 * <p>Coverage: typical scaling inputs, decimal ceiling-rounding, and
 * edge cases (zero value, zero rate).
 */
class QuotaScalingTest {

    // ── Typical inputs ───────────────────────────────────────────────────────

    /**
     * Verifies the quota target scales up by 5% correctly.
     * 100 * 1.05 = 105.0 → 105 (no rounding needed).
     */
    @Test
    void testQuotaScalesUpByFivePercent() {
        assertEquals(105, QuotaScaling.scaleUp(100, 0.05));
    }

    /**
     * Verifies the turn limit scales up by 10% correctly.
     * 200 * 1.10 = 220.00000000000003 due to floating point → ceiling = 221.
     */
    @Test
    void testTurnLimitScalesUpByTenPercent() {
        assertEquals(221, QuotaScaling.scaleUp(200, 0.10));
    }

    // ── Decimal ceiling rounding ─────────────────────────────────────────────

    /**
     * Verifies that a decimal result is rounded up to the nearest integer.
     * 101 * 1.05 = 106.05 → ceiling = 107.
     */
    @Test
    void testDecimalResultRoundsUp() {
        assertEquals(107, QuotaScaling.scaleUp(101, 0.05));
    }

    /**
     * Verifies ceiling rounding with the 10% rate.
     * 202 * 1.10 = 222.2 → ceiling = 223.
     */
    @Test
    void testAnotherDecimalRoundsUp() {
        assertEquals(223, QuotaScaling.scaleUp(202, 0.10));
    }

    // ── Edge cases ───────────────────────────────────────────────────────────

    /**
     * Verifies that scaling zero always returns zero regardless of rate.
     * Edge case: zero input should produce zero output.
     */
    @Test
    void testScaleUpWithZeroValueStaysZero() {
        assertEquals(0, QuotaScaling.scaleUp(0, 0.05));
    }

    /**
     * Verifies that a zero growth rate leaves the value unchanged.
     * Edge case: 100 * 1.0 = 100, no change expected.
     */
    @Test
    void testScaleUpWithZeroRateUnchanged() {
        assertEquals(100, QuotaScaling.scaleUp(100, 0.0));
    }
}