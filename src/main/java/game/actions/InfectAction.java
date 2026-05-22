package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Infectable;

/**
 * An action where a Parasite infects an {@link Infectable} target
 * (actor or item) and then dies in the process.
 * <p>
 * The action delegates to the target's {@link Infectable#onInfected(Actor, GameMap)}
 * method, then removes the infecting Parasite from the map.
 *
 * @author Jax
 */
public class InfectAction extends Action {

    /** The target being infected. */
    private final Infectable target;

    /** The location of the target (used for display context). */
    private final Location targetLocation;

    /**
     * Constructor.
     *
     * @param target         the infectable entity to infect
     * @param targetLocation the location where the target resides
     */
    public InfectAction(Infectable target, Location targetLocation) {
        this.target = target;
        this.targetLocation = targetLocation;
    }

    /**
     * Performs the infection: applies the infection effect on the target,
     * then removes the Parasite from the map (it dies after infecting).
     *
     * @param actor the Parasite performing this action
     * @param map   the map the actor is on
     * @return a description of the infection event
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        target.onInfected(actor, map);
        map.removeActor(actor);
        return actor + " infects " + target + " and perishes";
    }

    /**
     * Returns a menu description for this action.
     *
     * @param actor the actor performing this action
     * @return a string for the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " infects " + target;
    }
}
