package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Slime;
import game.capabilities.Ability;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stage 1 of the Fleshy Tree lifecycle. Displays as {@code y}.
 * <p>
 * A mutated tree sprout spawned by The Company's toxic waste. Each turn it
 * performs at most ONE action (single-threaded flora rule):
 * <ol>
 *   <li>If a worker ({@link Ability#ATTACKABLE_BY_CREATURES}) is on an adjacent
 *       tile, spawn one {@link Slime} on a random adjacent empty tile and stop.</li>
 *   <li>Otherwise, if the 20-turn growth timer fires, roll a 25% chance to
 *       grow into a {@link FleshyTreeSapling} and stop.</li>
 * </ol>
 * The growth counter resets on every 20-turn check regardless of whether the
 * roll succeeds, so growth is attempted at turns 20, 40, 60, etc.
 * <p>
 * Actors cannot enter the sprout tile itself.
 *
 * @author Ethan
 */
public class FleshyTreeSprout extends Ground {

    private static final int GROWTH_INTERVAL = 20;
    private static final int GROWTH_CHANCE = 25;

    private int turnCounter;
    private final Random random;

    /**
     * Constructor.
     */
    public FleshyTreeSprout() {
        super('y', "Fleshy Tree Sprout");
        this.turnCounter = 0;
        this.random = new Random();
    }

    /**
     * Actors cannot enter a tree sprout tile.
     *
     * @param actor the Actor attempting to enter
     * @return false always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn, performs at most one action:
     * spawn a Slime if a worker is adjacent, otherwise check for growth.
     *
     * @param location the location of this sprout
     */
    @Override
    public void tick(Location location) {
        turnCounter++;

        // Priority 1: spawn a Slime if a worker is on any adjacent tile
        if (isWorkerAdjacent(location)) {
            spawnSlime(location);
            return;
        }

        // Priority 2: attempt growth every 20 turns
        if (turnCounter >= GROWTH_INTERVAL) {
            turnCounter = 0; // reset regardless of roll outcome
            if (random.nextInt(100) < GROWTH_CHANCE) {
                location.setGround(new FleshyTreeSapling());
            }
        }
    }

    /**
     * Returns true if at least one adjacent tile contains a worker
     * (an actor with {@link Ability#ATTACKABLE_BY_CREATURES}).
     *
     * @param location the location of this sprout
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
     * Attempts to spawn a {@link Slime} on a random adjacent empty tile.
     * If no valid tile is available, the spawn is silently skipped.
     *
     * @param location the location of this sprout
     */
    private void spawnSlime(Location location) {
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
        try {
            spawnLocation.addActor(new Slime());
        } catch (GameEngineException e) {
        }
    }
}
