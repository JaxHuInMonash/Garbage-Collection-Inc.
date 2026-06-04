package game.quota;

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
 * </ul>
 *
 * <p>All magic constants are defined once here (DRY). Rounding is delegated
 * to {@link QuotaScaling} (SRP / avoid Feature Envy).
 *
 * @author Mary
 */
public class QuotaManager {

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
        if (currentTurn >= turnLimit && companyCredits < quotaTarget) {
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
        notifyQuotaMet();
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