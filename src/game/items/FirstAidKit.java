package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ItemStatistics;
import game.actions.ConsumeAction;
import game.capabilities.Buyable;
import game.capabilities.Consumable;

/**
 * A super useful first aid kit weighing 25 units. It can be used to permanently
 * increase a worker's maximum health by 1 point while simultaneously restoring
 * their health to full.
 * <p>
 * It requires a 20-turn cooldown between uses. Crucially, the cooldown timer
 * only progresses while the kit is actively being carried by a worker; if it is
 * left on the floor, the cooldown timer stops.
 *
 * @author Tommy
 */
public class FirstAidKit extends Item implements Consumable, Buyable {
    private static final int WEIGHT = 25;
    private static final int BUY_VALUE = 1000;

    private static final int HEALTH_INCREASE_ON_CONSUME = 1;
    private static final int MAX_COOLDOWN_TURNS = 20;
    private final BaseStatistic cooldown = new BaseStatistic(MAX_COOLDOWN_TURNS);

    /**
     * Constructor.
     * Creates a FirstAidKit with weight 25, made portable, and ready to use immediately.
     */
    public FirstAidKit() {
        super("First Aid Kit", '+');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.BUY_VALUE, new BaseStatistic(BUY_VALUE));
        this.makePortable();
    }

    /**
     * Defines what happens when a First Aid Kit is bought.
     * adds a First Aid Kit to the actor's inventory.
     *
     * @param actor the actor selling the First Aid Kit
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onBought(Actor actor, GameMap map) {
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

    /**
     * Kills the actor immediately for having insufficient credits when attempting to purchase
     *
     * @param actor actor that attempted to buy item with insufficient credits
     * @param map map the actor is on
     * @return the result of having insufficient credits
     */
    @Override
    public String onInsufficientCredits(Actor actor, GameMap map) {
        actor.hurt(actor.getMaximumStatistic(ActorStatistics.HEALTH)); // KILL PLAYER

        return " - " + actor + " was killed on the spot.";
    }

    /**
     * Defines what happens when the First Aid Kit is consumed.
     * Permanently adding {@value HEALTH_INCREASE_ON_CONSUME} max
     * health to the actor, with a cooldown of {@value MAX_COOLDOWN_TURNS} turns.
     *
     * @param actor the actor selling the cookies
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String consumedBy(Actor actor, GameMap map) {
        // Increase maximum health by 1
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, HEALTH_INCREASE_ON_CONSUME);

        // Start cooldown
        cooldown.update(cooldown.getMaximum());

        return actor + " uses the First Aid Kit. Maximum health increased by 1 and health restored to full! ("
                + actor.getStatistic(ActorStatistics.HEALTH) + "/"
                + actor.getMaximumStatistic(ActorStatistics.HEALTH) + ")";
    }

    /**
     * Ticks the cooldown timer while being carried by an actor.
     * The timer only progresses while the kit is in a worker's inventory.
     *
     * @param currentLocation the location of the actor carrying this item
     * @param actor           the actor carrying this item
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        if (cooldown.get() > 0) {
            cooldown.decrease(1);
        }
    }

    /**
     * Provides the use action to the owner when the kit is off cooldown.
     *
     * @param owner the actor carrying this kit
     * @param map   the map where the actor is
     * @return a list containing the UseFirstAidKitAction if ready, empty otherwise
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (cooldown.get() <= 0) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }
}
