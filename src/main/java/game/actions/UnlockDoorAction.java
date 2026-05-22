package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.SecuredDoor;
/**
 * An action that unlocks a {@link game.grounds.SecuredDoor} when the actor
 * has sufficient clearance. The specific side effect of unlocking depends on
 * the concrete door type being unlocked.
 * <p>
 * This single action class handles all door types through the abstract
 * {@link game.grounds.SecuredDoor} class, following the DRY and
 * Dependency Inversion principles.
 *@author zhengjun Yang
 */
public class UnlockDoorAction extends Action {

    private final SecuredDoor door;
    private final Location doorLocation;
    private final String direction;
    /**
     * Constructor.
     *
     * @param door         the SecuredDoor to unlock
     * @param doorLocation the location of the door
     * @param direction    the direction of the door from the actor
     */
    public UnlockDoorAction(SecuredDoor door, Location doorLocation, String direction) {
        this.door = door;
        this.doorLocation = doorLocation;
        this.direction = direction;
    }
    /**
     * Unlocks the door and applies its unique side effect.
     *
     * @param actor the actor performing the action
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return door.unlock(actor, doorLocation);
    }
    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " unlocks " + door + " (" + direction + ")";
    }
}