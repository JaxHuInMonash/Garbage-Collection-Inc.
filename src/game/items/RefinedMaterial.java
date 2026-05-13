package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ItemStatistics;
import game.actions.ConsumeAction;
import game.capabilities.Consumable;

/**
 * Processed remains produced by the Meeple's TransmuteAction from an
 * {@link UndeadCorpse}. When consumed by a Worker, permanently raises
 * their maximum HP by 20.
 */
public class RefinedMaterial extends Item implements Consumable {

    private static final int WEIGHT = 2;
    private static final int MAX_HP_BONUS = 20;

    /** Constructor. */
    public RefinedMaterial() {
        super("Refined Material", 'r');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }

    @Override
    public String consumedBy(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, MAX_HP_BONUS);
        actor.heal(MAX_HP_BONUS);
        return actor + " integrates Refined Material — max HP permanently increased by " + MAX_HP_BONUS + ".";
    }

    @Override
    public String getConsumeMenuDescription(Actor actor) {
        return actor + " integrates Refined Material (+20 max HP)";
    }

    @Override
    public void removeFromGround(Location location) {
        location.removeItem(this);
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }
}
