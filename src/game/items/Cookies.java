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
import game.capabilities.Infectable;
import game.statuses.CookieInfectedStatus;

/**
 * A packet of cookies found on the moon's facility. Weighs 2 units and
 * contains 5 individual cookies (i.e., can be used five times).
 * <p>
 * Without a {@link SterilisationBox}, eating one permanently decreases the
 * consumer's maximum HP by 1. With sterilisation, each cookie heals 1 HP.
 * The item disappears from the inventory only after all 5 cookies are eaten.
 *
 * @author Jax
 */
public class Cookies extends Item implements Consumable, Infectable {

    private static final int MAX_COOKIES = 5;
    private int remainingCookies;

    /**
     * Constructor.
     */
    public Cookies() {
        super("Cookies", '◍');
        this.remainingCookies = MAX_COOKIES;
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(2));
        this.makePortable();
    }

    /**
     * Returns the number of cookies remaining.
     *
     * @return remaining cookie count
     */
    public int getRemainingCookies() {
        return remainingCookies;
    }

    /**
     * Reduces the remaining cookies by the specified amount.
     * Cookies cannot go below zero.
     *
     * @param amount the number of cookies to remove
     */
    public void reduceCookies(int amount) {
        this.remainingCookies = Math.max(0, this.remainingCookies - amount);
    }

    /**
     * Applies an infection status to these cookies. Each turn, one cookie
     * is consumed and a Parasite is spawned nearby.
     *
     * @param infector the Parasite that caused the infection
     * @param map      the current game map
     */
    @Override
    public void onInfected(Actor infector, GameMap map) {
        this.addStatus(new CookieInfectedStatus(this));
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
        remainingCookies--;

        String result;
        if (actor.hasAbility(Ability.HAS_STERILISATION)) {
            actor.heal(1);
            result = actor + " eats a sterilised cookie and heals 1 HP.";
        } else {
            actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.DECREASE, 1);
            result = actor + " eats a toxic cookie. Maximum HP decreased by 1!";
        }

        if (remainingCookies <= 0) {
            actor.getInventory().remove(this);
            result += " The cookie packet is now empty and discarded.";
        } else {
            result += " (" + remainingCookies + " cookies remaining)";
        }

        return result;
    }

    @Override
    public Item asItem() {
        return this;
    }

    @Override
    public String getConsumeMenuDescription(Actor actor) {
        return actor + " eats a cookie (" + remainingCookies + " remaining)";
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
        if (remainingCookies > 0) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }
}
