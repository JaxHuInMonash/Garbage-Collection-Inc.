package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ItemStatistics;
import game.capabilities.Sellable;

import java.util.Random;

/**
 * A piece of ancient technology. Extremely lightweight
 *
 * @author Tommy
 */
public class FloppyDisk extends Item implements Sellable {
    private static final int WEIGHT = 1;
    private static final int SELL_VALUE = 1;

    private static final int GLITCH_PERCENTAGE_CHANCE = 50;
    private static final int GLITCH_VALUE = 50;
    Random random = new Random();

    /**
     * Constructor.
     */
    public FloppyDisk() {
        super("Floppy Disk", '⊟');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(SELL_VALUE));
        this.makePortable();
    }

    /**
     * Defines what happens when a Floppy Disk is sold.
     * Removes the Floppy Disk from the actor's inventory.
     * There is however a {@value GLITCH_PERCENTAGE_CHANCE}
     * percentage chance for the Floppy disk to glitch out,
     * deducting an additional {@value GLITCH_VALUE} credits.
     *
     * @param actor the actor selling the cookies
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onSell(Actor actor, GameMap map) {
        actor.getInventory().remove(this);

        if (random.nextInt(100) < GLITCH_PERCENTAGE_CHANCE) {
            actor.modifyStatistic(game.ActorStatistics.CREDITS, StatisticOperations.DECREASE, GLITCH_VALUE);
        }

        return this + " was sold for " + getStatistic(ItemStatistics.SELL_VALUE) + " credits";
    }

    /**
     * Provides the value of the item when sold
     * @return current sell value of the item
     */
    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
    }
}
