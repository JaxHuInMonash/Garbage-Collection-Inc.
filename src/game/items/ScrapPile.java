package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Scrap;

/**
 * A pile of scrap debris scattered around the facility.
 * <p>
 * Implements {@link Scrap} so the Meeple's NomadState can detect it via
 * {@code location.getItemsAs(Scrap.class)} and trigger a transition to
 * EngineerState. Unlike {@link UndeadCorpse}, Scrap cannot be transmuted
 * — it is purely an environmental trigger.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class ScrapPile extends Item implements Scrap {

    private static final int WEIGHT = 2;

    /** Constructor. */
    public ScrapPile() {
        super("Scrap Pile", 'S');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }
}
