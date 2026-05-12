package game.grounds;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.capabilities.Ability;
/**
 * A permanently corrupted ground tile created when an Alien Cube is used.
 * Displays as {@code =}.
 * <p>
 * Any actor standing on Toxic Waste takes 1 point of damage per turn.
 * Unlike Fire, Toxic Waste is permanent and never reverts to its original ground.
 *
 * @author zhengjun Yang
 */
public class ToxicWaste extends Ground {

    private static final int DAMAGE_PER_TURN = 1;
    /**
     * Constructor.
     */
    public ToxicWaste() {
        super('≈', "Toxic Waste");
        this.enableAbility(Ability.IS_TOXIC);
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
