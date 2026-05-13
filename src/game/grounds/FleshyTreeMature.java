package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Undead;
import game.capabilities.Ability;
import game.spawning.SpawnEffectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stage 3 (final) of the Fleshy Tree lifecycle. Displays as {@code Y}.
 * <p>
 * A fully matured, heavily mutated fleshy tree. This stage has no further
 * growth. Each turn, if a worker ({@link Ability#ATTACKABLE_BY_CREATURES})
 * is on an adjacent tile, the tree spawns one {@link Undead} on a random
 * adjacent empty tile (single-threaded flora rule — only one action per turn).
 * <p>
 * Spawned Undeads register themselves with the {@link game.alarm.AlarmManager}
 * automatically via their constructor, consistent with REQ4 behaviour.
 * <p>
 * Actors cannot enter the mature tree tile itself.
 *
 * @author Ethan
 */
public class FleshyTreeMature extends Ground {

    private final Random random;

    /**
     * Constructor.
     */
    public FleshyTreeMature() {
        super('Y', "Fleshy Tree (Mature)");
        this.random = new Random();
    }

    /**
     * Actors cannot enter a mature tree tile.
     *
     * @param actor the Actor attempting to enter
     * @return false always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn, spawns an {@link Undead} on a random adjacent empty tile
     * if a worker is on any adjacent tile. Only one action is performed per turn.
     *
     * @param location the location of this mature tree
     */
    @Override
    public void tick(Location location) {
        if (isWorkerAdjacent(location)) {
            spawnUndead(location);
        }
    }

    /**
     * Returns true if at least one adjacent tile contains a worker
     * (an actor with {@link Ability#ATTACKABLE_BY_CREATURES}).
     *
     * @param location the location of this tree
     * @return true if a worker is adjacent
     */
    private boolean isWorkerAdjacent(Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()
                    && destination.getActor().hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to spawn an {@link Undead} on a random adjacent empty tile.
     * If no valid tile is available, the spawn is silently skipped.
     *
     * @param location the location of this tree
     */
    private void spawnUndead(Location location) {
        Undead undead = new Undead();

        List<Location> validLocations = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(undead)) {
                validLocations.add(destination);
            }
        }

        if (validLocations.isEmpty()) {
            return;
        }

        Location spawnLocation = validLocations.get(random.nextInt(validLocations.size()));

        try {
            spawnLocation.addActor(undead);
            SpawnEffectManager.getInstance().onCreatureSpawned(undead, spawnLocation);
        } catch (GameEngineException e) {
        }
    }
}
