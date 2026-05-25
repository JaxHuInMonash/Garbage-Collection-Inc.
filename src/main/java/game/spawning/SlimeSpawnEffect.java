package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;

import java.util.ArrayList;

/**
 * Spawn effect for Slime: all workers within its adjacent tiles drop all
 * the items they are carrying in their inventory onto the ground.
 *
 * @author Jax
 */
public class SlimeSpawnEffect implements SpawnEffect {

    @Override
    public void apply(Actor creature, Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (adjacent.containsAnActor()) {
                Actor actor = adjacent.getActor();
                if (actor.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                    // Drop all items from the worker's inventory onto the ground
                    for (Item item : new ArrayList<>(actor.getInventory().getItems())) {
                        actor.getInventory().remove(item);
                        adjacent.addItem(item);
                    }
                }
            }
        }
    }
}
