package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Stage 2 of the Fleshy Tree lifecycle. Displays as {@code v}.
 * <p>
 * An intermediate growth stage between {@link FleshyTreeSprout} and
 * {@link FleshyTreeMature}. The sapling has no proximity effect — it simply
 * grows. Every 25 turns, it has a 50% chance to mature into a
 * {@link FleshyTreeMature}. The growth counter resets on every 25-turn check
 * regardless of whether the roll succeeds, so growth is attempted at turns
 * 25, 50, 75, etc.
 * <p>
 * Actors cannot enter the sapling tile itself.
 *
 * @author Ethan
 */
public class FleshyTreeSapling extends Ground {

    private static final int GROWTH_INTERVAL = 25;
    private static final int GROWTH_CHANCE = 50;

    private int turnCounter;
    private static final Random RANDOM = new Random();

    /**
     * Constructor.
     */
    public FleshyTreeSapling() {
        super('v', "Fleshy Tree Sapling");
        this.turnCounter = 0;
    }

    /**
     * Actors cannot enter a sapling tile.
     *
     * @param actor the Actor attempting to enter
     * @return false always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn, increments the growth counter. Every 25 turns, rolls a 50%
     * chance to grow into a {@link FleshyTreeMature}. The counter resets
     * regardless of the roll outcome.
     *
     * @param location the location of this sapling
     */
    @Override
    public void tick(Location location) {
        turnCounter++;

        if (turnCounter >= GROWTH_INTERVAL) {
            turnCounter = 0; // reset regardless of roll outcome
            if (RANDOM.nextInt(100) < GROWTH_CHANCE) {
                location.setGround(new FleshyTreeMature());
            }
        }
    }
}
