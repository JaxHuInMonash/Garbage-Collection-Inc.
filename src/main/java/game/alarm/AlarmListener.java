package game.alarm;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An interface for entities that respond to the facility alarm being triggered.
 * <p>
 * This is part of the Observer pattern: {@link AlarmManager} is the subject,
 * and implementations of this interface are observers. When the alarm fires,
 * each listener's {@link #onAlarmTriggered(Actor, GameMap)} is called, allowing
 * it to define its own response (e.g., spawning drones, changing behaviour).
 * <p>
 * New consequences can be added by implementing this interface and registering
 * with the {@link AlarmManager}, without modifying any existing code
 * (Open-Closed Principle).
 *
 * @author Jax
 */
public interface AlarmListener {

    /**
     * Called when the facility alarm is triggered.
     *
     * @param triggeringWorker the worker who triggered the alarm
     * @param map              the game map where the alarm was triggered
     */
    void onAlarmTriggered(Actor triggeringWorker, GameMap map);
}
