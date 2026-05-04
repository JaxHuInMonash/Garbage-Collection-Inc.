package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

public class AluminiumDoor extends SecuredDoor {

    public AluminiumDoor() {
        super('=', "Aluminium Door");
    }

    @Override
    protected int requiredClearance() { return 1; }

    @Override
    public String unlockEffect(Actor actor, Location location) {
        actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, 2);
        return actor + " unlocked the Aluminium Door — electrical short-circuit deals 2 damage!";
    }
}