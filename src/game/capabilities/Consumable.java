package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An interface for any item that can be consumed by an actor.
 * <p>
 * Each consumable defines its own effect in {@link #consumedBy(Actor, GameMap)},
 * which is called by the unified {@link game.actions.ConsumeAction}. This avoids
 * creating a separate Action subclass for every consumable item, following the
 * DRY and Open-Closed principles.
 *
 * @author Jax
 */
public interface Consumable {

    /**
     * Defines what happens when this item is consumed by an actor.
     * The implementation handles all effects (healing, poisoning, removal
     * from inventory, etc.).
     *
     * @param actor the actor consuming this item
     * @param map   the game map the actor is on
     * @return a description of the result of the consumption
     */
    String consumedBy(Actor actor, GameMap map);

    /**
     * Returns the menu description for the consume action on this item.
     *
     * @param actor the actor who may consume this item
     * @return a string to display in the action menu
     */
    String getConsumeMenuDescription(Actor actor);

    /**
     * Returns this consumable as an {@link Item}. All consumables in the game
     * are items, so this provides a safe way to obtain the Item reference
     * without using instanceof checks.
     *
     * @return this consumable as an Item
     */
    Item asItem();
}
