package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A damage-absorption shield that negates up to {@code maxAbsorption} total
 * incoming damage for the bearer and reflects the absorbed amount back at the
 * attacker.
 * <p>
 * Applied by the Swarmling's DormantState on entry. The Swarmling's
 * {@code hurt()} override calls {@link #absorb(int)} to consume incoming
 * damage and stage the reflection; {@link game.actions.AttackAction} then
 * delivers that staged amount back to the attacker via the
 * {@link PostHitFeedback#onTargetHit(Actor, Actor, GameMap)} hook.
 *
 * @author Ethan
 * @author Mary
 * @author Jax
 * @author Thomas
 */
public class ReflectionStatus implements PostHitFeedback {

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
     * Delivers any pending reflected damage back to the attacker. Implements
     * the {@link PostHitFeedback} contract so {@link game.actions.AttackAction}
     * can apply this generically alongside other post-hit effects.
     */
    @Override
    public String onTargetHit(Actor attacker, Actor victim, GameMap map) {
        int reflected = pendingReflection;
        pendingReflection = 0;
        if (reflected <= 0) {
            return "";
        }
        attacker.hurt(reflected);
        return victim + " reflects " + reflected + " damage back at " + attacker + "!";
    }

    /** No per-turn effect — absorption happens synchronously in {@code hurt()}. */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
    }

    /**
     * Active while absorption remains, or while reflected damage has not yet
     * been applied back to the attacker. This prevents the status from being
     * removed in {@code hurt()} before {@link game.actions.AttackAction} can
     * deliver the pending reflection.
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
