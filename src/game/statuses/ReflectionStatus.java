package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A damage-absorption shield that negates up to {@code maxAbsorption} total
 * incoming damage for the bearer.
 * <p>
 * Applied by the Swarmling's DormantState on entry. The Swarmling's
 * {@code hurt()} override checks for this status before forwarding damage,
 * consuming absorption points until exhausted.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class ReflectionStatus implements Status {

    private int remainingAbsorption;

    /**
     * Constructor.
     *
     * @param maxAbsorption total damage this shield absorbs before expiring
     */
    public ReflectionStatus(int maxAbsorption) {
        this.remainingAbsorption = maxAbsorption;
    }

    /**
     * Absorbs as much of {@code damage} as possible, reducing the pool.
     *
     * @param damage incoming damage amount
     * @return how much damage was absorbed (may be less than {@code damage})
     */
    public int absorb(int damage) {
        int absorbed = Math.min(damage, remainingAbsorption);
        remainingAbsorption -= absorbed;
        return absorbed;
    }

    /** No per-turn effect — absorption happens synchronously in {@code hurt()}. */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
    }

    @Override
    public boolean isStatusActive() {
        return remainingAbsorption > 0;
    }

    public int getRemainingAbsorption() {
        return remainingAbsorption;
    }
}
