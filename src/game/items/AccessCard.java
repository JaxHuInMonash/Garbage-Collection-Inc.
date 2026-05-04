package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.capabilities.Ability;

public class AccessCard extends Item {

    private final int clearanceLevel;

    public AccessCard(int clearanceLevel) {
        super("Access Card (Level " + clearanceLevel + ")", '▤');
        this.clearanceLevel = clearanceLevel;
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
        this.enableAbility(Ability.CAN_UNLOCK_DOORS);
    }

    public int getClearanceLevel() {
        return clearanceLevel;
    }
}
