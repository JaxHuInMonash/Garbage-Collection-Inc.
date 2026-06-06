package game.statuses;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LockedOutStatus}.
 * Covers: active when turns remain, inactive at zero, tick decrements,
 * toString contains turn count, and exact boundary of one turn.
 */
class LockedOutStatusTest {

    @Test
    @DisplayName("five turns — status is active")
    void isStatusActive_returnsTrue_whenTurnsRemaining() {
        LockedOutStatus status = new LockedOutStatus(5);
        assertTrue(status.isStatusActive());
    }

    @Test
    @DisplayName("zero turns — status is inactive immediately")
    void isStatusActive_returnsFalse_whenZeroTurns() {
        LockedOutStatus status = new LockedOutStatus(0);
        assertFalse(status.isStatusActive());
    }

    @Test
    @DisplayName("tick three times on three-turn status — becomes inactive")
    void tickStatus_exhaustsAllTurns_becomesInactive() {
        LockedOutStatus status = new LockedOutStatus(3);
        status.tickStatus(null, null);
        status.tickStatus(null, null);
        status.tickStatus(null, null);
        assertFalse(status.isStatusActive());
    }

    @Test
    @DisplayName("toString — contains remaining turn count")
    void toString_containsRemainingTurnCount() {
        LockedOutStatus status = new LockedOutStatus(5);
        assertTrue(status.toString().contains("5"));
    }

    @Test
    @DisplayName("boundary — one turn, one tick: status expires exactly")
    void isStatusActive_returnsFalse_afterSingleTick_onOneTurnStatus() {
        LockedOutStatus status = new LockedOutStatus(1);
        status.tickStatus(null, null);
        assertFalse(status.isStatusActive());
    }
}