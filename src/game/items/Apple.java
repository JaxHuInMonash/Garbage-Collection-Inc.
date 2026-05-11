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
 * @author Tommy
 */
public class Apple extends Item implements Consumable, Sellable {
    private static final int WEIGHT = 1;
    private static final int SELL_VALUE = 1;

    private static final int SELL_POISON_DAMAGE_PER_TURN = 2;
    private static final int SELL_POISON_DURATION = 2;

    private static final int CONSUME_STERILISATION_HEAL = 3;
    private static final int CONSUME_POISON_DAMAGE_PER_TURN = 1;
    private static final int CONSUME_POISON_DURATION = 5;

    /**
     * Constructor.
     */
    public Apple() {
        super("Apple", 'ó');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(SELL_VALUE));
        this.makePortable();
    }

    /**
     * Defines what happens when an Apple is sold.
     * Removes the apple from the actor's inventory.
     * if the player does not have the ability to sterilise,
     * they will take {@value SELL_POISON_DAMAGE_PER_TURN} damage
     * for {@value SELL_POISON_DURATION} turns.
     *
     * @param actor the actor selling the cookies
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String onSell(Actor actor, GameMap map) {
        actor.getInventory().remove(this);

        if (actor.hasAbility(Ability.HAS_STERILISATION)) {
            return actor + " sells a sterilised Apple - Neutralising its poisonous effects.";
        } else {
            actor.addStatus(new PoisonStatus(SELL_POISON_DAMAGE_PER_TURN, SELL_POISON_DURATION));
            return actor + " sells a toxic Apple and is poisoned! (" + SELL_POISON_DAMAGE_PER_TURN + " dmg/turn for " + SELL_POISON_DURATION + " turns)";
        }
    }

    /**
     * provides the value of the item when sold
     * @return sell value of the item
     */
    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
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
            actor.heal(CONSUME_STERILISATION_HEAL);
            return actor + " eats a sterilised Apple and heals " + CONSUME_STERILISATION_HEAL + " HP.";
        } else {
            actor.addStatus(new PoisonStatus(CONSUME_POISON_DAMAGE_PER_TURN, CONSUME_POISON_DURATION));
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
