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
    private final Random random = new Random();
    /**
     * Constructor.
     * Creates a SterilisationBox with weight 7, made portable, and grants
     * the HAS_STERILISATION ability.
     */
    public SterilisationBox() {
        super("Sterilisation Box", '▣');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(7));
        this.addNewStatistic(ItemStatistics.BUY_VALUE, new BaseStatistic(750));
        this.makePortable();
        this.enableAbility(Ability.HAS_STERILISATION);
    }

    @Override
    public String buy(Actor actor, GameMap map) {
        if (actor.getStatistic(ActorStatistics.CREDITS) < getStatistic(ItemStatistics.BUY_VALUE)) {
            return actor + " cannot afford this " + this + ". ("
                    + actor.getStatistic(ActorStatistics.CREDITS) + "/"
                    + getStatistic(ItemStatistics.BUY_VALUE) + ")";
        }

        List<Item> items = actor.getInventory().getItems();
        Item randomItem = items.get(random.nextInt(items.size()));

        actor.getInventory().remove(randomItem);

        actor.getInventory().add(this);

        return actor + " bought " + this + " for " + getStatistic(ItemStatistics.BUY_VALUE) + " credits." +
                "- However due to radiation, " + randomItem + " was destroyed." ;
    }
}
