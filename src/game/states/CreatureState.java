package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.StatefulCreature;
import game.behaviours.WanderBehaviour;

/**
 * Interface for all creature state-machine states.
 * <p>
 * Each concrete state encapsulates:
 * <ul>
 *   <li>{@link #onEnter} — an immediate, significant effect applied the moment
 *       this state is entered (area damage, status application, item drops, etc.)</li>
 *   <li>{@link #onExit} — clean-up when leaving this state (remove transient statuses,
 *       sever links, reset counters).</li>
 *   <li>{@link #checkTransitions} — deterministic logic that inspects the world
 *       (surroundings, HP thresholds, counters) and returns the next state. The
 *       transitions live <em>in the state</em>, satisfying the State Pattern contract.</li>
 *   <li>{@link #getAction} — the action the creature executes while in this state.</li>
 * </ul>
 */
public interface CreatureState {

    /** Shared WanderBehaviour instance — stateless between calls, safe to reuse. */
    WanderBehaviour WANDER = new WanderBehaviour();

    /**
     * Convenience method: attempts to wander, falls back to doing nothing.
     * Eliminates the repeated {@code new WanderBehaviour().operate(...)} pattern
     * across every state's {@link #getAction} implementation.
     *
     * @param creature the creature that will wander
     * @param location the creature's current location
     * @return a move action to a random adjacent tile, or {@link DoNothingAction}
     */
    default Action wanderOrDoNothing(StatefulCreature creature, Location location) {
        Action wander = WANDER.operate(creature, location);
        return wander != null ? wander : new DoNothingAction();
    }

    /**
     * Called exactly once when the creature first enters this state.
     * Must trigger a significant immediate effect (damage, status, drop, etc.).
     *
     * @param creature the creature entering this state
     * @param map      the game map
     */
    void onEnter(StatefulCreature creature, GameMap map);

    /**
     * Called when the creature is about to leave this state.
     * Default is a no-op; override to remove transient statuses or sever links.
     *
     * @param creature the creature leaving this state
     * @param map      the game map
     */
    default void onExit(StatefulCreature creature, GameMap map) {}

    /**
     * Inspects the world and returns the next state. Must be deterministic —
     * based solely on measurable conditions (surroundings, HP ratio, counters).
     * Returns {@code this} if no transition is required this turn.
     *
     * @param creature the creature whose world is being inspected
     * @param location the creature's current location
     * @param map      the game map
     * @return the next state (may be the same instance)
     */
    CreatureState checkTransitions(StatefulCreature creature, Location location, GameMap map);

    /**
     * Returns the action the creature will execute this turn in this state.
     *
     * @param creature the creature acting
     * @param location the creature's current location
     * @return the chosen Action
     */
    Action getAction(StatefulCreature creature, Location location);

    /** Human-readable state name for display and debugging. */
    String getStateName();
}
