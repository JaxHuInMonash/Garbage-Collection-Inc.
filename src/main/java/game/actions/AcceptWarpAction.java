package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.statuses.PendingWarpStatus;

/**
 * Consumes a worker's staged {@link PendingWarpStatus} (offered by a
 * {@link game.grounds.WarperTreeMature} to a phase-stable worker) and performs
 * the warp.
 * <p>
 * Surfaced in the worker's menu by {@link game.actors.ContractedWorker#playTurn}
 * whenever a pending warp is present. The status is removed after execution
 * regardless of outcome — a failed warp (destination became blocked between
 * staging and acceptance) just clears the offer cleanly.
 *
 * @author Jax
 */
public class AcceptWarpAction extends Action {

    private final PendingWarpStatus warp;

    public AcceptWarpAction(PendingWarpStatus warp) {
        this.warp = warp;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        Location dest = warp.getDestination();

        // Defensive: destination may have been taken since the offer was staged.
        if (!dest.canActorEnter(actor) || dest.containsAnActor()) {
            actor.removeStatus(warp);
            return actor + " tries to phase-warp, but the destination is no longer clear.";
        }

        map.moveActor(actor, dest);
        actor.removeStatus(warp);
        return actor + " phase-warps via " + warp.getSourceLabel()
                + " → (" + dest.x() + ", " + dest.y() + ").";
    }

    @Override
    public String menuDescription(Actor actor) {
        Location dest = warp.getDestination();
        return actor + " accept warp from " + warp.getSourceLabel()
                + " → (" + dest.x() + ", " + dest.y() + ")";
    }
}
