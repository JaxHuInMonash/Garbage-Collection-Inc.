package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

public class IronDoor extends SecuredDoor {

    public IronDoor() {
        super('N', "Iron Door");
    }

    @Override
    protected int requiredClearance() { return 2; }

    @Override
    public String unlockEffect(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location adjacent = exit.getDestination();
            Ground ground = adjacent.getGround();
            if (!(ground instanceof Wall) && !(ground instanceof SecuredDoor)) {
                adjacent.setGround(new Fire(ground, 2));
            }
        }
        return actor + " unlocked the Iron Door — machinery overheats, adjacent tiles catch fire!";
    }
}