package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Sellable;

/**
 * A heavy industrial fan salvaged from the facility's ventilation system.
 * Can be deposited at the Supercomputer. Displays as {@code F}.
 *
 * @author Ethan
 */
public class IndustrialFan extends Item implements Sellable {

    private static final int WEIGHT = 2;
    private static final int SELL_VALUE = 10;

    /** Constructor. */
    public IndustrialFan() {
        super("Industrial Fan", 'F');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(SELL_VALUE));
        this.makePortable();
    }

    @Override
    public String onSold(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        return actor + " deposited Industrial Fan for " + SELL_VALUE + " credits.";
    }

    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
    }
}
