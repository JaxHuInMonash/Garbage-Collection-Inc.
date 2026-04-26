package game.alarm;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton manager that coordinates the facility alarm system.
 * <p>
 * This is the Subject in the Observer pattern. It maintains a list of
 * {@link AlarmListener} observers and notifies all of them when the alarm
 * is triggered. Each listener defines its own response independently
 * (Single Responsibility Principle), and new consequences can be added
 * by registering new listeners (Open-Closed Principle).
 * <p>
 * The alarm can only be triggered once per game — subsequent triggers
 * are ignored.
 *
 * @author Jax
 */
public class AlarmManager {

    private static AlarmManager instance;

    private final List<AlarmListener> listeners = new ArrayList<>();
    private boolean alarmTriggered = false;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private AlarmManager() {
    }

    /**
     * Returns the singleton instance of the AlarmManager.
     *
     * @return the AlarmManager instance
     */
    public static AlarmManager getInstance() {
        if (instance == null) {
            instance = new AlarmManager();
        }
        return instance;
    }

    /**
     * Resets the singleton instance. Should be called at game initialisation
     * to ensure a clean state.
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Registers a listener to be notified when the alarm triggers.
     *
     * @param listener the listener to register
     */
    public void registerListener(AlarmListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener so it will no longer be notified.
     *
     * @param listener the listener to remove
     */
    public void removeListener(AlarmListener listener) {
        listeners.remove(listener);
    }

    /**
     * Triggers the facility alarm, notifying all registered listeners.
     * Can only be triggered once; subsequent calls are ignored.
     *
     * @param triggeringWorker the worker who set off the alarm
     * @param map              the game map
     */
    public void triggerAlarm(Actor triggeringWorker, GameMap map) {
        if (alarmTriggered) {
            return;
        }
        alarmTriggered = true;

        // Notify all listeners (iterate over a copy to allow safe modification)
        for (AlarmListener listener : new ArrayList<>(listeners)) {
            listener.onAlarmTriggered(triggeringWorker, map);
        }
    }

    /**
     * Returns whether the alarm has been triggered.
     *
     * @return true if the alarm has been triggered
     */
    public boolean isAlarmTriggered() {
        return alarmTriggered;
    }
}
