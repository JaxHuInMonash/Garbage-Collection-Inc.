package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ItemStatistics;
import game.actions.ConsumeAction;
import game.capabilities.Ability;
import game.capabilities.Consumable;
import game.capabilities.Sellable;

/**
 * A packet of cookies found on the moon's facility. Weighs 2 units and
 * contains 5 individual cookies (i.e., can be used five times).
 * <p>
 * Without a {@link SterilisationBox}, eating one permanently decreases the
 * consumer's maximum HP by 1. With sterilisation, each cookie heals 1 HP.
 * The item disappears from the inventory only after all 5 cookies are eaten.
 *
 * @author Jax
 * @author Tommy
 */
public class Cookies extends Item implements Consumable, Sellable {
    private static final int WEIGHT = 2;

    private static final int INITIAL_QUANTITY = 5;
    private static final int VALUE_PER_COOKIE = 1;

    private static final int SELL_DAMAGE_PER_COOKIE = 1;

    private static final int CONSUME_STERILISED_HEAL = 1;
    private static final int CONSUME_UNSTERILISED_DAMAGE = 1;

    /**
     * Constructor.
     */
    public Cookies() {
        super("Cookies", '◍');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.QUANTITY, new BaseStatistic(INITIAL_QUANTITY * VALUE_PER_COOKIE));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(this.getStatistic(ItemStatistics.QUANTITY) * VALUE_PER_COOKIE));
        this.makePortable();
    }

    /**
     * Defines what happens when a cookie is sold.
     * Removes the Cookies from the actor's inventory
     * and damages them by {@value SELL_DAMAGE_PER_COOKIE} per cookie
     *
     * @param actor the actor selling the cookies
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String sell(Actor actor, GameMap map) {
        actor.getInventory().remove(this);

        actor.hurt(SELL_DAMAGE_PER_COOKIE * getStatistic(ItemStatistics.QUANTITY));

        return this + " were sold for " + getStatistic(ItemStatistics.SELL_VALUE) + " credits, harming the player " + SELL_DAMAGE_PER_COOKIE + " for each cookie.";
    }

    /**
     * Provides the value of the item when sold
     * @return current sell value of the item
     */
    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
    }

    /**
     * Defines what happens when a cookie is consumed. Checks for
     * sterilisation to determine the effect. Removes the item from
     * the inventory once all cookies are eaten.
     *
     * @param actor the actor consuming a cookie
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String consumedBy(Actor actor, GameMap map) {
        modifyStatistic(ItemStatistics.QUANTITY, StatisticOperations.DECREASE, 1);
        modifyStatistic(ItemStatistics.SELL_VALUE, StatisticOperations.UPDATE, getStatistic(ItemStatistics.QUANTITY) * VALUE_PER_COOKIE);

        String result;
        if (actor.hasAbility(Ability.HAS_STERILISATION)) {
            actor.heal(CONSUME_STERILISED_HEAL);
            result = actor + " eats a sterilised cookie and heals 1 HP.";
        } else {
            actor.hurt(CONSUME_UNSTERILISED_DAMAGE);
            result = actor + " eats a toxic cookie. Maximum HP decreased by 1!";
        }

        if (getStatistic(ItemStatistics.QUANTITY) <= 0) {
            actor.getInventory().remove(this);
            result += " The cookie packet is now empty and discarded.";
        } else {
            result += " (" + getStatistic(ItemStatistics.QUANTITY) + " cookies remaining)";
        }

        return result;
    }

    /**
     * Provides the correct name when displaying the name
     * @return Singular or plural depending on quantity
     */
    @Override
    public String toString() {
        return "Cookie" + (getStatistic(ItemStatistics.QUANTITY) == 1 ? "" : "s");
    }

    /**
     * Offers the consume action while cookies remain.
     *
     * @param owner the actor carrying these cookies
     * @param map   the current game map
     * @return a list containing the ConsumeAction if cookies remain
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (getStatistic(ItemStatistics.QUANTITY) > 0) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

}
