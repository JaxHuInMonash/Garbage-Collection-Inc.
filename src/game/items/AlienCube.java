package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.actions.UseAlienCubeAction;
import game.capabilities.Sellable;
import game.actions.SellAction;
/**
 * A teleportation device installed in armoured ships. Displays as {@code Φ}.
 * <p>
 * Supports multiple destination locations across maps. Using the tube burns
 * the surroundings of the destination for 2 turns. There is a 50% chance the
 * device malfunctions, teleporting the worker to a random location within the
 * destination map instead of the intended spot. The burning effect still occurs
 * at the random destination. If the destination is occupied by another actor,
 * the worker is automatically relocated to a random empty tile.
 * <p>
 * Must be added to the ship in both the 99-deprecated and 20-overflow maps.
 *
 *@author zhengjun yang
 */
public class AlienCube extends Item implements Sellable {

    private static final int CREDIT_VALUE = 25;
    /**
     * Constructor.
     */
    public AlienCube() {
        super("Alien Cube", '◈');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(2));
        this.makePortable();
    }
    /**
     * Sells the cube to the Supercomputer for 25 credits.
     * Removes the cube from the actor's inventory upon sale.
     *
     * @param actor the actor selling the cube
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String sell(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        return actor + " sold the Alien Cube for " + CREDIT_VALUE + " credits — an Undead materialises nearby!";
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new UseAlienCubeAction(this, 0));
        actions.add(new UseAlienCubeAction(this, 1));
        actions.add(new UseAlienCubeAction(this, 2));
        actions.add(new SellAction(this));
        return actions;
    }
}
