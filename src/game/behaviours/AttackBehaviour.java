package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.capabilities.Ability;

import java.util.function.Predicate;

/**
 * A behaviour that makes an actor attack an adjacent target that satisfies
 * a configurable filter.
 * <p>
 * By default, only targets with {@link Ability#ATTACKABLE_BY_CREATURES} are
 * attacked. Callers can supply a custom {@link Predicate} to widen or
 * narrow the targeting (e.g. {@code target -> true} for indiscriminate
 * aggression).
 *
 * @author Jax
 */
public class AttackBehaviour implements Behaviour<Actor, Action> {

    private final Predicate<Actor> targetFilter;

    /**
     * Constructor with selective targeting.
     * Only attacks adjacent actors that possess
     * {@link Ability#ATTACKABLE_BY_CREATURES}.
     */
    public AttackBehaviour() {
        this(target -> target.hasAbility(Ability.ATTACKABLE_BY_CREATURES));
    }

    /**
     * Constructor with a custom target filter.
     *
     * @param targetFilter predicate that returns {@code true} for actors
     *                     this behaviour should attack
     */
    public AttackBehaviour(Predicate<Actor> targetFilter) {
        this.targetFilter = targetFilter;
    }

    /**
     * Returns an AttackAction targeting the first adjacent actor that
     * satisfies the target filter, or null if no valid target is adjacent.
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
                if (targetFilter.test(target)) {
                    return new AttackAction(target);
                }
            }
        }

        return null;
    }
}
