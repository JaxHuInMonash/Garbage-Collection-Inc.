package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * The interface for any object that can be bought by an actor
 *
 * @author Tommy
 */
public interface Buyable {
    /**
     * A method that is called when the actor lacks
     * the funds to purchase the Sellable, allowing for
     * flexibility if the item has a fallback
     *
     * @author Tommy
     */
    default String onInsufficientCredits(Actor actor, GameMap map) {
        return ".";
    } // Used for FirstAidKit

    /**
     * Defines what happens when this Buyable is bought by an actor.
     * The implementation handles all effects (healing, poisoning, removal
     * from inventory, etc.).
     *
     * @param actor actor that bought the Buyable
     * @param map map the Buyable was bought on
     * @return resultant message of buying the Buyable
     */
    String onBuy(Actor actor, GameMap map);

    /**
     * Provides the value of the Buyable
     * @return integer value of the Buyable's buy value
     */
    int getBuyValue();
}
