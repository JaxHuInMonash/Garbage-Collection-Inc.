package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.actions.UseTeleportationTubeAction;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
/**
 * A teleportation device installed in armoured ships. Displays as {@code Φ}.
 * <p>
 * Supports multiple destination locations across maps. Using the tube burns
 * the surroundings of the destination for 2 turns. There is a 50% chance the
 * device malfunctions, teleporting the worker to a random location within the
 * destination map instead of the intended spot. The burning effect still occurs
 * at the random destination. If the destination is occupied by another actor,
 * the worker is automatically relocated to a random empty tile.
 * <p>
 * Must be added to the ship in both the 99-deprecated and 20-overflow maps.
 *
 *@author zhengjun Yang
 */
public class TeleportationTube extends Item {

    public record Destination(GameMap map, int x, int y, String label) {}

    private final List<Destination> destinations = new ArrayList<>();
    /**
     * Constructor.
     */
    public TeleportationTube() {
        super("Teleportation Tube", 'Φ');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(5));
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
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        for (int i = 0; i < destinations.size(); i++) {
            actions.add(new UseTeleportationTubeAction(this, i));
        }
        return actions;
    }
}
