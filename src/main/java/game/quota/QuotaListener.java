package game.quota;

/**
 * Observer interface for Quota Cycle events.
 *
 * <p>Implementing the Observer pattern here keeps {@link QuotaManager}
 * open for extension and closed for modification (OCP): new reactions
 * (e.g. map changes, sound effects) are added by registering a new
 * listener rather than editing the manager.
 *
 * <p>Connascence of name: callers depend only on this interface,
 * not on concrete listener implementations.
 *
 * @author Mary
 */
public interface QuotaListener {

    /**
     * Called when the quota for the current rank has been fully met and
     * the manager has already advanced to the next rank.
     *
     * @param newRank      the rank just reached
     * @param newTarget    the new quota target (already scaled up)
     * @param newTurnLimit the new turn limit (already scaled up)
     * @param message      a human-readable summary for logging / display
     */
    void onQuotaMet(int newRank, int newTarget, int newTurnLimit, String message);

    /**
     * Called when the turn deadline expires without the quota being met.
     * At this point the Supercomputer should fire adjacent workers.
     *
     * @param message a human-readable summary for logging / display
     */
    void onDeadlineMissed(String message);
}