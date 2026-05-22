package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Parasite;
import game.actors.Slime;
import game.capabilities.Ability;
import game.spawning.SpawnEffectManager;
import game.statuses.PoisonStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A toxic vent that is motion-activated. Displays as {@code V}.
 * <p>
 * When a worker (actor with {@link Ability#ATTACKABLE_BY_CREATURES}) is
 * standing within its adjacent tiles, the vent activates: it spawns either
 * a {@link Parasite} or a {@link Slime} (equal probability) on a random
 * adjacent empty tile, then poisons all adjacent actors (including the
 * newly spawned creature) for 5 turns at 1 damage per turn.
 * <p>
 * When inactive (no workers nearby), the vent does nothing.
 * Actors can walk over the vent tile.
 *
 * @author Jax
 */
public class Vent extends Ground {

    private static final int POISON_DAMAGE = 1;
    private static final int POISON_DURATION = 5;

    private final Random random;

    /**
     * Constructor.
     */
    public Vent() {
        super('V', "Vent");
        this.random = new Random();
    }

    /**
     * Each turn, checks if any worker is within adjacent tiles.
     * If so, activates: spawns a creature on an adjacent empty tile,
     * then poisons all adjacent actors (including the newly spawned creature).
     *
     * @param location the location of this vent
     */
    @Override
    public void tick(Location location) {
        if (!isWorkerNearby(location)) {
            return;
        }

        // Spawn a creature first; only poison if spawn was successful
        boolean spawned = spawnCreature(location);

        if (spawned) {
            // Poison all adjacent actors (including the newly spawned creature)
            poisonAdjacentActors(location);
        }
    }

    /**
     * Checks if any worker is within adjacent tiles of this vent.
     *
     * @param location the vent's location
     * @return true if a worker is adjacent
     */
    private boolean isWorkerNearby(Location location) {
        for (Exit exit : location.getExits()) {
            if (exit.getDestination().containsAnActor()) {
                Actor actor = exit.getDestination().getActor();
                if (actor.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Spawns either a Parasite or Slime on a random adjacent empty tile.
     *
     * @param location the vent's location
     * @return true if a creature was successfully spawned
     */
    private boolean spawnCreature(Location location) {
        List<Location> validLocations = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (!destination.containsAnActor()) {
                validLocations.add(destination);
            }
        }

        if (validLocations.isEmpty()) {
            return false;
        }

        Location spawnLocation = validLocations.get(random.nextInt(validLocations.size()));
        Actor creature = random.nextBoolean() ? new Parasite() : new Slime();

        try {
            spawnLocation.addActor(creature);
            SpawnEffectManager.getInstance().onCreatureSpawned(creature, spawnLocation);
            return true;
        } catch (GameEngineException e) {
            // Spawn failed; skip
            return false;
        }
    }

    /**
     * Applies poison to all actors in adjacent tiles, including any
     * newly spawned creature. Poison lasts 5 turns at 1 damage per turn.
     *
     * @param location the vent's location
     */
    private void poisonAdjacentActors(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) {
                adjacent.getActor().addStatus(new PoisonStatus(POISON_DAMAGE, POISON_DURATION));
            }
        }
    }
}
