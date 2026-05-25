package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Transmutable;
import game.items.RefinedMaterial;

import java.util.List;

/**
 * The Meeple's EngineerState unique action.
 * <p>
 * Locates the first {@link Transmutable} item at the target tile via
 * {@code location.getItemsAs(Transmutable.class)}
 * removes it, and replaces it with {@link RefinedMaterial} that Workers can
 * consume for a permanent +20 max HP.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class TransmuteAction extends Action {

    private final Location targetLocation;

    /**
     * Constructor.
     *
     * @param targetLocation the tile holding the Transmutable item
     */
    public TransmuteAction(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        List<Transmutable> found = targetLocation.getItemsAs(Transmutable.class);
        if (found.isEmpty()) {
            return actor + " finds nothing to transmute.";
        }
        // All Transmutable items are Items — the cast is guaranteed by the type hierarchy
        Item item = (Item) found.get(0);
        targetLocation.removeItem(item);
        targetLocation.addItem(new RefinedMaterial());
        return actor + " transmutes the remains into Refined Material.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " transmutes remains into Refined Material";
    }
}
