package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;

/**
 * A piece of ancient technology. Extremely lightweight at 1 unit.
 *
 * @author Jax
 */
public class FloppyDisk extends Item {

    /**
     * Constructor.
     */
    public FloppyDisk() {
        super("Floppy Disk", '⊟');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
    }
}
