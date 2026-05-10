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
public class AccessCardLevel1 extends Item implements Buyable {
    private static final int WEIGHT = 1;
    private static final int BUY_VALUE = 50;

    /**
     * Constructor.
     * Creates an AccessCard with weight 1, made portable so it can be
     * picked up and dropped, and grants the CAN_UNLOCK_DOORS ability.
     */
    public AccessCardLevel1() {
        super("Access Card (Level 1)", '▤');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.BUY_VALUE, new BaseStatistic(BUY_VALUE));
        this.makePortable();
        this.enableAbility(Ability.ACCESS_LEVEL_1);
    }

    @Override
    public String buy(Actor actor, GameMap map) {
        if (actor.getStatistic(ActorStatistics.CREDITS) < getStatistic(ItemStatistics.BUY_VALUE)) {
            return actor + " cannot afford this " + this + ". ("
                    + actor.getStatistic(ActorStatistics.CREDITS) + "/"
                    + getStatistic(ItemStatistics.BUY_VALUE) + ")";
        }

        actor.modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.DECREASE, getStatistic(ItemStatistics.BUY_VALUE));


        actor.getInventory().add(this);

        return actor + " bought " + this + " for " + getStatistic(ItemStatistics.BUY_VALUE) + " credits.";
    }
}
