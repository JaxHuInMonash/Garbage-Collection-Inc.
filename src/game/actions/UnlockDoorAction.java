package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Door;

/**
 * The bureaucratic process of asking a piece of the environment for permission to pass.
 * This action unlocks a specific door at a known location.
 *
 * @author Jax
 */
public class UnlockDoorAction extends Action {

    private final Door door;
    private final Location doorLocation;
    private final String direction;

    /**
     * Constructor.
     *
     * @param door         the Door to unlock
     * @param doorLocation the location of the door
     * @param direction    the direction of the door from the actor
     */
    public UnlockDoorAction(Door door, Location doorLocation, String direction) {
        this.door = door;
        this.doorLocation = doorLocation;
        this.direction = direction;
    }

    /**
     * Unlocks the target door.
     *
     * @param actor the actor performing the action
     * @param map   the map the actor is on
     * @return a description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        door.unlock();
        return actor + " unlocked the Door at " + doorLocation + " (" + direction + ")";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " unlocks Door (" + direction + ")";
    }
}
