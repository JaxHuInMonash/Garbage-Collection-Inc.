package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A damage-sharing tether applied to a Worker by the Meeple's Kinetic Link action.
 * <p>
 * Each tick this status compares the Worker's HP against the previous tick.
 * If the Worker took damage, half of that damage (rounded down) is forwarded
 * to the linked Meeple. The link expires when the Meeple dies.
 */
public class KineticLinkStatus implements Status {

    private final Actor meeple;
    private int lastHp;
    private boolean initialised;

    /**
     * Constructor.
     *
     * @param meeple  the Meeple that will absorb shared damage
     * @param workerHp the Worker's current HP at the moment of tethering
     */
    public KineticLinkStatus(Actor meeple, int workerHp) {
        this.meeple = meeple;
        this.lastHp = workerHp;
        this.initialised = false;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        int currentHp = currEntity.getStatistic(ActorStatistics.HEALTH);
        if (!initialised) {
            lastHp = currentHp;
            initialised = true;
            return;
        }
        int damageTaken = lastHp - currentHp;
        if (damageTaken > 0 && meeple.isConscious()) {
            meeple.hurt(damageTaken / 2);
        }
        lastHp = currentHp;
    }

    @Override
    public boolean isStatusActive() {
        return meeple.isConscious();
    }
}
