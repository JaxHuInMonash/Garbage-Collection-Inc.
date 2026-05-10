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
 * @author Jax
 */
public class AccessCardLevel2 extends Item implements Buyable {
    private static final int WEIGHT = 1;
    private static final int BUY_VALUE = 100;

    /**
     * Constructor.
     * Creates an AccessCard with weight 1, made portable so it can be
     * picked up and dropped, and grants the CAN_UNLOCK_DOORS ability.
     */
    public AccessCardLevel2() {
        super("Access Card (Level 2)", 'α');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.BUY_VALUE, new BaseStatistic(BUY_VALUE));
        this.makePortable();
        this.enableAbility(Ability.ACCESS_LEVEL_1);
        this.enableAbility(Ability.ACCESS_LEVEL_2);
    }

    @Override
    public String buy(Actor actor, GameMap map) {
        actor.getInventory().add(this);
        actor.hurt(5);

        return actor + " bought " + this + " for " + getStatistic(ItemStatistics.BUY_VALUE) + " credits.";
    }

    @Override
    public int getBuyValue() {
        return getStatistic(ItemStatistics.BUY_VALUE);
    }
}
