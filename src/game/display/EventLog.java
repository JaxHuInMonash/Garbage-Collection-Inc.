package game.display;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton message queue that collects notable game events (creature
 * spawns, status applications, etc.) between player turns. The player's
 * {@code playTurn()} drains the queue each turn and prints the messages
 * above the action menu, giving the player full situational awareness
 * before choosing an action.
 *
 * @author Jax
 */
public class EventLog {

    private static EventLog instance;

    private final List<String> messages = new ArrayList<>();

    private EventLog() {
    }

    /**
     * Returns the singleton instance.
     *
     * @return the EventLog instance
     */
    public static EventLog getInstance() {
        if (instance == null) {
            instance = new EventLog();
        }
        return instance;
    }

    /**
     * Resets the singleton for a fresh game.
     */
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Queues a message to be displayed on the player's next turn.
     *
     * @param message the event description
     */
    public void log(String message) {
        messages.add(message);
    }

    /**
     * Returns all queued messages and clears the queue. The caller is
     * responsible for displaying them.
     *
     * @return the list of messages since the last drain (may be empty)
     */
    public List<String> drain() {
        List<String> copy = new ArrayList<>(messages);
        messages.clear();
        return copy;
    }
}
