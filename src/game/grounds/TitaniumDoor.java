package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

public class TitaniumDoor extends SecuredDoor {

    public TitaniumDoor() {
        super('M', "Titanium Door");
    }

    @Override
    protected int requiredClearance() { return 3; }

    @Override
    public String unlockEffect(Actor actor, Location location) {
        actor.heal(5);
        return actor + " unlocked the Titanium Door — decontamination sequence heals 5 HP!";
    }
}
