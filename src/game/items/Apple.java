package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ItemStatistics;
import game.actions.ConsumeAction;
import game.capabilities.Ability;
import game.capabilities.Consumable;
import game.capabilities.Sellable;
import game.statuses.PoisonStatus;

/**
 * A spoiled apple found on the moon's facility. Weighs 1 unit.
 * <p>
 * Without a {@link SterilisationBox}, eating it poisons the consumer
 * (1 damage per turn for 5 turns). With sterilisation, it heals 3 HP.
 * The apple disappears from the inventory once consumed.
 *
 * @author Jax
 */
public class Apple extends Item implements Consumable, Sellable {

    /**
     * Constructor.
     */
    public Apple() {
        super("Apple", 'ó');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.addNewStatistic(ItemStatistics.VALUE, new BaseStatistic(1));
        this.makePortable();
    }

    @Override
    public String sell(Actor actor, GameMap map) {
        actor.modifyStatistic(game.ActorStatistics.CREDITS, StatisticOperations.INCREASE, getStatistic(ItemStatistics.VALUE));
        actor.getInventory().remove(this);

        if (actor.hasAbility(Ability.HAS_STERILISATION)) {
            return actor + " sells a sterilised Apple - Neutralising its poisonous effects.";
        } else {
            actor.addStatus(new PoisonStatus(2, 2));
            return actor + " sells a toxic Apple and is poisoned! (2 dmg/turn for 2 turns)";
        }
    }

    /**
     * Defines what happens when the apple is consumed. Checks for
     * sterilisation to determine the effect, then removes itself
     * from the actor's inventory.
     *
     * @param actor the actor consuming this apple
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String consumedBy(Actor actor, GameMap map) {
        actor.getInventory().remove(this);

        if (actor.hasAbility(Ability.HAS_STERILISATION)) {
            actor.heal(3);
            return actor + " eats a sterilised Apple and heals 3 HP.";
        } else {
            actor.addStatus(new PoisonStatus(1, 5));
            return actor + " eats a toxic Apple and is poisoned! (1 dmg/turn for 5 turns)";
        }
    }

    /**
     * Offers the consume action while the apple is being carried.
     *
     * @param owner the actor carrying this apple
     * @param map   the current game map
     * @return a list containing the ConsumeAction
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new ConsumeAction(this));
        return actions;
    }
}
