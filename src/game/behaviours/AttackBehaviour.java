package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.capabilities.Ability;

/**
 * A behaviour that makes an actor attack an adjacent target if one is present.
 * <p>
 * The behaviour scans all adjacent locations for actors that possess the
 * {@link Ability#ATTACKABLE_BY_CREATURES} ability and returns an
 * {@link AttackAction} targeting the first one found.
 *
 * @author Jax
 */
public class AttackBehaviour implements Behaviour<Actor, Action> {

    /**
     * Returns an AttackAction targeting the first adjacent actor that has
     * the {@link Ability#ATTACKABLE_BY_CREATURES} ability, or null if no
     * valid target is adjacent.
     *
     * @param actor    the actor looking to attack
     * @param location the current location of the actor
     * @return an AttackAction, or null if no valid target is nearby
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                Actor target = destination.getActor();
                if (target.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                    return new AttackAction(target);
                }
            }
        }

        return null;
    }
}
