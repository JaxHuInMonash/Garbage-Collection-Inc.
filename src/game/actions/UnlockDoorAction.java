package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.SecuredDoor;

public class UnlockDoorAction extends Action {

    private final SecuredDoor door;
    private final Location doorLocation;
    private final String direction;

    public UnlockDoorAction(SecuredDoor door, Location doorLocation, String direction) {
        this.door = door;
        this.doorLocation = doorLocation;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        return door.unlock(actor, doorLocation);
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " unlocks " + door + " (" + direction + ")";
    }
}