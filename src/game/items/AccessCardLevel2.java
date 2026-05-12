package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Buyable;

/**
 * A small rectangular piece of plastic that holds entirely too much power
 * over your ability to walk through doors. Its primary function is to beep
 * happily when the player has clearance, and beep angrily when they don't
 *
 * @author Tommy
 */
public class AccessCardLevel2 extends AccessCard implements Buyable {
    private static final int CLEARANCE = 2;
    private static final int BUY_VALUE = 100;
    private static final int DAMAGE_ON_BUY = 5;

    /**
     * Constructor.
     * <p>
     * Creates an AccessCard, makes it portable so it can be
     * picked up and dropped, and grants the ability ACCESS_LEVEL_2.
     */
    public AccessCardLevel2() {
        super("Access Card (Level 2)", 'α', CLEARANCE);
        this.addNewStatistic(ItemStatistics.BUY_VALUE, new BaseStatistic(BUY_VALUE));
    }

    /**
     * Defines what happens when a AccessCardLevel2 is bought.
     * Adds the card to the actor's inventory and
     * damages the actor by {@value DAMAGE_ON_BUY}
     *
     * @param actor the actor selling the cookies
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onBought(Actor actor, GameMap map) {
        actor.getInventory().add(this);
        actor.hurt(DAMAGE_ON_BUY);

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


