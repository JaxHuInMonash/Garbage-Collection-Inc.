package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;

/**
 * A behaviour that makes an actor attack any adjacent actor indiscriminately.
 * <p>
 * Unlike {@link AttackBehaviour}, which only targets actors with the
 * {@link game.capabilities.Ability#ATTACKABLE_BY_CREATURES} ability,
 * this behaviour attacks the first adjacent actor found — workers,
 * creatures, anything. Used by Security Drones which are hostile to all
 * moving entities.
 *
 * @author Jax
 */
public class AggressiveAttackBehaviour implements Behaviour<Actor, Action> {

    /**
     * Returns an AttackAction targeting the first adjacent actor found,
     * regardless of type, or null if no actor is adjacent.
     *
     * @param actor    the actor looking to attack
     * @param location the current location of the actor
     * @return an AttackAction, or null if no target is nearby
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                Actor target = destination.getActor();
                return new AttackAction(target);
            }
        }

        return null;
    }
}
