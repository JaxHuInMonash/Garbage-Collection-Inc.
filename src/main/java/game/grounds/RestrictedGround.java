package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.penalty.ZoneTrespassTrigger;

public class RestrictedGround extends Ground {
    private final ZoneTrespassTrigger trigger;

    public RestrictedGround(String zoneName) {
        super('R', "Restricted Zone");
        this.trigger = new ZoneTrespassTrigger(zoneName);
    }

    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            trigger.checkAndApply(actor, location.map());
        }
    }
}
