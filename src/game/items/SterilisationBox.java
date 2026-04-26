package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Ability;

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
public class SterilisationBox extends Item {

    /**
     * Constructor.
     * Creates a SterilisationBox with weight 7, made portable, and grants
     * the HAS_STERILISATION ability.
     */
    public SterilisationBox() {
        super("Sterilisation Box", '▣');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(7));
        this.makePortable();
        this.enableAbility(Ability.HAS_STERILISATION);
    }
}
