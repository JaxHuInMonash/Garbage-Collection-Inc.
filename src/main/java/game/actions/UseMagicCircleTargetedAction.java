package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;
import game.items.Flask;

/**
 * Variant of {@link UseMagicCircleAction} that teleports the actor to a
 * <strong>specific</strong> Magic Circle chosen at construction time, rather
 * than picking one at random.
 * <p>
 * Surfaced by {@link game.grounds.MagicCircle#allowableActions} to phase-stable
 * workers only (those carrying a {@link game.statuses.PhaseStabilizerStatus}) —
 * one instance per other circle on the map, so the player can pick exactly
 * where to land. Mirrors the multi-destination pattern of
 * {@link UseAlienCubeAction}.
 * <p>
 * Arrival still spawns a Flask adjacent to the destination, matching the base
 * action's flavour.
 *
 * @author Jax
 */
public class UseMagicCircleTargetedAction extends Action {

    private final Location targetCircle;

    /**
     * @param targetCircle the Magic Circle to teleport to
     */
    public UseMagicCircleTargetedAction(Location targetCircle) {
        this.targetCircle = targetCircle;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!targetCircle.canActorEnter(actor) || targetCircle.containsAnActor()) {
            return actor + " channels the Magic Circle, but the destination is blocked.";
        }
        map.moveActor(actor, targetCircle);
        spawnFlaskAdjacent(targetCircle);
        return actor + " channels the Magic Circle to (" + targetCircle.x()
                + ", " + targetCircle.y() + "). A Flask materialises nearby!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " channels Magic Circle → (" + targetCircle.x()
                + ", " + targetCircle.y() + ")";
    }

    private void spawnFlaskAdjacent(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (!adjacent.getGround().hasAbility(Ability.BLOCKS_ENTRY)
                    && !adjacent.containsAnActor()
                    && adjacent.getItems().isEmpty()) {
                adjacent.addItem(new Flask());
                return;
            }
        }
    }
}
