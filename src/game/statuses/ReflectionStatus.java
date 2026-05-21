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
    private int pendingReflection;

    /**
     * Constructor.
     *
     * @param maxAbsorption total damage this shield reflects before expiring
     */
    public ReflectionStatus(int maxAbsorption) {
        this.remainingAbsorption = maxAbsorption;
        this.pendingReflection = 0;
    }

    /**
     * Absorbs as much of {@code damage} as possible, reducing the pool.
     * The absorbed amount is stored as pending reflection so that
     * {@link game.actions.AttackAction} can deal it back to the attacker.
     *
     * @param damage incoming damage amount
     * @return how much damage was absorbed (may be less than {@code damage})
     */
    public int absorb(int damage) {
        int absorbed = Math.min(damage, remainingAbsorption);
        remainingAbsorption -= absorbed;
        pendingReflection = absorbed;
        return absorbed;
    }

    /**
     * Returns and clears the pending reflected damage. Called by
     * {@link game.actions.AttackAction} after the attack resolves, so the
     * reflected damage can be applied to the attacker.
     *
     * @return the amount of damage to reflect back (0 if nothing pending)
     */
    public int consumePendingReflection() {
        int reflected = pendingReflection;
        pendingReflection = 0;
        return reflected;
    }

    /** No per-turn effect — absorption happens synchronously in {@code hurt()}. */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
    }

    /**
     * Active while absorption remains, or while reflected damage has not yet
     * been applied back to the attacker. This prevents the status from being
     * removed in {@code hurt()} before {@link game.actions.AttackAction} can
     * consume the pending reflection.
     */
    @Override
    public boolean isStatusActive() {
        return remainingAbsorption > 0 || pendingReflection > 0;
    }

    public int getRemainingAbsorption() {
        return remainingAbsorption;
    }

    @Override
    public String toString() {
        return "Reflection (" + remainingAbsorption + " dmg reflection remaining)";
    }
}
