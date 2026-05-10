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
 * A piece of ancient technology. Extremely lightweight at 1 unit.
 *
 * @author Jax
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

    @Override
    public String sell(Actor actor, GameMap map) {
        actor.modifyStatistic(game.ActorStatistics.CREDITS, StatisticOperations.INCREASE, getStatistic(ItemStatistics.SELL_VALUE));
        actor.getInventory().remove(this);

        if (random.nextInt(100) < GLITCH_PERCENTAGE_CHANCE) {
            actor.modifyStatistic(game.ActorStatistics.CREDITS, StatisticOperations.DECREASE, GLITCH_VALUE);
        }

        return this + " was sold for " + getStatistic(ItemStatistics.SELL_VALUE) + " credits";
    }
}
