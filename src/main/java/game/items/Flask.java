package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.ItemStatistics;
import game.actions.ConsumeAction;
import game.capabilities.Consumable;

/**
 * Due to severe budget cuts, the flask is only permitted to hold five (5)
 * mouthfuls of liquid per deployment. Employees are reminded not to consume
 * all five charges in a panic during a single encounter.
 * <p>
 * The Flask weighs 3 units. Each use heals 1 point of health.
 * Once depleted, the empty flask remains in the worker's inventory.
 *
 * @author Jax
 */
public class Flask extends Item implements Consumable {

    private static final int MAX_USES = 5;
    private int remainingUses;
    private static final int WEIGHT = 3;
    private static final int INITIAL_QUANTITY = 5;
    private static final int CONSUME_HEAL = 1;

    /**
     * Constructor.
     * Creates a Flask with weight 3, made portable, and 5 uses available.
     */
    public Flask() {
        super("Flask", 'u');
        this.remainingUses = MAX_USES;
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.addNewStatistic(ItemStatistics.QUANTITY, new BaseStatistic(INITIAL_QUANTITY)); // Uses
        this.makePortable();
    }

    /**
     * Returns the number of remaining uses for this flask.
     *
     * @return the number of remaining uses
     */
    public int getRemainingUses() {
        return remainingUses;
    }

    /**
     * Defines what happens when the flask is consumed: heals 1 HP and
     * decrements remaining uses. The empty flask stays in the inventory.
     *
     * @param actor the actor consuming this flask
     * @param map   the game map the actor is on
     * @return a description of the result
     */
    @Override
    public String consumedBy(Actor actor, GameMap map) {
        if (getStatistic(ItemStatistics.QUANTITY) > 0) {
            modifyStatistic(ItemStatistics.QUANTITY, StatisticOperations.DECREASE, 1);
            remainingUses--;
            actor.heal(CONSUME_HEAL);
            return actor + " drinks from the Flask, healing " + CONSUME_HEAL + " point. (" + getStatistic(ItemStatistics.QUANTITY) + " uses remaining)";
        }
        return actor + "'s Flask is empty.";
    }

    @Override
    public void removeFromGround(Location location) {
        location.removeItem(this);
    }

    @Override
    public String getConsumeMenuDescription(Actor actor) {
        return actor + " drinks from Flask (" + getStatistic(ItemStatistics.QUANTITY) + " uses remaining)";
    }

    /**
     * Provides the consume action to the owner when the flask still has uses remaining.
     *
     * @param owner the actor carrying this flask
     * @param map   the map where the actor is
     * @return a list containing the ConsumeAction if uses remain, empty otherwise
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (getStatistic(ItemStatistics.QUANTITY) > 0) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }
}
