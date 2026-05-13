package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Abstract base for all creature state-machine states.
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
public interface  CreatureState {

    /**
     * Called exactly once when the creature first enters this state.
     * Must trigger a significant immediate effect (damage, status, drop, etc.).
     *
     * @param creature the creature entering this state
     * @param map      the game map
     */
    void onEnter(Actor creature, GameMap map);

    /**
     * Called when the creature is about to leave this state.
     * Default is a no-op; override to remove transient statuses or sever links.
     *
     * @param creature the creature leaving this state
     * @param map      the game map
     */
    default void onExit(Actor creature, GameMap map) {}

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
    CreatureState checkTransitions(Actor creature, Location location, GameMap map);

    /**
     * Returns the action the creature will execute this turn in this state.
     *
     * @param creature the creature acting
     * @param location the creature's current location
     * @return the chosen Action
     */
    Action getAction(Actor creature, Location location);

    /** Human-readable state name for display and debugging. */
    String getStateName();
}
