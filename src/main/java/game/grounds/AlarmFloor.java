package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.alarm.AlarmManager;
import game.capabilities.Ability;

/**
 * A concealed alarm tile disguised as regular floor. Displays as {@code _}.
 * <p>
 * When a worker (an actor with {@link Ability#ATTACKABLE_BY_CREATURES}) steps
 * onto this tile, the facility alarm is triggered via the {@link AlarmManager}.
 * The alarm can only fire once; after that, the tile remains inert.
 * <p>
 * This class depends on {@link AlarmManager} (the Observer pattern subject)
 * rather than on specific consequence classes, satisfying the Dependency
 * Inversion Principle. It does not know what happens when the alarm fires —
 * that is determined by whatever listeners are registered with the manager.
 *
 * @author Jax
 */
public class AlarmFloor extends Ground {

    /**
     * Constructor.
     */
    public AlarmFloor() {
        super('_', "Alarm Floor");
    }

    /**
     * Workers can enter this tile (it looks like normal floor).
     *
     * @param actor the Actor attempting to enter
     * @return true always
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Each turn, checks if a worker is standing on this tile. If so,
     * triggers the facility alarm via the {@link AlarmManager}.
     *
     * @param location the location of this tile
     */
    @Override
    public void tick(Location location) {
        if (!AlarmManager.getInstance().isAlarmTriggered() && location.containsAnActor()) {
            Actor actor = location.getActor();
            if (actor.hasAbility(Ability.ATTACKABLE_BY_CREATURES)) {
                AlarmManager.getInstance().triggerAlarm(actor, location.map());
            }
        }
    }
}
