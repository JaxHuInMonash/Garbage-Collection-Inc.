package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Ability;
import game.capabilities.Buyable;

import java.util.List;
import java.util.Random;

/**
 * A sterilisation box weighing 7 units that provides the necessary function
 * of sterilising any consumables, including items and grounds, found on the
 * moon's facility.
 * <p>
 * While carried, it grants the {@link Ability#HAS_STERILISATION} ability to
 * the carrier, which purifies toxic consumables and environmental hazards.
 *
 * @author Jax
 * @author Tommy
 */
public class SterilisationBox extends Item implements Buyable {
    private static final int WEIGHT = 7;
    private static final int BUY_VALUE = 750;

    private final Random random = new Random();
    /**
     * Constructor.
     * Creates a SterilisationBox with weight 7, made portable, and grants
     * the HAS_STERILISATION ability.
     */
    public SterilisationBox() {
        super("Sterilisation Box", '▣');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.BUY_VALUE, new BaseStatistic(BUY_VALUE));
        this.makePortable();
        this.enableAbility(Ability.HAS_STERILISATION);
    }

    /**
     * Places this box into the buyer's inventory. The polymorphic
     * {@code Inventory.add()} handles any weight check.
     */
    @Override
    public boolean attemptAcquireBy(Actor actor) {
        return actor.getInventory().add(this);
    }

    /**
     * Side effects after a Sterilisation Box is successfully acquired:
     * radiation from the box destroys one random item from the buyer's
     * inventory (which may be the box itself).
     *
     * @param actor the actor buying the Sterilisation Box
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onBought(Actor actor, GameMap map) {
        List<Item> items = actor.getInventory().getItems();
        Item randomItem = items.get(random.nextInt(items.size()));

        actor.getInventory().remove(randomItem);

        return actor + " bought " + this + " for " + getStatistic(ItemStatistics.BUY_VALUE) + " credits - " + randomItem + " was destroyed due to radiation.";
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
