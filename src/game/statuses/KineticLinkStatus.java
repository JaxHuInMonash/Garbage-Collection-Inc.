package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A damage-sharing tether applied to a Worker by the Meeple's Kinetic Link action.
 * <p>
 * Damage is intercepted synchronously in the Worker's {@code hurt()} override
 * via {@link #share(int)}. Half the incoming damage (rounded down) is forwarded
 * to the linked Meeple; the Worker only suffers the remaining half. The link
 * expires when the Meeple dies.
 */
public class KineticLinkStatus implements Status {

    private final Actor meeple;

    /**
     * Constructor.
     *
     * @param meeple the Meeple that will absorb shared damage
     */
    public KineticLinkStatus(Actor meeple) {
        this.meeple = meeple;
    }

    /**
     * Splits incoming damage between the Worker and the linked Meeple.
     * Half (rounded down) is forwarded to the Meeple; the remainder is
     * returned for the Worker to take.
     *
     * @param damage incoming damage amount
     * @return the damage the Worker should actually suffer
     */
    public int share(int damage) {
        int forwarded = damage / 2;
        if (meeple.isConscious()) {
            meeple.hurt(forwarded);
        }
        return damage - forwarded;
    }

    /** No per-turn effect — sharing happens synchronously in {@code hurt()}. */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
    }

    @Override
    public boolean isStatusActive() {
        return meeple.isConscious();
    }
}
