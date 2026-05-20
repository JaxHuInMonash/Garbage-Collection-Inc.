package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A nanobot shield that absorbs damage before it reaches the bearer's HP.
 * <p>
 * Applied to Workers by the Meeple's GuardianState Nanobot Injection.
 * Damage is intercepted synchronously in the bearer's {@code hurt()} override
 * via {@link #absorb(int)}, consuming shield points until exhausted. This
 * avoids the one-tick delay of a tick-based approach — the Worker never sits
 * at reduced HP between damage and restoration.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class ShieldStatus implements Status {

    private int shieldPoints;

    /**
     * Constructor.
     *
     * @param shieldPoints total HP this shield can absorb
     */
    public ShieldStatus(int shieldPoints) {
        this.shieldPoints = shieldPoints;
    }

    /**
     * Absorbs as much of {@code damage} as possible, reducing the shield pool.
     *
     * @param damage incoming damage amount
     * @return how much damage was absorbed (may be less than {@code damage})
     */
    public int absorb(int damage) {
        int absorbed = Math.min(damage, shieldPoints);
        shieldPoints -= absorbed;
        return absorbed;
    }

    /** No per-turn effect — absorption happens synchronously in {@code hurt()}. */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
    }

    @Override
    public boolean isStatusActive() {
        return shieldPoints > 0;
    }

    public int getShieldPoints() {
        return shieldPoints;
    }

    @Override
    public String toString() {
        return "Shield (" + shieldPoints + " HP remaining)";
    }
}
