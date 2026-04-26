package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;

/**
 * A massive, incredibly heavy piece of archaic junk, taking up 30 units
 * of weight in the worker's inventory.
 *
 * @author Jax
 */
public class CRTMonitor extends Item {

    /**
     * Constructor.
     */
    public CRTMonitor() {
        super("CRT Monitor", '◙');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(30));
        this.makePortable();
    }
}
