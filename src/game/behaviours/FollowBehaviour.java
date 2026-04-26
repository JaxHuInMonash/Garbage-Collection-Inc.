package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A behaviour that moves an actor one step closer to a target actor
 * using Manhattan distance.
 * <p>
 * Each turn, the behaviour evaluates all adjacent exits and picks the one
 * that minimises the Manhattan distance to the target. If the target has
 * been removed from the map or no closer step is available, returns null.
 *
 * @author Jax
 */
public class FollowBehaviour implements Behaviour<Actor, Action> {

    private final Actor target;

    /**
     * Constructor.
     *
     * @param target the actor to follow
     */
    public FollowBehaviour(Actor target) {
        this.target = target;
    }

    /**
     * Returns a MoveActorAction that moves one step closer to the target,
     * or null if the target is gone or no closer step exists.
     *
     * @param actor    the actor doing the following
     * @param location the current location of the actor
     * @return a MoveActorAction toward the target, or null
     */
    @Override
    public Action operate(Actor actor, Location location) {
        GameMap map = location.map();
        if (!map.contains(target) || !map.contains(actor)) {
            return null;
        }

        Location here = map.locationOf(actor);
        Location there = map.locationOf(target);

        int currentDistance = distance(here, there);
        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(actor)) {
                int newDistance = distance(destination, there);
                if (newDistance < currentDistance) {
                    return new MoveActorAction(destination, exit.getName());
                }
            }
        }

        return null;
    }

    /**
     * Computes the Manhattan distance between two locations.
     *
     * @param a the first location
     * @param b the second location
     * @return the Manhattan distance
     */
    private int distance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
