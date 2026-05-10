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

import java.util.Random;

/**
 * A small rectangular piece of plastic that holds entirely too much power
 * over your ability to walk through doors. Its primary function is to beep
 * happily when the player has clearance, and beep angrily when they don't
 *
 * @author Tommy
 */
public class AccessCardLevel3 extends Item implements Buyable {
    private static final int WEIGHT = 1;
    private static final int BUY_VALUE = 200;

    private static final int HIDDEN_FEE_PERCENTAGE_CHANCE = 50;
    private static final int HIDDEN_FEE = 50;

    private Random random = new Random();

    /**
     * Constructor.
     * <p>
     * Creates an AccessCard, makes it portable so it can be
     * picked up and dropped, and grants the ability ACCESS_LEVEL_3.
     */
    public AccessCardLevel3() {
        super("Access Card (Level 3)", '◐');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.BUY_VALUE, new BaseStatistic(BUY_VALUE));
        this.makePortable();
        this.enableAbility(Ability.ACCESS_LEVEL_1);
        this.enableAbility(Ability.ACCESS_LEVEL_2);
        this.enableAbility(Ability.ACCESS_LEVEL_3);
    }

    /**
     * Defines what happens when a AccessCardLevel3 is bought.
     * Adds the card to the actor's inventory,
     * however there is a percentage chance of {@value HIDDEN_FEE_PERCENTAGE_CHANCE}
     * to deduct {@value HIDDEN_FEE} from the actor
     *
     * @param actor the actor selling the cookies
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String buy(Actor actor, GameMap map) {
        String returnMessage = actor + " bought " + this + " for " + getStatistic(ItemStatistics.BUY_VALUE) + " credits";
        actor.getInventory().add(this);

        if (random.nextInt(100) < HIDDEN_FEE_PERCENTAGE_CHANCE) {
            actor.modifyStatistic(ActorStatistics.CREDITS, StatisticOperations.DECREASE, HIDDEN_FEE);
            return returnMessage + " - " + actor + " was also hit by a hidden fee of " + HIDDEN_FEE + " credits!!";
        }

        return returnMessage + ".";
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
