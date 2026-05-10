package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ActorStatistics;
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

    @Override
    public String buy(Actor actor, GameMap map) {
        actor.getInventory().add(this);

        List<Item> items = actor.getInventory().getItems();
        Item randomItem = items.get(random.nextInt(items.size()));

        actor.getInventory().remove(randomItem);

        return actor + " bought " + this + " for " + getStatistic(ItemStatistics.BUY_VALUE) + " credits - " + randomItem + " was destroyed due to radiation." ;
    }

    @Override
    public int getBuyValue() {
        return getStatistic(ItemStatistics.BUY_VALUE);
    }
}
