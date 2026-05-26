package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Sellable;

/**
 * A mysterious alien relic of unknown origin. Highly valued by the Supercomputer
 * for analysis. Can be deposited for credits. Displays as {@code X}.
 *
 * @author Ethan
 */
public class AlienArtifact extends Item implements Sellable {

    private static final int WEIGHT = 1;
    private static final int SELL_VALUE = 15;

    /** Constructor. */
    public AlienArtifact() {
        super("Alien Artifact", 'X');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(SELL_VALUE));
        this.makePortable();
    }

    @Override
    public String onSold(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        return actor + " deposited Alien Artifact for " + SELL_VALUE + " credits.";
    }

    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
    }
}
