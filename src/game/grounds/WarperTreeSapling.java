package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Stage 1 of the Warper Tree lifecycle. Displays as {@code w}.
 * <p>
 * A sapling whose roots have begun to intertwine with the moon's unstable
 * teleportation grid. It has no proximity effect at this stage — it simply
 * grows. Every 20 turns, it has a 25% chance to mature into a
 * {@link WarperTreeMature}. The growth counter resets on every 20-turn check
 * regardless of whether the roll succeeds, so growth is attempted at turns
 * 20, 40, 60, etc.
 * <p>
 * Actors cannot enter the sapling tile itself
 *
 * @author Ethan
 */
public class WarperTreeSapling extends Ground {

    private static final int GROWTH_INTERVAL = 20;
    private static final int GROWTH_CHANCE = 25;

    private int turnCounter;
    private final Random random;

    /**
     * Constructor.
     */
    public WarperTreeSapling() {
        super('w', "Warper Tree Sapling");
        this.turnCounter = 0;
        this.random = new Random();
    }

    /**
     * Actors cannot enter a warper sapling tile.
     *
     * @param actor the Actor attempting to enter
     * @return false always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn, increments the growth counter. Every 20 turns, rolls a 25%
     * chance to grow into a {@link WarperTreeMature}. The counter resets
     * regardless of the roll outcome.
     *
     * @param location the location of this sapling
     */
    @Override
    public void tick(Location location) {
        turnCounter++;

        if (turnCounter >= GROWTH_INTERVAL) {
            turnCounter = 0; // reset regardless of roll outcome
            if (random.nextInt(100) < GROWTH_CHANCE) {
                location.setGround(new WarperTreeMature());
            }
        }
    }
}
