package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;
import game.items.TeleportationTube;
import game.capabilities.Ability;
import java.util.List;
import java.util.Random;
/**
 * An action that teleports the actor to a chosen destination via a
 * {@link game.items.TeleportationTube}.
 * <p>
 * There is a 50% chance of malfunction, which causes the actor to be
 * teleported to a random passable location in the destination map instead
 * of the intended spot. Regardless of malfunction, the surroundings of the
 * destination are set on fire for 2 turns. If the destination is occupied
 * by another actor, a random empty location is chosen automatically.
 *
 *@author zhengjun yang
 */
public class UseTeleportationTubeAction extends Action {

    private final TeleportationTube tube;
    private final int destinationIndex;
    private static final Random random = new Random();
    /**
     * Constructor.
     *
     * @param tube             the TeleportationTube being used
     * @param destinationIndex the index of the chosen destination
     */
    public UseTeleportationTubeAction(TeleportationTube tube, int destinationIndex) {
        this.tube = tube;
        this.destinationIndex = destinationIndex;
    }
    /**
     * Teleports the actor to the chosen destination, handling malfunction,
     * occupied destinations, and burning surroundings.
     *
     * @param actor the actor performing the action
     * @param map   the map the actor is currently on
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        List<TeleportationTube.Destination> destinations = tube.getDestinations();
        TeleportationTube.Destination chosen = destinations.get(destinationIndex);

        GameMap destMap = chosen.map();
        Location destLocation;
        boolean malfunction = random.nextDouble() < 0.5;

        if (malfunction) {
            destLocation = getRandomPassableLocation(destMap);
        } else {
            destLocation = destMap.at(chosen.x(), chosen.y());
        }
        if (destLocation.containsAnActor()) {
            destLocation = getRandomPassableLocation(destMap);
        }
        burnSurroundings(destLocation);
        map.moveActor(actor, destLocation);

        String result = actor + " uses Teleportation Tube " + chosen.label();
        if (malfunction) {
            result += " — MALFUNCTION! Landed at random location!";
        }
        return result + " (surroundings catch fire!)";
    }
    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        List<TeleportationTube.Destination> destinations = tube.getDestinations();
        return actor + " uses Teleportation Tube → " + destinations.get(destinationIndex).label();
    }
    /**
     * Burns all adjacent non-wall, non-fire ground tiles at the given
     * location for 2 turns.
     *
     * @param location the centre location to burn around
     */
    private void burnSurroundings(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Ground ground = adjacent.getGround();
            if (!ground.hasAbility(Ability.BLOCKS_ENTRY) && !ground.hasAbility(Ability.ON_FIRE)) {
                adjacent.setGround(new Fire(ground, 2));
            }
        }
    }
    /**
     * Returns a random passable location in the given map that is not
     * occupied by another actor.
     *
     * @param destMap the destination map
     * @return a random passable, unoccupied location
     */
    private Location getRandomPassableLocation(GameMap destMap) {
        int width = destMap.getXRange().max() - destMap.getXRange().min() + 1;
        int height = destMap.getYRange().max() - destMap.getYRange().min() + 1;
        Location candidate;
        int attempts = 0;
        do {
            int x = destMap.getXRange().min() + random.nextInt(width);
            int y = destMap.getYRange().min() + random.nextInt(height);
            candidate = destMap.at(x, y);
            attempts++;
        } while ((candidate.getGround().hasAbility(Ability.BLOCKS_ENTRY) || candidate.containsAnActor()) && attempts < 100);
        return candidate;
    }
}
