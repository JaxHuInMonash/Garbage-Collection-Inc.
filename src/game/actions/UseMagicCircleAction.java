package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Flask;
import game.capabilities.Ability;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * An action that uses a {@link game.grounds.MagicCircle} to teleport the actor
 * to a random different Magic Circle within the current map.
 * <p>
 * Upon arrival, magical energy surges and spawns a {@link game.items.Flask}
 * on an empty tile directly adjacent to the destination circle.
 *@author zhengjun yang
 */
public class UseMagicCircleAction extends Action {

    private final Location circleLocation;
    private final String direction;
    private static final Random random = new Random();
    /**
     * Constructor.
     *
     * @param circleLocation the location of the Magic Circle being used
     * @param direction      the direction from the actor to the circle
     */
    public UseMagicCircleAction(Location circleLocation, String direction) {
        this.circleLocation = circleLocation;
        this.direction = direction;
    }
    /**
     * Teleports the actor to a random different Magic Circle on the current map
     * and spawns a Flask adjacent to the destination circle.
     *
     * @param actor the actor performing the action
     * @param map   the map the actor is currently on
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        List<Location> otherCircles = new ArrayList<>();
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location loc = map.at(x, y);
                if (loc.getGround().hasAbility(Ability.IS_MAGIC_CIRCLE) && !loc.equals(circleLocation)) {
                    otherCircles.add(loc);
                }
            }
        }

        if (otherCircles.isEmpty()) {
            return actor + " steps onto the Magic Circle, but there are no other circles to travel to...";
        }

        Location destination = otherCircles.get(random.nextInt(otherCircles.size()));
        map.moveActor(actor, destination);
        spawnFlaskAdjacent(destination);

        return actor + " teleports via Magic Circle to (" + destination.x() + ", " + destination.y()
                + ")! A Flask materialises nearby!";
    }
    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " steps through Magic Circle (" + direction + ")";
    }
    /**
     * Spawns a Flask on the first available empty adjacent tile next to
     * the given location.
     *
     * @param location the location to spawn the Flask next to
     */
    private void spawnFlaskAdjacent(Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            if (!adjacent.getGround().hasAbility(Ability.BLOCKS_ENTRY)
                    && !adjacent.containsAnActor()
                    && adjacent.getItems().isEmpty()) {
                adjacent.addItem(new Flask());
                return;
            }
        }
    }
}
