package game.statuses;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PoisonStatus}.
 *
 * <p>Tests focus on the status lifecycle: active/inactive state based on
 * duration, and the string representation. {@code tickStatus} is not called
 * directly since it requires a live engine entity — instead, duration is
 * controlled via the constructor to test expiry logic cleanly.
 *
 * <p>Coverage: active on creation, expired at zero duration, still active
 * before expiry, and toString format.
 * @author Mary
 */
class PoisonStatusTest {

    // ── Active state ─────────────────────────────────────────────────────────

    /**
     * Verifies that a freshly created PoisonStatus with positive duration
     * is active. Typical case.
     */
    @Test
    void testPoisonIsActiveInitially() {
        PoisonStatus poison = new PoisonStatus(1, 5);
        assertTrue(poison.isStatusActive());
    }

    /**
     * Verifies that PoisonStatus with a duration of 3 is still active.
     * Typical case: status created mid-duration.
     */
    @Test
    void testPoisonStillActiveBeforeDurationEnds() {
        PoisonStatus poison = new PoisonStatus(1, 3);
        assertTrue(poison.isStatusActive());
    }

    // ── Expired state ────────────────────────────────────────────────────────

    /**
     * Verifies that PoisonStatus created with zero duration is immediately
     * inactive. Edge case: duration of exactly zero.
     */
    @Test
    void testPoisonExpiresAtZeroDuration() {
        PoisonStatus poison = new PoisonStatus(1, 0);
        assertFalse(poison.isStatusActive());
    }

    /**
     * Verifies that PoisonStatus created with a negative duration is inactive.
     * Invalid input: negative duration should not be considered active.
     */
    @Test
    void testPoisonInactiveWithNegativeDuration() {
        PoisonStatus poison = new PoisonStatus(1, -1);
        assertFalse(poison.isStatusActive());
    }

    // ── toString ─────────────────────────────────────────────────────────────

    /**
     * Verifies that toString contains the word "Poison" so the player
     * can identify the status in the HUD.
     */
    @Test
    void testToStringContainsPoisonLabel() {
        PoisonStatus poison = new PoisonStatus(1, 5);
        assertTrue(poison.toString().contains("Poison"));
    }

    /**
     * Verifies that toString contains the remaining turn count so the
     * player can see how long the effect lasts.
     */
    @Test
    void testToStringContainsRemainingTurns() {
        PoisonStatus poison = new PoisonStatus(1, 5);
        assertTrue(poison.toString().contains("5"));
    }
}