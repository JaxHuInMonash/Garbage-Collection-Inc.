package game.quota;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QuotaManager}.
 *
 * <p>Each test resets the singleton via {@link QuotaManager#resetInstance()}
 * in {@link #setUp()} so tests are fully isolated and deterministic.
 *
 * <p>Coverage: initial state, credit accumulation, rank-up scaling,
 * turn-counter behaviour, listener notification, and invalid inputs.
 */
class QuotaManagerTest {

    /**
     * Resets the QuotaManager singleton before every test to ensure
     * no state leaks between test cases.
     */
    @BeforeEach
    void setUp() {
        QuotaManager.resetInstance();
    }

    // ── Initial state ────────────────────────────────────────────────────────

    /**
     * Verifies the company starts at Rank 1.
     */
    @Test
    void testInitialRankIsOne() {
        assertEquals(1, QuotaManager.getInstance().getCompanyRank());
    }

    /**
     * Verifies the initial quota target is 100 company credits.
     */
    @Test
    void testInitialQuotaTargetIsOneHundred() {
        assertEquals(100, QuotaManager.getInstance().getQuotaTarget());
    }

    /**
     * Verifies the initial turn limit is 200 turns.
     */
    @Test
    void testInitialTurnLimitIsTwoHundred() {
        assertEquals(200, QuotaManager.getInstance().getTurnLimit());
    }

    /**
     * Verifies company credits start at zero.
     */
    @Test
    void testInitialCompanyCreditsAreZero() {
        assertEquals(0, QuotaManager.getInstance().getCompanyCredits());
    }

    // ── Credit accumulation ──────────────────────────────────────────────────

    /**
     * Verifies that adding a positive credit amount increases the total correctly.
     */
    @Test
    void testAddingCreditsIncreasesTotal() {
        QuotaManager.getInstance().addCompanyCredits(50);
        assertEquals(50, QuotaManager.getInstance().getCompanyCredits());
    }

    /**
     * Verifies that adding zero credits does not change the total.
     * Edge case: zero is a valid but no-op input.
     */
    @Test
    void testAddingZeroCreditsDoesNothing() {
        QuotaManager.getInstance().addCompanyCredits(0);
        assertEquals(0, QuotaManager.getInstance().getCompanyCredits());
    }

    /**
     * Verifies that adding negative credits does not change the total.
     * Negative input is invalid and should be ignored.
     */
    @Test
    void testAddingNegativeCreditsDoesNothing() {
        QuotaManager.getInstance().addCompanyCredits(-50);
        assertEquals(0, QuotaManager.getInstance().getCompanyCredits());
    }

    // ── Rank-up behaviour ────────────────────────────────────────────────────

    /**
     * Verifies that meeting the quota advances the company rank by one.
     */
    @Test
    void testMeetingQuotaAdvancesRank() {
        QuotaManager.getInstance().addCompanyCredits(100);
        assertEquals(2, QuotaManager.getInstance().getCompanyRank());
    }

    /**
     * Verifies that company credits reset to zero after a rank-up.
     */
    @Test
    void testCreditsResetToZeroAfterRankUp() {
        QuotaManager.getInstance().addCompanyCredits(100);
        assertEquals(0, QuotaManager.getInstance().getCompanyCredits());
    }

    /**
     * Verifies the quota target is scaled up by 5% (ceiling-rounded) after rank-up.
     * 100 * 1.05 = 105.0 → 105.
     */
    @Test
    void testQuotaTargetScaledUpAfterRankUp() {
        QuotaManager.getInstance().addCompanyCredits(100);
        assertEquals(105, QuotaManager.getInstance().getQuotaTarget());
    }

    /**
     * Verifies the turn limit is scaled up by 10% (ceiling-rounded) after rank-up.
     * 200 * 1.10 = 220.00000000000003 due to floating point → ceiling = 221.
     */
    @Test
    void testTurnLimitScaledUpAfterRankUp() {
        QuotaManager.getInstance().addCompanyCredits(100);
        assertEquals(221, QuotaManager.getInstance().getTurnLimit());
    }

    /**
     * Verifies that multiple consecutive rank-ups work correctly.
     * Rank 1 → deposit 100 → Rank 2 (target 105) → deposit 105 → Rank 3.
     */
    @Test
    void testMultipleRankUps() {
        QuotaManager.getInstance().addCompanyCredits(100);
        QuotaManager.getInstance().addCompanyCredits(105);
        assertEquals(3, QuotaManager.getInstance().getCompanyRank());
    }

    /**
     * Verifies the turn counter resets to zero after a rank-up.
     */
    @Test
    void testTurnCounterResetsAfterRankUp() {
        for (int i = 0; i < 50; i++) {
            QuotaManager.getInstance().tick();
        }
        QuotaManager.getInstance().addCompanyCredits(100);
        assertEquals(0, QuotaManager.getInstance().getCurrentTurn());
    }

    // ── Turn counter ─────────────────────────────────────────────────────────

    /**
     * Verifies the turn counter increments by one on each tick.
     */
    @Test
    void testTurnCounterIncrementsOnTick() {
        QuotaManager.getInstance().tick();
        assertEquals(1, QuotaManager.getInstance().getCurrentTurn());
    }

    // ── Listener notification ────────────────────────────────────────────────

    /**
     * Verifies that {@link QuotaListener#onQuotaMet} is called when the
     * quota is met by depositing enough company credits.
     */
    @Test
    void testQuotaMetNotifiesListener() {
        boolean[] called = {false};
        QuotaManager.getInstance().addListener(new QuotaListener() {
            @Override public void onQuotaMet(int r, int t, int tl, String msg) { called[0] = true; }
            @Override public void onDeadlineMissed(String msg) {}
        });

        QuotaManager.getInstance().addCompanyCredits(100);
        assertTrue(called[0]);
    }

    /**
     * Verifies that {@link QuotaListener#onDeadlineMissed} is called when
     * the turn limit is reached without meeting the quota.
     */
    @Test
    void testDeadlineMissedNotifiesListener() {
        boolean[] called = {false};
        QuotaManager.getInstance().addListener(new QuotaListener() {
            @Override public void onQuotaMet(int r, int t, int tl, String msg) {}
            @Override public void onDeadlineMissed(String msg) { called[0] = true; }
        });

        for (int i = 0; i < 200; i++) {
            QuotaManager.getInstance().tick();
        }
        assertTrue(called[0]);
    }

    /**
     * Verifies that the deadline listener is NOT called before the turn limit.
     * Edge case: ticking 199 times should not trigger the deadline.
     */
    @Test
    void testDeadlineNotMissedBeforeTurnLimit() {
        boolean[] called = {false};
        QuotaManager.getInstance().addListener(new QuotaListener() {
            @Override public void onQuotaMet(int r, int t, int tl, String msg) {}
            @Override public void onDeadlineMissed(String msg) { called[0] = true; }
        });

        for (int i = 0; i < 199; i++) {
            QuotaManager.getInstance().tick();
        }
        assertFalse(called[0]);
    }
}