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
 * Stage 1 of the Fleshy Tree lifecycle on the <em>99-Deprecated</em> map.
 * Displays as {@code y}.
 * <p>
 * This variant differs from the standard {@link FleshyTreeSprout} in two ways:
 * <ol>
 *   <li>It spawns an {@link Undead} (not a Slime) when a worker is adjacent.</li>
 *   <li>It skips the Sapling stage entirely, growing directly into a
 *       {@link DeprecatedFleshyTreeMature} after 20 turns with a 25% chance.</li>
 * </ol>
 * The single-threaded flora rule applies: at most one action is performed per turn.
 * <p>
 * Actors cannot enter this tile.
 *
 * @author Ethan
 */
public class DeprecatedFleshyTreeSprout extends Ground {

    private static final int GROWTH_INTERVAL = 20;
    private static final int GROWTH_CHANCE = 25;

    private int turnCounter;
    private static final Random RANDOM = new Random();

    /** Constructor. */
    public DeprecatedFleshyTreeSprout() {
        super('y', "Fleshy Tree Sprout (Deprecated)");
        this.turnCounter = 0;
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn, performs at most one action:
     * spawn an Undead if a worker is adjacent; otherwise check for growth
     * directly to {@link DeprecatedFleshyTreeMature}.
     *
     * @param location the location of this sprout
     */
    @Override
    public void tick(Location location) {
        turnCounter++;

        if (isWorkerAdjacent(location)) {
            spawnUndead(location);
            return;
        }

        if (turnCounter >= GROWTH_INTERVAL) {
            turnCounter = 0;
            if (RANDOM.nextInt(100) < GROWTH_CHANCE) {
                location.setGround(new DeprecatedFleshyTreeMature());
            }
        }
    }

    private boolean isWorkerAdjacent(Location location) {
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.containsAnActor()
                    && dest.getActor().hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                return true;
            }
        }
        return false;
    }

    private void spawnUndead(Location location) {
        Undead undead = new Undead();

        List<Location> valid = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.canActorEnter(undead)) {
                valid.add(dest);
            }
        }

        if (valid.isEmpty()) return;

        Location spawnLoc = valid.get(RANDOM.nextInt(valid.size()));
        try {
            spawnLoc.addActor(undead);
            SpawnEffectManager.getInstance().onCreatureSpawned(undead, spawnLoc);
        } catch (GameEngineException e) {
            // tile became occupied between check and placement; skip silently
        }
    }
}
