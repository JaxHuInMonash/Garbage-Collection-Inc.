package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Floor;
import game.statuses.PhaseStabilizerStatus;

import java.util.List;

/**
 * Surveyor Tier 3 — "Phase Through Wall" action. Offered to the worker by
 * {@link game.actors.ContractedWorker#playTurn} when the worker carries a
 * charged {@link PhaseStabilizerStatus} and stands adjacent to an
 * {@link game.capabilities.Ability#IS_WALL IS_WALL} tile.
 * <p>
 * Cross-component: consumes a status charge, permanently modifies the map
 * (Wall → Floor), then moves the actor. Pure terrain change driven by an
 * actor capability.
 *
 * @author Jax
 */
public class PhaseThroughWallAction extends Action {

    private final Location wallLocation;
    private final String direction;

    /**
     * @param wallLocation the wall tile to phase through
     * @param direction    direction of the wall from the actor (for menu text)
     */
    public PhaseThroughWallAction(Location wallLocation, String direction) {
        this.wallLocation = wallLocation;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        List<PhaseStabilizerStatus> statuses = actor.statusesOf(PhaseStabilizerStatus.class);
        if (statuses.isEmpty() || !statuses.get(0).hasCharge()) {
            return actor + " tries to phase, but the stabilizer is recharging.";
        }

        // Permanently convert the wall to floor, then move the actor.
        wallLocation.setGround(new Floor());
        map.moveActor(actor, wallLocation);
        statuses.get(0).consumeCharge();

        return actor + " phases through the wall — it crumbles into floor.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " phases through the wall to the " + direction;
    }
}
