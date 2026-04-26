package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.capabilities.Consumable;

import java.util.List;

/**
 * A behaviour that makes an actor consume a {@link Consumable} item found
 * on the ground at its current location.
 * <p>
 * This reuses the existing {@link Consumable} interface and {@link ConsumeAction},
 * so the same consumption effects that apply to workers also apply to creatures
 * using this behaviour (e.g., poison from a rotten apple).
 * <p>
 * The item is removed from the ground before the {@link ConsumeAction} is returned,
 * since the item is not in any actor's inventory. The {@code consumedBy()} call
 * within the action may attempt {@code actor.getInventory().remove(this)}, which
 * is a harmless no-op when the item is not carried.
 *
 * @author Jax
 */
public class ConsumeItemBehaviour implements Behaviour<Actor, Action> {

    /**
     * Checks the current location for items that implement {@link Consumable}.
     * If one is found, removes it from the ground and returns a {@link ConsumeAction}.
     *
     * @param actor    the actor looking to consume
     * @param location the current location of the actor
     * @return a ConsumeAction for the first consumable item on the ground, or null
     */
    @Override
    public Action operate(Actor actor, Location location) {
        List<Consumable> consumables = location.getItemsAs(Consumable.class);

        if (!consumables.isEmpty()) {
            Consumable consumable = consumables.get(0);
            // Remove the item from the ground before consuming
            if (consumable instanceof Item) {
                location.removeItem((Item) consumable);
            }
            return new ConsumeAction(consumable);
        }

        return null;
    }
}
