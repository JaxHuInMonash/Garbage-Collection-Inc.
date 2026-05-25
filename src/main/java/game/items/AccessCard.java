package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Ability;
import game.capabilities.Buyable;

/**
 * An abstract AccessCard class that is intended to be extended,
 * allowing cards to assign their own clearance, weight, and buy value
 * on instantiation.
 * <p>
 * All concrete cards are {@link Buyable}, so {@code getBuyValue()} and the
 * {@link ItemStatistics#BUY_VALUE} statistic are owned by this base class
 * rather than duplicated across every subclass.
 *
 * @author Jax
 * @author Tommy
 */
public abstract class AccessCard extends Item implements Buyable {

    /**
     * Constructor.
     *
     * @param name        Name of the AccessCard
     * @param displayChar character to represent the AccessCard on the map
     * @param clearance   the integer clearance level the card provides
     * @param weight      the weight of the card in inventory units
     * @param buyValue    the credit cost when purchased from the Supercomputer
     */
    public AccessCard(String name, Character displayChar, int clearance, int weight, int buyValue) {
        super(name, displayChar);
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(weight));
        this.addNewStatistic(ItemStatistics.CLEARANCE, new BaseStatistic(clearance));
        this.addNewStatistic(ItemStatistics.BUY_VALUE, new BaseStatistic(buyValue));
        this.enableAbility(Ability.CAN_UNLOCK_DOORS);
        this.makePortable();
    }

    /**
     * Provides the credit cost of this card.
     *
     * @return current buy value of the card
     */
    @Override
    public int getBuyValue() {
        return getStatistic(ItemStatistics.BUY_VALUE);
    }
}
