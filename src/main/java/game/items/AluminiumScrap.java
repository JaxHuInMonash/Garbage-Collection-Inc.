package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Sellable;

/**
 * A chunk of salvaged aluminium that can be deposited at the Supercomputer.
 * Displays as {@code S}.
 *
 * @author Ethan
 */
public class AluminiumScrap extends Item implements Sellable {

    private static final int WEIGHT = 1;
    private static final int SELL_VALUE = 5;

    /** Constructor. */
    public AluminiumScrap() {
        super("Aluminium Scrap", 'S');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(SELL_VALUE));
        this.makePortable();
    }

    @Override
    public String onSold(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        return actor + " deposited Aluminium Scrap for " + SELL_VALUE + " credits.";
    }

    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
    }
}
