package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.spawning.SpawnEffectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A mysterious hole in the moon's facility that periodically spawns creatures.
 * Displays as {@code O}.
 * <p>
 * Every 20 game turns, the hole spawns a creature (chosen randomly from a
 * configurable list of suppliers) on a random adjacent empty tile. If no
 * adjacent tile is available, the spawn is skipped for that cycle.
 * <p>
 * Each turn, there is also a 1% chance the hole expands by converting a
 * random adjacent non-hole tile into a new Hole with the same creature
 * supplier configuration.
 * <p>
 * Actors cannot enter the hole tile itself.
 *
 * @author Jax
 */
public class Hole extends Ground {

    private static final int SPAWN_INTERVAL = 20;
    private static final int EXPANSION_CHANCE_PERCENT = 1;

    private int turnCounter;
    private final Random random;
    private final List<Supplier<Actor>> creatureSuppliers;

    /**
     * Constructor.
     *
     * @param creatureSuppliers list of suppliers, each producing a creature type
     *                          that this hole can spawn (chosen uniformly at random)
     */
    public Hole(List<Supplier<Actor>> creatureSuppliers) {
        super('O', "Hole");
        this.turnCounter = 0;
        this.random = new Random();
        this.creatureSuppliers = new ArrayList<>(creatureSuppliers);
    }

    /**
     * Actors cannot enter a hole.
     *
     * @param actor the Actor attempting to enter
     * @return false always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn, increments the counter. Every 20 turns, attempts to spawn
     * a creature on a random adjacent empty tile. Each turn, there is also
     * a 1% chance the hole expands to an adjacent tile.
     *
     * @param location the location of this hole
     */
    @Override
    public void tick(Location location) {
        turnCounter++;

        if (turnCounter >= SPAWN_INTERVAL) {
            turnCounter = 0;
            boolean spawned = spawnCreature(location);

            // Expansion only occurs after a successful spawn
            if (spawned && random.nextInt(100) < EXPANSION_CHANCE_PERCENT) {
                expand(location);
            }
        }
    }

    /**
     * Attempts to spawn a random creature on an adjacent empty tile.
     * After successful placement, notifies the {@link SpawnEffectManager}.
     *
     * @param location the location of this hole
     * @return true if a creature was successfully spawned
     */
    private boolean spawnCreature(Location location) {
        if (creatureSuppliers.isEmpty()) {
            return false;
        }

        List<Location> validLocations = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            Actor testActor = creatureSuppliers.get(0).get();
            if (destination.canActorEnter(testActor)) {
                validLocations.add(destination);
            }
        }

        if (validLocations.isEmpty()) {
            return false;
        }

        Location spawnLocation = validLocations.get(random.nextInt(validLocations.size()));
        Supplier<Actor> supplier = creatureSuppliers.get(random.nextInt(creatureSuppliers.size()));
        Actor creature = supplier.get();

        try {
            spawnLocation.addActor(creature);
            SpawnEffectManager.getInstance().onCreatureSpawned(creature, spawnLocation);
            return true;
        } catch (GameEngineException e) {
            // Spawn failed; silently skip this spawn cycle.
            return false;
        }
    }

    /**
     * Attempts to expand this hole by converting a random adjacent non-hole
     * tile into a new Hole with the same creature supplier configuration.
     *
     * @param location the location of this hole
     */
    private void expand(Location location) {
        List<Location> expandable = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (!(adjacent.getGround() instanceof Hole)) {
                expandable.add(adjacent);
            }
        }

        if (!expandable.isEmpty()) {
            Location target = expandable.get(random.nextInt(expandable.size()));
            target.setGround(new Hole(creatureSuppliers));
        }
    }
}
