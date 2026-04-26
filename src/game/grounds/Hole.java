package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Slime;
import game.actors.Undead;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A mysterious hole in the moon's facility that periodically spawns creatures.
 * Displays as {@code o}.
 * <p>
 * Every 20 game turns, the hole spawns either an {@link Undead} or a
 * {@link Slime} (equal probability) on a random adjacent empty tile.
 * If no adjacent tile is available, the spawn is skipped for that cycle.
 * <p>
 * Actors cannot enter the hole tile itself.
 *
 * @author Jax
 */
public class Hole extends Ground {

    private static final int SPAWN_INTERVAL = 20;

    private int turnCounter;
    private final Random random;

    /**
     * Constructor.
     */
    public Hole() {
        super('o', "Hole");
        this.turnCounter = 0;
        this.random = new Random();
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
     * a creature on a random adjacent empty tile.
     *
     * @param location the location of this hole
     */
    @Override
    public void tick(Location location) {
        turnCounter++;
        if (turnCounter >= SPAWN_INTERVAL) {
            turnCounter = 0;
            spawnCreature(location);
        }
    }

    /**
     * Attempts to spawn a random creature on an adjacent empty tile.
     * If no valid tile is available, the spawn is skipped.
     *
     * @param location the location of this hole
     */
    private void spawnCreature(Location location) {
        // Collect adjacent locations that are empty and passable
        List<Location> validLocations = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (!destination.containsAnActor() && destination.getGround().canActorEnter(null)) {
                validLocations.add(destination);
            }
        }

        if (validLocations.isEmpty()) {
            return;
        }

        Location spawnLocation = validLocations.get(random.nextInt(validLocations.size()));
        Actor creature = random.nextBoolean() ? new Undead() : new Slime();

        try {
            spawnLocation.addActor(creature);
        } catch (GameEngineException e) {
            // Spawn failed; silently skip this spawn cycle.
        }
    }
}
