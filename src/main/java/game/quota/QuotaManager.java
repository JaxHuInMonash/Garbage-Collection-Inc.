package game.quota;

import game.upgrades.CompanyAccount;

/**
 * Singleton that owns the entire Quota Cycle state.
 *
 * <p>Responsibilities (Single Responsibility Principle):
 * <ul>
 *   <li>Track current company credits accumulated toward the quota.</li>
 *   <li>Track the current turn counter and enforce the deadline.</li>
 *   <li>Advance the rank when the quota is met (scaling values via
 *       {@link QuotaScaling}).</li>
 *   <li>Notify registered {@link QuotaListener}s of quota events so that
 *       game objects (e.g. Supercomputer) can react without coupling to
 *       this class.</li>
 *   <li>Fulfil REQ3's {@link CompanyAccount} contract — exposes
 *       {@code canAfford} / {@code deduct} so Lab Stations can debit the
 *       pool when workers purchase upgrades.</li>
 * </ul>
 *
 * <p>All magic constants are defined once here (DRY). Rounding is delegated
 * to {@link QuotaScaling} (SRP / avoid Feature Envy).
 *
 * @author Mary
 * @author Jax (REQ3 integration)
 */
public class QuotaManager implements CompanyAccount {

    //Quota cycel constants
    private static final int INITIAL_QUOTA          = 100;
    private static final int INITIAL_TURN_LIMIT     = 200;
    private static final double QUOTA_GROWTH_RATE   = 0.05;   // +5 % each rank
    private static final double TIME_GROWTH_RATE    = 0.10;   // +10 % each rank

    //Singleton manager
    private static QuotaManager instance;

    //state
    private int companyRank;
    private int quotaTarget;
    private int turnLimit;
    private int companyCredits;
    private int currentTurn;
    /** Latches once the deadline-missed notification has fired so it doesn't repeat each tick. */
    private boolean deadlineNotified;

    private final java.util.List<QuotaListener> listeners = new java.util.ArrayList<>();

    //constructor
    private QuotaManager() {
        reset();
    }

    /** Returns the singleton instance, creating it if necessary. */
    public static QuotaManager getInstance() {
        if (instance == null) {
            instance = new QuotaManager();
        }
        return instance;
    }

    /** Resets the singleton (use between games / tests). */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Registers a listener that will be notified of quota events.
     *
     * @param listener the listener to add
     */
    public void addListener(QuotaListener listener) {
        listeners.add(listener);
    }

    // ── Turn tick ────────────────────────────────────────────────────────────

    /**
     * Called once per game turn (e.g. from the Supercomputer's {@code tick}
     * or the game loop). Advances the turn counter and checks whether the
     * deadline has been reached without meeting the quota.
     */
    public void tick() {
        currentTurn++;
        // Fire the deadline-missed notification exactly once, on the turn the
        // limit is first reached and the quota is still unmet. The latch is
        // cleared at the next rank-up so a future deadline can fire again.
        if (!deadlineNotified && currentTurn >= turnLimit && companyCredits < quotaTarget) {
            deadlineNotified = true;
            notifyDeadlineMissed();
        }
    }

    /**
     * Adds {@code credits} to the running company-credit total and checks
     * whether the quota has now been met.
     *
     * @param credits the amount to add (must be &gt; 0)
     */
    public void addCompanyCredits(int credits) {
        if (credits <= 0) return;
        companyCredits += credits;
        if (companyCredits >= quotaTarget) {
            advanceRank();
        }
    }

    /** Advances the company rank, scales the quota and time limit, and notifies listeners. */
    private void advanceRank() {
        companyRank++;
        quotaTarget   = QuotaScaling.scaleUp(quotaTarget,   QUOTA_GROWTH_RATE);
        turnLimit     = QuotaScaling.scaleUp(turnLimit,     TIME_GROWTH_RATE);
        companyCredits = 0;
        currentTurn    = 0;
        deadlineNotified = false; // re-arm for the new rank's deadline
        notifyQuotaMet();
    }

    // ── CompanyAccount integration (REQ3) ────────────────────────────────────

    /**
     * REQ3 integration — reports whether the company pool currently holds at
     * least {@code companyCredits}. Equivalent to {@code getCompanyCredits() >= amount},
     * exposed through the {@link CompanyAccount} interface so the REQ3 Lab
     * Stations and {@code ApplyUpgradeAction} can debit the pool without
     * depending on this concrete class.
     *
     * @param companyCredits amount to test
     * @return true if {@link #companyCredits} ≥ {@code companyCredits}
     */
    @Override
    public boolean canAfford(int companyCredits) {
        return this.companyCredits >= companyCredits;
    }

    /**
     * REQ3 integration — removes {@code companyCredits} from the company pool.
     * Spending against the pool moves the worker further from meeting the
     * current rank's quota, which is the intended REQ3 trade-off: Company
     * Credits spent on personal upgrades are not available for the deadline.
     * <p>
     * Callers must check {@link #canAfford(int)} first; an underfunded deduct
     * throws {@link IllegalStateException} rather than silently going negative.
     *
     * @param companyCredits amount to remove (must be ≤ current balance)
     * @throws IllegalStateException if the pool cannot cover the amount
     */
    @Override
    public void deduct(int companyCredits) {
        if (!canAfford(companyCredits)) {
            throw new IllegalStateException(
                    "QuotaManager.deduct: insufficient company credits ("
                            + this.companyCredits + " < " + companyCredits + ").");
        }
        this.companyCredits -= companyCredits;
    }

    private void notifyQuotaMet() {
        String msg = String.format(
                "[QUOTA] Quota met! Advancing to Rank %d. New target: %d credits in %d turns.",
                companyRank, quotaTarget, turnLimit);
        for (QuotaListener l : listeners) l.onQuotaMet(companyRank, quotaTarget, turnLimit, msg);
    }

    private void notifyDeadlineMissed() {
        String msg = String.format(
                "[QUOTA] Deadline reached! Quota not met (%d/%d). Workers near Supercomputer will be fired.",
                companyCredits, quotaTarget);
        for (QuotaListener l : listeners) l.onDeadlineMissed(msg);
    }

    /** Full reset to game-start values (Rank 1). */
    private void reset() {
        companyRank    = 1;
        quotaTarget    = INITIAL_QUOTA;
        turnLimit      = INITIAL_TURN_LIMIT;
        companyCredits = 0;
        currentTurn    = 0;
        deadlineNotified = false;
    }

    public int getCompanyRank()    { return companyRank; }
    public int getQuotaTarget()    { return quotaTarget; }
    public int getTurnLimit()      { return turnLimit; }
    public int getCompanyCredits() { return companyCredits; }
    public int getCurrentTurn()    { return currentTurn; }

    /** Human-readable quota status line for the HUD. */
    @Override
    public String toString() {
        return String.format("Rank %d | Quota: %d/%d credits | Turn: %d/%d",
                companyRank, companyCredits, quotaTarget, currentTurn, turnLimit);
    }
}