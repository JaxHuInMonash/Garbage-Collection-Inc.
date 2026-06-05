package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
/**
 * Abstract base class for all {@link game.actors.EnforcerBot} AI behaviours.
 * <p>
 * Holds a reference to the target Worker and defines the contract for all
 * enforcement AI via the {@link #operate(Actor, Location)} method. Three
 * concrete subclasses implement distinct bot strategies: chasing and fining,
 * patrolling, and surrender detection.
 *
 * @author Zhengjun Yang
 */
public abstract class EnforcerBehaviour implements Behaviour<Actor, Action> {
    protected final Actor target;
    /**
     * Constructor.
     *
     * @param target the Worker being pursued or monitored
     */
    public EnforcerBehaviour(Actor target) {
        this.target = target;
    }
    /**
     * Decide what action the EnforcerBot should take this turn.
     *
     * @param bot      the EnforcerBot actor
     * @param location the bot's current location
     * @return the Action to perform, or null if nothing to do
     */
    @Override
    public abstract Action operate(Actor bot, Location location);
}