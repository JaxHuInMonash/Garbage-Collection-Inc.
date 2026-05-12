package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.ToxicWaste;
import game.items.AlienCube;
import game.capabilities.Ability;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An action that uses an {@link AlienCube} to teleport the actor to a
 * randomly chosen passable location within the current map.
 * <p>
 * After teleportation, all adjacent ground tiles at the source location are
 * permanently corrupted into {@link ToxicWaste}, which deals 1 damage per
 * turn to any actor standing on it.
 *
 * @author zhengjun Yang
 */
public class UseAlienCubeAction extends Action {

    private final AlienCube cube;
    private static final Random random = new Random();

    /**
     * Constructor.
     *
     * @param cube the AlienCube being used
     */
    public UseAlienCubeAction(AlienCube cube) {
        this.cube = cube;
    }

    /**
     * Teleports the actor to a random passable location on the current map
     * and corrupts the source location's adjacent tiles into ToxicWaste.
     *
     * @param actor the actor performing the action
     * @param map   the map the actor is currently on
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location source = map.locationOf(actor);
        Location target = getRandomPassableLocation(map);

        if (target == null) {
            return actor + " tries to use the Alien Cube, but there is nowhere to teleport!";
        }

        corruptSurroundings(source);
        map.moveActor(actor, target);
        return actor + " uses the Alien Cube! Source corrupted into Toxic Waste. Teleported to ("
                + target.x() + ", " + target.y() + ")!";
    }

    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses Alien Cube (teleport to random location)";
    }

    /**
     * Returns a random passable, unoccupied location in the given map.
     *
     * @param map the current map
     * @return a random passable location, or null if none available
     */
    private Location getRandomPassableLocation(GameMap map) {
        List<Location> candidates = new ArrayList<>();
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location loc = map.at(x, y);
                if (!loc.getGround().hasAbility(Ability.BLOCKS_ENTRY) && !loc.containsAnActor()) {
                    candidates.add(loc);
                }
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    /**
     * Turns all adjacent non-wall, non-toxic ground tiles at the given
     * location into ToxicWaste.
     *
     * @param location the source location to corrupt around
     */
    private void corruptSurroundings(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Ground ground = adjacent.getGround();
            if (!ground.hasAbility(Ability.BLOCKS_ENTRY) && !ground.hasAbility(Ability.IS_TOXIC)) {
                adjacent.setGround(new ToxicWaste());
            }
        }
    }
}
