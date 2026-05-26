package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.SnatchAction;
import game.capabilities.Sellable;

import java.util.List;

/**
 * A behaviour that makes a Scrap Snatcher steal any {@link Sellable} items
 * sitting on the tile it currently occupies — i.e. any resource that could
 * otherwise be deposited at the Supercomputer.
 * <p>
 * Given higher priority than {@link WanderBehaviour}, so the Snatcher always
 * clears resources from its tile before moving on.
 *
 * @author Ethan
 */
public class SnatchItemBehaviour implements Behaviour<Actor, Action> {

    /**
     * Returns a {@link SnatchAction} if the current tile holds any
     * {@link Sellable} items, otherwise returns {@code null}.
     */
    @Override
    public Action operate(Actor actor, Location location) {
        List<Sellable> sellables = location.getItemsAs(Sellable.class);
        if (!sellables.isEmpty()) {
            return new SnatchAction(sellables);
        }
        return null;
    }
}
