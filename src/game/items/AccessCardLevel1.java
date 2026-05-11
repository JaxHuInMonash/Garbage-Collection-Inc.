package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ActorStatistics;
import game.ItemStatistics;
import game.capabilities.Ability;
import game.capabilities.Buyable;

/**
 * A small rectangular piece of plastic that holds entirely too much power
 * over your ability to walk through doors. Its primary function is to beep
 * happily when the player has clearance, and beep angrily when they don't
 *
 * @author Tommy
 */
public class AccessCardLevel1 extends Item implements Buyable {
    private static final int WEIGHT = 1;
    private static final int BUY_VALUE = 50;

    /**
     * Constructor.
     * <p>
     * Creates an AccessCard, makes it portable so it can be
     * picked up and dropped, and grants the ability ACCESS_LEVEL_1.
     */
    public AccessCardLevel1() {
        super("Access Card (Level 1)", '▤');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.BUY_VALUE, new BaseStatistic(BUY_VALUE));
        this.makePortable();
        this.enableAbility(Ability.ACCESS_LEVEL_1);
    }

    /**
     * Defines what happens when a AccessCardLevel1 is bought.
     * Adds the card to the actor's inventory
     *
     * @param actor the actor selling the cookies
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onBuy(Actor actor, GameMap map) {
        actor.getInventory().add(this);

        return actor + " bought " + this + " for " + getStatistic(ItemStatistics.BUY_VALUE) + " credits.";
    }

    /**
     * Provides the value of the item when bought
     * @return current buy value of the item
     */
    @Override
    public int getBuyValue() {
        return getStatistic(ItemStatistics.BUY_VALUE);
    }
}
