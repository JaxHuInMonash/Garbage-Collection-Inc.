package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;

/**
 * An abstract AccessCard class that is intended to be extended,
 * allowing cards to assign their own clearances on instantiation
 *
 * @author Tommy
 */
public abstract class AccessCard extends Item {
    private static final int WEIGHT = 1;

    /**
     * Constructor. Assigns a clearances provided in the parameters
     * as a statistic and makes the Item able to be picked up and dropped.
     *
     * @param name Name of the AccessCard
     * @param displayChar character to represent the AccessCard on the map
     * @param clearance the integer number level of clearance the card provides
     */
    public AccessCard(String name, Character displayChar, int clearance) {
        super(name, displayChar);
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.CLEARANCE, new BaseStatistic(clearance));

        this.makePortable();
    }
}
