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
 * <p>
 * The {@link game.actions.SellAction} handles the credit transfer via
 * {@link #getSellValue()}; implementations only need to define any
 * additional side effects in {@link #onSold(Actor, GameMap)}.
 *
 * @author zhengjun Yang
 */
public interface Sellable {

    /**
     * Called when the item is sold to the Supercomputer.
     * Performs any side effects of selling (e.g., removing the item,
     * applying damage, spawning creatures). The credit transfer is
     * handled by {@link game.actions.SellAction}.
     *
     * @param actor the actor selling the item
     * @param map   the map the actor is on
     * @return a description of the result
     */
    String onSold(Actor actor, GameMap map);

    /**
     * Returns the credit value this item yields when sold.
     *
     * @return the sell value in credits
     */
    int getSellValue();
}
