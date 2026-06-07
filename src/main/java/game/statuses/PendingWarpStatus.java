package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A staged warp offer placed on a phase-stable worker by an adjacent
 * {@link game.grounds.WarperTreeMature}. Holds the destination the tree
 * <em>would</em> have warped the worker to, but pending the worker's consent
 * — the worker may accept the offer via {@link game.actions.AcceptWarpAction}
 * on a subsequent turn, or simply let it expire by doing something else.
 * <p>
 * Auto-expires after {@code duration} turns (driven by the engine's per-actor
 * status tick) so unused offers don't accumulate.
 *
 * @author Jax
 */
public class PendingWarpStatus implements Status {

    private final Location destination;
    private final String sourceLabel;
    private int remainingTurns;

    /**
     * @param destination  the candidate landing tile (already validated as enterable + empty)
     * @param sourceLabel  human-readable origin description (e.g. "Warper Tree at (12, 5)") for menu text
     * @param duration     number of turns this offer remains in the menu before auto-expiring
     */
    public PendingWarpStatus(Location destination, String sourceLabel, int duration) {
        this.destination = destination;
        this.sourceLabel = sourceLabel;
        this.remainingTurns = duration;
    }

    public Location getDestination() {
        return destination;
    }

    public String getSourceLabel() {
        return sourceLabel;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        remainingTurns--;
    }

    @Override
    public boolean isStatusActive() {
        return remainingTurns > 0;
    }

    @Override
    public String toString() {
        return "Pending warp from " + sourceLabel
                + " → (" + destination.x() + ", " + destination.y()
                + ") — " + remainingTurns + " turns to decide";
    }
}
