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
 * Stage 2 (final) of the Warper Tree lifecycle. Displays as {@code W}.
 * <p>
 * A fully matured warper tree whose root system is deeply entangled with the
 * moon's unstable teleportation grid. Each turn, if a worker
 * ({@link Ability#ATTACKABLE_BY_CREATURES}) is on an adjacent tile, the tree's
 * roots grab them and warp them to a random valid location anywhere on the map
 * (single-threaded flora rule — only one warp per turn, targeting the first
 * worker found).
 * <p>
 * The warp destination is chosen from all map tiles the worker can normally
 * enter (i.e. {@code canActorEnter(worker)} returns true and no other actor
 * is already present). This guarantees the worker can continue moving after
 * being warped.
 * <p>
 * Actors cannot enter the mature warper tree tile itself.
 *
 * @author Ethan
 * @author Mary
 */
public class WarperTreeMature extends Ground {

    private static final Random RANDOM = new Random();

    /**
     * Constructor.
     */
    public WarperTreeMature() {
        super('W', "Warper Tree (Mature)");
    }

    /**
     * Actors cannot enter a mature warper tree tile.
     *
     * @param actor the Actor attempting to enter
     * @return false always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Each turn, warps the first adjacent worker to a random valid location
     * on the map. Only one warp is performed per turn.
     *
     * @param location the location of this mature warper tree
     */
    @Override
    public void tick(Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (destination.containsAnActor()
                    && destination.getActor().hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                warpWorker(destination.getActor(), location);
                return; // single-threaded flora rule — only one warp per turn
            }
        }
    }


    /**
     * Teleports the given worker to a random valid location on the map.
     * A valid destination is any tile the worker can enter that is not
     * already occupied by another actor. If no valid tile exists (extremely
     * unlikely), the warp is silently skipped.
     *
     * @param worker   the worker to warp
     * @param location the location of this tree (used to get the map)
     */
    private void warpWorker(Actor worker, Location location) {
        GameMap map = location.map();

        // Collect every tile on the map the worker can validly land on
        List<Location> validDestinations = new ArrayList<>();
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location candidate = map.at(x, y);

                if (candidate.canActorEnter(worker) && !candidate.containsAnActor())
                    validDestinations.add(candidate);
            }
        }

        if (!validDestinations.isEmpty())
            map.moveActor(worker, validDestinations.get(RANDOM.nextInt(validDestinations.size())));
    }
}
