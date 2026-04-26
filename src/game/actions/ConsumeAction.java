package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Consumable;

/**
 * A unified action for consuming any item that implements the {@link Consumable}
 * interface. Delegates the actual consumption effect to the item's
 * {@link Consumable#consumedBy(Actor, GameMap)} method.
 * <p>
 * This eliminates the need for a separate Action subclass per consumable item,
 * following the DRY principle.
 *
 * @author Jax
 */
public class ConsumeAction extends Action {

    private final Consumable consumable;

    /**
     * Constructor.
     *
     * @param consumable the Consumable item to be consumed
     */
    public ConsumeAction(Consumable consumable) {
        this.consumable = consumable;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        return consumable.consumedBy(actor, map);
    }

    @Override
    public String menuDescription(Actor actor) {
        return consumable.getConsumeMenuDescription(actor);
    }
}
