package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.display.EventLog;

import java.util.HashMap;
import java.util.Map;

/**
 * A singleton manager that dispatches environmental reactions when creatures
 * are spawned into the game world.
 * <p>
 * This uses a registry pattern: each creature class is mapped to a
 * {@link SpawnEffect}. When a spawner calls {@link #onCreatureSpawned(Actor, Location)},
 * the manager looks up and applies the appropriate effect.
 * <p>
 * This design satisfies the Open-Closed Principle: new creature types can
 * register their own spawn effects without modifying existing code. It also
 * provides a single point of integration for all spawners (Holes, Vents,
 * trees, infection, etc.) to call, ensuring spawn effects are applied
 * consistently regardless of the source.
 *
 * @author Jax
 */
public class SpawnEffectManager {

    private static SpawnEffectManager instance;

    private final Map<Class<? extends Actor>, SpawnEffect> effects = new HashMap<>();

    private SpawnEffectManager() {
    }

    /**
     * Returns the singleton instance.
     *
     * @return the SpawnEffectManager instance
     */
    public static SpawnEffectManager getInstance() {
        if (instance == null) {
            instance = new SpawnEffectManager();
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
     * Registers a spawn effect for a specific creature type.
     *
     * @param creatureType the class of the creature
     * @param effect       the effect to apply when this creature type spawns
     */
    public void registerEffect(Class<? extends Actor> creatureType, SpawnEffect effect) {
        effects.put(creatureType, effect);
    }

    /**
     * Called after a creature is successfully placed on the map.
     * Looks up and applies the registered spawn effect for the creature's type.
     *
     * @param creature the newly spawned creature
     * @param location the location where the creature was placed
     */
    public void onCreatureSpawned(Actor creature, Location location) {
        EventLog.getInstance().log(
                creature + " spawned at (" + location.x() + ", " + location.y()
                        + ") of " + location.map());
        SpawnEffect effect = effects.get(creature.getClass());
        if (effect != null) {
            effect.apply(creature, location);
        }
    }
}
