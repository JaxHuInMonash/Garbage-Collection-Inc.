package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.InfectAction;
import game.capabilities.Infectable;

import java.util.List;

/**
 * A behaviour that makes a Parasite scan adjacent tiles for any
 * {@link Infectable} target (actor or ground item) and infect the first
 * one found.
 * <p>
 * The Parasite checks adjacent actors first (via {@code getActorAs}),
 * then ground items (via {@code getItemsAs}). If an infectable target
 * is found, it returns an {@link InfectAction}. The Parasite dies after
 * successfully infecting.
 *
 * @author Jax
 */
public class InfectBehaviour implements Behaviour<Actor, Action> {

    /**
     * Scans adjacent tiles for an {@link Infectable} target.
     * Prioritises actors over items. Returns an {@link InfectAction}
     * targeting the first infectable entity found, or null if none exist.
     *
     * @param actor    the Parasite looking to infect
     * @param location the current location of the Parasite
     * @return an InfectAction if an infectable target is adjacent, null otherwise
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();

            // Check if the adjacent actor is infectable
            Infectable infectableActor = adjacent.getActorAs(Infectable.class);
            if (infectableActor != null) {
                return new InfectAction(infectableActor, adjacent);
            }

            // Check ground items for infectable targets
            List<Infectable> infectableItems = adjacent.getItemsAs(Infectable.class);
            if (!infectableItems.isEmpty()) {
                return new InfectAction(infectableItems.get(0), adjacent);
            }
        }

        return null;
    }
}
