package game.capabilities;

/**
 * Callback interface for notifying a listener when a kill occurs.
 * Used by the Swarmling's BerserkState to track how many entities
 * have been eliminated so it can transition back to DormantState
 * after 3 kills.
 */
@FunctionalInterface
public interface KillListener {

    /** Called each time the associated attack action kills a target. */
    void onKill();
}
