package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Ability;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The final stage of the Fleshy Tree lifecycle on the <em>99-Deprecated</em> map.
 * Displays as {@code H}.
 * <p>
 * The Fleshy Monolith cannot grow any further and does not spawn enemies.
 * Instead, each turn it acts as an involuntary teleporter: the first worker
 * ({@link Ability#ATTACKABLE_BY_CREATURES}) found on an adjacent tile is
 * violently warped to a random valid location elsewhere on the same map.
 * <p>
 * A valid warp destination is any tile the worker could normally enter that is
 * not already occupied by another actor. If no valid tile exists the warp is
 * silently skipped.
 * <p>
 * Actors cannot enter the monolith tile itself.
 *
 * @author Ethan
 */
public class FleshyMonolith extends Ground {

    private static final Random RANDOM = new Random();

    /** Constructor. */
    public FleshyMonolith() {
        super('H', "Fleshy Monolith");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn, warps the first adjacent worker to a random valid location
     * on the map. Only one warp is performed per turn.
     *
     * @param location the location of this monolith
     */
    @Override
    public void tick(Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()
                    && destination.getActor().hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                warpWorker(destination.getActor(), location);
                return;
            }
        }
    }

    /**
     * Teleports the worker to a random valid location on the same map.
     *
     * @param worker   the worker to warp
     * @param location the monolith's location (used to access the map)
     */
    private void warpWorker(Actor worker, Location location) {
        GameMap map = location.map();

        List<Location> validDestinations = new ArrayList<>();
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location candidate = map.at(x, y);
                if (candidate.canActorEnter(worker) && !candidate.containsAnActor()) {
                    validDestinations.add(candidate);
                }
            }
        }

        if (!validDestinations.isEmpty()) {
            map.moveActor(worker, validDestinations.get(RANDOM.nextInt(validDestinations.size())));
        }
    }
}
