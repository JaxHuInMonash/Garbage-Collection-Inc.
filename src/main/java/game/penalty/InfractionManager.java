package game.penalty;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.EnforcerBot;
import java.util.HashMap;
import java.util.Map;

public class InfractionManager {
    private static final int MAX_LEVEL = 3;
    private static InfractionManager instance;
    private final Map<Actor, Integer> infractionLevels = new HashMap<>();

    private InfractionManager() {}

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

    public void spawnEnforcer(Actor violator, GameMap map) {
        Location loc = map.locationOf(violator);
        for (Exit exit : loc.getExits()) {
            Location candidate = exit.getDestination();
            EnforcerBot bot = new EnforcerBot(violator);
            if (candidate.canActorEnter(bot) && !candidate.containsAnActor()) {
                try {
                    candidate.addActor(bot);
                    System.out.println("[INFRACTION] EnforcerBot deployed!");
                } catch (Exception ignored) {}
                return;
            }
        }
    }
}
