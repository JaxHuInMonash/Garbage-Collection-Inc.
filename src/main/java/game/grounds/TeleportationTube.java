package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.UseTeleportationTubeAction;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
/**
 * A teleportation device installed in armoured ships. Displays as {@code Φ}.
 * <p>
 * Supports multiple destination locations across maps. When an actor stands
 * on the tube, they can choose a destination to teleport to. Using the tube
 * burns the surroundings of the destination for 2 turns. There is a 50% chance
 * the device malfunctions, teleporting the worker to a random location within
 * the destination map instead of the intended spot. The burning effect still
 * occurs at the random destination. If the destination is occupied by another
 * actor, the worker is automatically relocated to a random empty tile.
 * <p>
 * Installed as a fixed ground tile in the ships of both the 99-deprecated
 * and 20-overflow maps.
 *@author zhengjun Yang
 */
public class TeleportationTube extends Ground {

    public record Destination(GameMap map, int x, int y, String label) {}

    private final List<Destination> destinations = new ArrayList<>();

    /**
     * Constructor.
     */
    public TeleportationTube() {
        super('Φ', "Teleportation Tube");
    }

    /**
     * Registers a destination location for this tube.
     *
     * @param map   the destination map
     * @param x     the x coordinate of the destination
     * @param y     the y coordinate of the destination
     * @param label a human-readable label for the destination shown in the menu
     */
    public void addDestination(GameMap map, int x, int y, String label) {
        destinations.add(new Destination(map, x, y, label));
    }

    /**
     * Returns an unmodifiable view of the registered destinations.
     *
     * @return an unmodifiable list of destinations
     */
    public List<Destination> getDestinations() {
        return Collections.unmodifiableList(destinations);
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (direction.isEmpty()) {
            for (int i = 0; i < destinations.size(); i++) {
                actions.add(new UseTeleportationTubeAction(this, i));
            }
        }
        return actions;
    }
}