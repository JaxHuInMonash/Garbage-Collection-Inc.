package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Permanent passive status attached to a worker by the Phase Stabilizer
 * upgrade (Surveyor T3). Holds the regenerating "phase charge" that powers
 * the once-per-{@value #RECHARGE_TURNS}-turns wall walkthrough, and is the
 * marker that {@link game.grounds.WarperTreeMature} and
 * {@link game.grounds.MagicCircle} consult to decide whether to bypass /
 * despawn instead of triggering their normal effects.
 * <p>
 * The charge starts ready, is consumed by a successful
 * {@link game.actions.PhaseThroughWallAction}, and regenerates after
 * {@value #RECHARGE_TURNS} turns. The engine calls {@code tickStatus} once
 * per turn on actor statuses, driving the cool-down.
 *
 * @author Jax
 */
public class PhaseStabilizerStatus implements Status {

    /** Turns between successive phase charges. */
    public static final int RECHARGE_TURNS = 10;

    private boolean hasCharge = true;
    private int turnsUntilRecharge = 0;

    /** @return true if a wall-phase is currently available. */
    public boolean hasCharge() {
        return hasCharge;
    }

    /** Spend the charge; resets the cool-down. */
    public void consumeCharge() {
        hasCharge = false;
        turnsUntilRecharge = RECHARGE_TURNS;
    }

    /** @return remaining turns until the charge regenerates (0 if ready). */
    public int getTurnsUntilRecharge() {
        return turnsUntilRecharge;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (hasCharge) return;
        turnsUntilRecharge--;
        if (turnsUntilRecharge <= 0) {
            hasCharge = true;
        }
    }

    @Override
    public boolean isStatusActive() {
        return true; // permanent — installed by a permanent upgrade
    }

    @Override
    public String toString() {
        return hasCharge
                ? "Phase Stabilizer (charge ready)"
                : "Phase Stabilizer (recharging — " + turnsUntilRecharge + " turns)";
    }
}
