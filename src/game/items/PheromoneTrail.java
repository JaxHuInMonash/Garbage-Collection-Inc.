package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.actions.ConsumeAction;
import game.capabilities.Consumable;

/**
 * A pheromone marker left by the Swarmling's ScoutState.
 * <p>
 * Workers who pick it up and consume it gain 5 HP — the chemical signals
 * provide a short burst of stimulation.
 */
public class PheromoneTrail extends Item implements Consumable {

    private static final int WEIGHT = 1;
    private static final int HEAL_AMOUNT = 5;

    /** Constructor. */
    public PheromoneTrail() {
        super("Pheromone Trail", '+');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.makePortable();
    }

    @Override
    public String consumedBy(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        actor.heal(HEAL_AMOUNT);
        return actor + " absorbs the Pheromone Trail and recovers " + HEAL_AMOUNT + " HP.";
    }

    @Override
    public String getConsumeMenuDescription(Actor actor) {
        return actor + " absorbs Pheromone Trail (+5 HP)";
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
