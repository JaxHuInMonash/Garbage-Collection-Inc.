package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;

public abstract class AccessCard extends Item {
    private static final int WEIGHT = 1;

    public AccessCard(String name, Character displayChar, int clearance) {
        super(name, displayChar);
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.CLEARANCE, new BaseStatistic(clearance));

        this.makePortable();
    }
}
