package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;

import java.util.Random;

/**
 * A Level 3 access card. Weighs 3 units and represented by {@code ◐}.
 * <p>
 * Due to predatory pricing algorithms, there is a
 * {@value HIDDEN_FEE_PERCENTAGE_CHANCE}% chance the Supercomputer will hit the
 * buyer with a hidden fee, deducting an additional {@value HIDDEN_FEE} credits
 * on top of the purchase price.
 *
 * @author Tommy
 */
public class AccessCardLevel3 extends AccessCard {

    private static final int CLEARANCE = 3;
    private static final int WEIGHT = 3;
    private static final int BUY_VALUE = 200;

    private static final int HIDDEN_FEE_PERCENTAGE_CHANCE = 50;
    private static final int HIDDEN_FEE = 50;

    private final Random random = new Random();

    /**
     * Constructor.
     */
    public AccessCardLevel3() {
        super("Access Card (Level 3)", '◐', CLEARANCE, WEIGHT, BUY_VALUE);
    }

    /**
     * Places this card into the buyer's inventory. The polymorphic
     * {@code Inventory.add()} handles any weight check.
     */
    @Override
    public boolean attemptAcquireBy(Actor actor) {
        return actor.getInventory().add(this);
    }

    /**
     * Side effects after a Level 3 card is successfully acquired: with a
     * {@value HIDDEN_FEE_PERCENTAGE_CHANCE}% chance deducts an additional
     * {@value HIDDEN_FEE} credits as a hidden fee.
     *
     * @param actor the actor buying the card
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onBought(Actor actor, GameMap map) {
        String message = actor + " bought " + this + " for " + getBuyValue() + " credits";

        if (random.nextInt(100) < HIDDEN_FEE_PERCENTAGE_CHANCE) {
            actor.modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.DECREASE, HIDDEN_FEE);
            return message + " — hit by a hidden fee of " + HIDDEN_FEE + " extra credits!";
        }

        return message + ".";
    }
}
