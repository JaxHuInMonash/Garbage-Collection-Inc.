package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Scrap;
import game.capabilities.Transmutable;

/**
 * The remains of a slain Undead creature, dropped on its death tile.
 * <p>
 * Implements {@link Scrap} so the Meeple's NomadState can detect it via
 * {@code location.getItemsAs(Scrap.class)} and trigger a transition to
 * EngineerState. Implements {@link Transmutable} so TransmuteAction can
 * find and convert it into {@link RefinedMaterial} via
 * {@code location.getItemsAs(Transmutable.class)} — no instanceof needed.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class UndeadCorpse extends Item implements Scrap, Transmutable {

    private static final int WEIGHT = 3;

    /** Constructor. */
    public UndeadCorpse() {
        // Display char 'c' chosen to avoid clashing with Flask's 'u'.
        super("Undead Corpse", 'c');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }
}
