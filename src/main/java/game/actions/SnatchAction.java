package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Sellable;

import java.util.List;

/**
 * An action where a Scrap Snatcher removes every {@link Sellable} item from
 * the tile it currently occupies. The resources are permanently taken — they
 * are not added to the actor's inventory.
 * <p>
 * Any item on the ground that implements {@link Sellable} counts as a
 * resource that can be deposited at the Supercomputer, which is exactly what
 * the Snatcher targets.
 *
 * @author Ethan
 */
public class SnatchAction extends Action {

    private final List<Sellable> items;

    /**
     * @param items the sellable items to snatch from the current tile
     */
    public SnatchAction(List<Sellable> items) {
        this.items = items;
    }

    /**
     * Removes all listed sellable items from the actor's current location.
     * The cast to {@link Item} is safe because anything resting on a tile
     * is always an {@link Item}.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location location = map.locationOf(actor);
        StringBuilder sb = new StringBuilder();
        for (Sellable sellable : items) {
            location.removeItem((Item) sellable);
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(sellable);
        }
        return actor + " snatches: " + sb + "!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " snatches depositable resources";
    }
}
