package game.statuses;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Combat Tier 1 — Retaliation passive. Installed permanently on a worker when
 * they take {@code CombatConditioning}.
 * <p>
 * Implements {@link PostHitFeedback}: each time the worker is hit by an
 * {@link game.actions.AttackAction}, this status rolls its trigger chance and,
 * on success, applies a {@link BurnStatus} to the attacker. Lives forever
 * (the upgrade is permanent), so {@link #isStatusActive()} always returns true.
 * <p>
 * No damage absorption — the worker still takes the full hit. Retaliation is
 * pure counter-pressure, not defence.
 *
 * @author Jax
 */
public class RetaliationStatus implements PostHitFeedback {

    private final int chancePercent;
    private final int burnDamage;
    private final int burnDuration;
    private final Random random;

    /**
     * @param chancePercent  percentage chance to trigger on each hit (0–100)
     * @param burnDamage     damage-per-turn of the inflicted {@link BurnStatus}
     * @param burnDuration   number of turns the burn lasts
     */
    public RetaliationStatus(int chancePercent, int burnDamage, int burnDuration) {
        this.chancePercent = chancePercent;
        this.burnDamage = burnDamage;
        this.burnDuration = burnDuration;
        this.random = new Random();
    }

    @Override
    public String onTargetHit(Actor attacker, Actor victim, GameMap map) {
        if (random.nextInt(100) >= chancePercent) {
            return "";
        }
        attacker.addStatus(new BurnStatus(burnDamage, burnDuration));
        return victim + " retaliates — " + attacker + " catches fire ("
                + burnDamage + " dmg/turn for " + burnDuration + " turns)!";
    }

    /** Permanent passive; never ticks. */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
    }

    /** Permanent — the upgrade is a permanent installation, not a buff. */
    @Override
    public boolean isStatusActive() {
        return true;
    }

    @Override
    public String toString() {
        return "Retaliation (" + chancePercent + "% burn on hit, "
                + burnDamage + " dmg/turn × " + burnDuration + ")";
    }
}
