package game.grounds;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.ScrapSnatcher;
import game.capabilities.Ability;
import game.spawning.SpawnEffectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stage 2 (mature) of the Fleshy Tree lifecycle on the <em>99-Deprecated</em> map.
 * Displays as {@code Y}.
 * <p>
 * Differences from the standard {@link FleshyTreeMature}:
 * <ol>
 *   <li>Spawns a {@link ScrapSnatcher} (not an Undead) when a worker is adjacent.</li>
 *   <li>Every 35 turns there is a 50% chance it grows into a
 *       {@link FleshyMonolith}.</li>
 * </ol>
 * The single-threaded flora rule applies: at most one action per turn.
 * <p>
 * Actors cannot enter this tile.
 *
 * @author Ethan
 */
public class DeprecatedFleshyTreeMature extends Ground {

    private static final int MONOLITH_INTERVAL = 35;
    private static final int MONOLITH_CHANCE = 50;

    private int turnCounter;
    private static final Random RANDOM = new Random();

    /** Constructor. */
    public DeprecatedFleshyTreeMature() {
        super('Y', "Fleshy Tree (Mature, Deprecated)");
        this.turnCounter = 0;
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn, performs at most one action:
     * spawn a ScrapSnatcher if a worker is adjacent; then check for growth
     * into a {@link FleshyMonolith} every 35 turns.
     *
     * @param location the location of this mature tree
     */
    @Override
    public void tick(Location location) {
        turnCounter++;

        if (isWorkerAdjacent(location)) {
            spawnSnatcher(location);
            return; // single-threaded flora rule
        }

        if (turnCounter >= MONOLITH_INTERVAL) {
            turnCounter = 0;
            if (RANDOM.nextInt(100) < MONOLITH_CHANCE) {
                location.setGround(new FleshyMonolith());
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

    private void spawnSnatcher(Location location) {
        ScrapSnatcher snatcher = new ScrapSnatcher();

        List<Location> valid = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.canActorEnter(snatcher)) {
                valid.add(dest);
            }
        }

        if (valid.isEmpty()) return;

        Location spawnLoc = valid.get(RANDOM.nextInt(valid.size()));
        try {
            spawnLoc.addActor(snatcher);
            SpawnEffectManager.getInstance().onCreatureSpawned(snatcher, spawnLoc);
        } catch (GameEngineException e) {
            // tile became occupied between check and placement; skip silently
        }
    }
}
