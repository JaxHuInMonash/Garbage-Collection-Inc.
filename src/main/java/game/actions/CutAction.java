package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.PlasmaCutter;
import game.capabilities.Cuttable;

/**
 * Action that uses a {@link PlasmaCutter} to cut a {@link Cuttable} target.
 *
 * <p>The action delegates all cut logic to the target's
 * {@link Cuttable#onCut(Actor, Location, GameMap)} method (OCP / Tell Don't Ask),
 * keeping this class a thin coordinator with no knowledge of what "cutting" means
 * for each specific target.
 *
 * @author Mary
 */
public class CutAction extends Action {

    private final Cuttable target;
    private final Location targetLocation;

    /**
     * Constructor for cutting a ground tile (door, vent) at a known location.
     *
     * @param target         the cuttable target
     * @param targetLocation the location of the target on the map
     */
    public CutAction(Cuttable target, Location targetLocation) {
        this.target = target;
        this.targetLocation = targetLocation;
    }

    /**
     * Delegates entirely to {@link Cuttable#onCut}: the target knows what
     * happens when it is cut (drops items, transforms tile, spawns creatures, etc.).
     *
     * @param actor the actor wielding the plasma cutter
     * @param map   the current map
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return target.onCut(actor, targetLocation, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " cuts " + target + " with the Plasma Cutter";
    }
}