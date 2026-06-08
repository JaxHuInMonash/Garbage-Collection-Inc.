package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.EnforcerBot;
import game.display.EventLog;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * Singleton that tracks every {@link game.actors.ContractedWorker}'s infraction
 * level (0-3) and manages {@link EnforcerBot} spawning.
 * <p>
 * When a Worker's infraction level reaches 1 or above,
 * {@link #spawnEnforcer(Actor, GameMap)} places an EnforcerBot on an empty Floor
 * tile within 4-5 tiles of the violator. Only one EnforcerBot is active per Worker
 * at any time; the {@code activeTargets} set prevents duplicate spawns.
 * <p>
 * When the bot catches or despawns, it calls {@link #clearTarget(Actor)} to
 * allow a new bot to be spawned on the next violation.
 *
 * @author Zhengjun Yang
 */
public class InfractionManager {
    private static final int MAX_LEVEL = 3;
    private static InfractionManager instance;
    private final Map<Actor, Integer> infractionLevels = new HashMap<>();
    private final Set<Actor> activeTargets = new HashSet<>();

    private InfractionManager() {
    }
    /**
     * Returns the singleton instance of InfractionManager.
     *
     * @return the singleton instance
     */
    public static InfractionManager getInstance() {
        if (instance == null) instance = new InfractionManager();
        return instance;
    }
    /**
     * Increments the violating Worker's infraction level by 1, capped at
     * {@value MAX_LEVEL}. If the new level is 1 or above, spawns an EnforcerBot.
     *
     * @param violator the Worker who committed the violation
     * @param map      the current GameMap
     */
    public void increment(Actor violator, GameMap map) {
        int current = infractionLevels.getOrDefault(violator, 0);
        int newLevel = Math.min(current + 1, MAX_LEVEL);
        infractionLevels.put(violator, newLevel);
        EventLog.getInstance().log("[INFRACTION] " + violator + " level now " + newLevel);
        if (newLevel >= 1) spawnEnforcer(violator, map);
    }
    /**
     * Returns the current infraction level of the given actor.
     *
     * @param actor the actor to check
     * @return the current infraction level, 0 if never recorded
     */
    public int getLevel(Actor actor) {
        return infractionLevels.getOrDefault(actor, 0);
    }
    /**
     * Resets the infraction level of the given actor back to 0.
     *
     * @param actor the actor to reset
     */
    public void reset(Actor actor) {
        infractionLevels.put(actor, 0);
    }
    /**
     * Removes the given Worker from the active targets set, allowing a new
     * EnforcerBot to be spawned on their next violation.
     *
     * @param violator the Worker whose active target status should be cleared
     */
    public void clearTarget(Actor violator) {
        activeTargets.remove(violator);
    }
    /**
     * Spawns an {@link EnforcerBot} on an empty Floor tile within 4-5 tiles
     * of the violator. Does nothing if the violator already has an active bot.
     *
     * @param violator the Worker to pursue
     * @param map      the current GameMap
     */
    public void spawnEnforcer(Actor violator, GameMap map) {
        if (activeTargets.contains(violator)) return;

        Location loc = map.locationOf(violator);
        int[] offsets = {-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5};

        for (int dx : offsets) {
            for (int dy : offsets) {
                if (dx == 0 && dy == 0) continue;
                int dist = Math.abs(dx) + Math.abs(dy);
                if (dist < 4 || dist > 5) continue;
                try {
                    Location candidate = map.at(loc.x() + dx, loc.y() + dy);
                    EnforcerBot bot = new EnforcerBot(violator);
                    if (candidate.canActorEnter(bot) && !candidate.containsAnActor()) {
                        candidate.addActor(bot);
                        activeTargets.add(violator);
                        EventLog.getInstance().log("[INFRACTION] EnforcerBot deployed!");
                        return;
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}