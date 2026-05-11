package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * The interface for any object that can be sold by an actor
 *
 * @author Tommy
 */
public interface Sellable {
    /**
     * Defines what happens when this Sellable is sold by an actor.
     * The implementation handles all effects (healing, poisoning, removal
     * from inventory, etc.).
     *
     * @param actor actor that sold the Sellable
     * @param map map the Sellable was sold on
     * @return resultant message of selling the Sellable
     */
    String onSold(Actor actor, GameMap map);

    /**
     * Provides the value of the Sellable
     * @return integer value of the Sellable's sell value
     */
    int getSellValue();
}
