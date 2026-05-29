package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Depositable;

import java.util.List;

/**
 * An action where a Scrap Snatcher removes every {@link Depositable} item from
 * the tile it currently occupies. The resources are permanently taken — they
 * are not added to the actor's inventory.
 * <p>
 * Any item on the ground that implements {@link Depositable} counts as a
 * resource that can be deposited at the Supercomputer, which is exactly what
 * the Snatcher targets.
 *
 * @author Ethan
 */
public class SnatchAction extends Action {

    private final List<Depositable> items;

    /**
     * @param items the depositable items to snatch from the current tile
     */
    public SnatchAction(List<Depositable> items) {
        this.items = items;
    }

    /**
     * Removes all listed depositable items from the actor's current location.
     * The cast to {@link Item} is safe because anything resting on a tile
     * is always an {@link Item}.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location location = map.locationOf(actor);
        StringBuilder sb = new StringBuilder();
        for (Depositable depositable : items) {
            location.removeItem((Item) depositable);
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(depositable);
        }
        return actor + " snatches: " + sb + "!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " snatches depositable resources";
    }
}
