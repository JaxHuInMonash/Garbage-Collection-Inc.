package game.statuses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PhaseStabilizerStatus} — Surveyor T3's regenerating
 * charge marker. Pure state machine; no engine objects needed.
 */
class PhaseStabilizerStatusTest {

    @Test
    void newStatusHasChargeReady() {
        PhaseStabilizerStatus status = new PhaseStabilizerStatus();
        assertTrue(status.hasCharge());
        assertEquals(0, status.getTurnsUntilRecharge());
    }

    @Test
    void consumingChargeDisarmsAndStartsRecharge() {
        PhaseStabilizerStatus status = new PhaseStabilizerStatus();
        status.consumeCharge();

        assertFalse(status.hasCharge());
        assertEquals(PhaseStabilizerStatus.RECHARGE_TURNS, status.getTurnsUntilRecharge());
    }

    @Test
    void tickWhileChargedIsANoOp() {
        PhaseStabilizerStatus status = new PhaseStabilizerStatus();
        // No charge consumed — ticking shouldn't change anything.
        status.tickStatus(null, null);
        assertTrue(status.hasCharge());
        assertEquals(0, status.getTurnsUntilRecharge());
    }

    @Test
    void chargeReturnsAfterExactlyRechargeTurnsTicks() {
        PhaseStabilizerStatus status = new PhaseStabilizerStatus();
        status.consumeCharge();

        for (int i = 0; i < PhaseStabilizerStatus.RECHARGE_TURNS - 1; i++) {
            status.tickStatus(null, null);
            assertFalse(status.hasCharge(), "still recharging at tick " + (i + 1));
        }
        // The Nth tick should restore the charge.
        status.tickStatus(null, null);
        assertTrue(status.hasCharge());
    }

    @Test
    void statusIsAlwaysActive() {
        PhaseStabilizerStatus status = new PhaseStabilizerStatus();
        assertTrue(status.isStatusActive());
        status.consumeCharge();
        assertTrue(status.isStatusActive(), "still active while recharging");
    }
}
