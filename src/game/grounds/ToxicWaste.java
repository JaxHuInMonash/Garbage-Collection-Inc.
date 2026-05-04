package game.grounds;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

public class ToxicWaste extends Ground {

    private static final int DAMAGE_PER_TURN = 1;

    public ToxicWaste() {
        super('≈', "Toxic Waste");
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.DECREASE, DAMAGE_PER_TURN);
        }
    }
}
