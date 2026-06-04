package game.penalty;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.EnforcerBot;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InfractionManager {
    private static final int MAX_LEVEL = 3;
    private static InfractionManager instance;
    private final Map<Actor, Integer> infractionLevels = new HashMap<>();
    private final Set<Actor> activeTargets = new HashSet<>();

    private InfractionManager() {
    }

    public static InfractionManager getInstance() {
        if (instance == null) instance = new InfractionManager();
        return instance;
    }

    public void increment(Actor violator, GameMap map) {
        int current = infractionLevels.getOrDefault(violator, 0);
        int newLevel = Math.min(current + 1, MAX_LEVEL);
        infractionLevels.put(violator, newLevel);
        System.out.println("[INFRACTION] " + violator + " level now " + newLevel);
        if (newLevel >= 1) spawnEnforcer(violator, map);
    }

    public int getLevel(Actor actor) {
        return infractionLevels.getOrDefault(actor, 0);
    }

    public void reset(Actor actor) {
        infractionLevels.put(actor, 0);
    }

    public void clearTarget(Actor violator) {
        activeTargets.remove(violator);
    }

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
                        System.out.println("[INFRACTION] EnforcerBot deployed!");
                        return;
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}