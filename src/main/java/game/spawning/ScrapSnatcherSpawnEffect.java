package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Depositable;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Spawn effect for the Scrap Snatcher: a "loot explosion" that scatters one
 * random depositable resource onto every valid, empty tile adjacent to the
 * newly spawned Snatcher.
 * <p>
 * A tile is considered valid if the Snatcher itself could enter it (i.e. not
 * a wall, tree, or other impassable ground) and no actor already occupies it.
 * Each adjacent tile independently rolls a random item from the configured
 * {@code resourcePool}, so the same type can appear multiple times.
 * <p>
 * The pool is injected at construction time so new depositable item types can
 * be added in {@link game.EclipseNebula} without modifying this class
 * (Open-Closed Principle).
 *
 * @author Ethan
 */
public class ScrapSnatcherSpawnEffect implements SpawnEffect {

    private final List<Supplier<? extends Depositable>> resourcePool;
    private final Random random = new Random();

    /**
     * @param resourcePool suppliers for every depositable resource type
     *                     that can appear in the loot explosion
     */
    public ScrapSnatcherSpawnEffect(List<Supplier<? extends Depositable>> resourcePool) {
        this.resourcePool = resourcePool;
    }

    /**
     * Drops one random resource from the pool onto every valid adjacent tile.
     */
    @Override
    public void apply(Actor creature, Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.canActorEnter(creature)) {
                Depositable resource = resourcePool.get(random.nextInt(resourcePool.size())).get();
                adjacent.addItem((Item) resource);
            }
        }
    }
}
