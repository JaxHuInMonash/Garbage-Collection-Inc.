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
 * A mysterious alien artifact that can teleport its user to a random
 * location on the current map. Displays as {@code ◈}.
 * <p>
 * Using the cube corrupts all adjacent ground tiles at the source into
 * {@link game.grounds.ToxicWaste}. The cube can also be sold at the
 * Supercomputer for {@value CREDIT_VALUE} credits, which causes an
 * Undead to materialise nearby.
 *
 * @author zhengjun Yang
 */
public class AlienCube extends Item implements Sellable {

    private static final int CREDIT_VALUE = 25;

    /**
     * Constructor.
     */
    public AlienCube() {
        super("Alien Cube", '◈');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(2));
        this.addNewStatistic(ItemStatistics.SELL_VALUE, new BaseStatistic(CREDIT_VALUE));
        this.makePortable();
    }

    /**
     * Called when the cube is sold to the Supercomputer.
     * Removes the cube from the actor's inventory.
     *
     * @param actor the actor selling the cube
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String onSold(Actor actor, GameMap map) {
        actor.getInventory().remove(this);
        return actor + " sold the Alien Cube for " + CREDIT_VALUE + " credits — an Undead materialises nearby!";
    }

    /**
     * Returns the credit value when sold.
     *
     * @return the sell value in credits
     */
    @Override
    public int getSellValue() {
        return getStatistic(ItemStatistics.SELL_VALUE);
    }

    /**
     * Provides actions to the owner: use the cube to teleport, or sell it.
     *
     * @param owner the actor carrying this cube
     * @param map   the current game map
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new UseAlienCubeAction(this));
        actions.add(new SellAction(this));
        return actions;
    }
}
