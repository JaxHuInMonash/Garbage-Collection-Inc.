package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
/**
 * An interface for items that can be sold to the Supercomputer.
 * <p>
 * Implementing this interface allows any item to be sold using the
 * generic {@link game.actions.SellAction}, without requiring a separate
 * action class per item type. This follows the Interface Segregation
 * and Open/Closed principles.
 *@author zhengjun yang
 */
public interface Sellable {
    /**
     * Called when the item is sold to the Supercomputer.
     * Performs any side effects of selling and removes the item from
     * the actor's inventory.
     *
     * @param actor the actor selling the item
     * @param map   the map the actor is on
     * @return a description of the result
     */
    String sell(Actor actor, GameMap map);
}

