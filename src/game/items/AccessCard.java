package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Ability;

/**
 * A small rectangular piece of plastic that holds entirely too much power
 * over your ability to walk through doors. Its primary function is to beep
 * happily when the player has clearance, and beep angrily when they don't.
 * <p>
 * The AccessCard weighs 1 unit and grants the {@link Ability#CAN_UNLOCK_DOORS}
 * ability to whoever carries it, enabling them to unlock doors in the facility.
 *
 * @author Jax
 */
public class AccessCard extends Item {

    /**
     * Constructor.
     * Creates an AccessCard with weight 1, made portable so it can be
     * picked up and dropped, and grants the CAN_UNLOCK_DOORS ability.
     */
    public AccessCard() {
        super("Access Card", '▤');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
        this.enableAbility(Ability.CAN_UNLOCK_DOORS);
    }
}
